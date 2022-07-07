package com.scgts.sctrace.rack_transfer.transferselection

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.base.model.TextEntry
import com.scgts.sctrace.base.util.formatTally
import com.scgts.sctrace.base.util.toUiModels
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination
import com.scgts.sctrace.rack_transfer.R
import com.scgts.sctrace.rack_transfer.transferselection.RackTransferSelectionMvi.Intent
import com.scgts.sctrace.rack_transfer.transferselection.RackTransferSelectionMvi.Intent.*
import com.scgts.sctrace.rack_transfer.transferselection.RackTransferSelectionMvi.ViewState
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.RackTransferCache
import util.sendErrorToDtrace

class RackTransferSelectionViewModel(
    private val taskId: String,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
    private val settingsManager: SettingsManager,
    private val rackTransferCache: RackTransferCache,
) : ViewModel(), MviViewModel<Intent, ViewState> {
    private val initialState = Supplier { ViewState() }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
            .distinctUntilChanged()
            .doOnError { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
    }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is Assets -> prev.copy(
                assetsList = intent.assetsList,
                createEnabled = intent.assetsList.isNotEmpty() && prev.selectedRackLocation != null
            )
            is RackLocations -> prev.copy(rackLocations = intent.locations)
            is SetSelectedRackLocation -> prev.copy(
                selectedRackLocation = intent.rackLocation ?: RackLocation("", "Choose Location"),
                createEnabled = prev.assetsList.isNotEmpty() && intent.rackLocation != null
            )
            is SetSummaryList -> prev.copy(summaryList = intent.summaryList)
            else -> prev
        }
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {

            dataIntent(tasksRepository.getTask(taskId)) {
                it.flatMapObservable { task ->
                    tasksRepository.getProjectRackLocations(task.projectId)
                        .flatMapObservable { rackLocations ->
                            Observable.just(RackLocations(rackLocations))
                        }
                }
            }

            dataIntent(rackTransferCache.getObservable()) {
                it.flatMap { rackTransferCache ->
                    rackTransferCache.rackLocationId?.let { rackLocationId ->
                        tasksRepository.getRackLocationById(rackLocationId)
                            .flatMapObservable { rackLocation ->
                                Observable.just(SetSelectedRackLocation(rackLocation))
                            }
                    } ?: run { Observable.just(SetSelectedRackLocation(null)) }
                }
            }

            dataIntent(rackTransferCache.getObservable()) {
                it.flatMap { rackTransferAsset ->
                    tasksRepository.getAssets(rackTransferAsset.assetIds.toList())
                        .flatMapObservable { assets ->
                            Observable.just(Assets(assets.toUiModels()))
                        }
                }
            }

            dataIntent(rackTransferCache.getObservable()) {
                it.flatMap { rackTransferAsset ->
                    Observable.combineLatest(
                        tasksRepository.getTalliesForAssets(rackTransferAsset.assetIds.toList()),
                        settingsManager.unitType(),
                        { tallies, unitType ->
                            SetSummaryList(
                                listOf(
                                    TextEntry(
                                        label = R.string.total_tally,
                                        body = formatTally(
                                            expectedLength = tallies.total,
                                            numJoints = tallies.totalJoints,
                                            unitType = unitType,
                                            decimalPlaces = 2
                                        ),
                                    )
                                )
                            )
                        },
                    )
                }
            }

            viewIntentCompletable<CreateClicked> {
                it.flatMapCompletable {
                    rackTransferCache.get().flatMapCompletable { rackTransferCache ->
                        rackTransferCache.rackLocationId?.let { rackLocationId ->
                            tasksRepository.createRackTransferTraceEvents(
                                assetIds = rackTransferCache.assetIds.toList(),
                                taskId = taskId,
                                rackLocationId = rackLocationId,
                            )
                        } ?: kotlin.run { Completable.complete() }
                    }
                        .andThen(clearCache())
                        .andThen(navigator.popBackStack())
                }
            }

            viewIntentCompletable<ScanClicked> {
                it.flatMapCompletable {
                    tasksRepository.getTask(taskId).flatMapCompletable { task ->
                        navigator.navigate(
                            NavDestination.NavDestinationArgs.Capture(task.projectId, task.id)
                        )
                    }
                }
            }

            viewIntentCompletable<RackLocationSelected> {
                it.flatMapCompletable { intent ->
                    rackTransferCache.edit {
                        rackLocationId = intent.rackLocation?.id
                        this
                    }
                }
            }

            viewIntentCompletable<OnAssetDeleteClicked> {
                it.flatMapCompletable { intent ->
                    val cache = rackTransferCache.get().blockingGet()
                    val newSet = cache.assetIds.filter { id -> id != intent.id }.toMutableSet()
                    rackTransferCache.edit {
                        assetIds = newSet
                        this
                    }
                }
            }

            viewIntentCompletable<CloseClicked> {
                it.flatMapCompletable {
                    navigator.popBackStack().andThen(clearCache())
                }
            }
        }

    private fun clearCache(): Completable {
        return rackTransferCache.edit {
            rackLocationId = null
            assetIds = mutableSetOf()
            toDeleteAssets = mutableSetOf()
            this
        }
    }
}