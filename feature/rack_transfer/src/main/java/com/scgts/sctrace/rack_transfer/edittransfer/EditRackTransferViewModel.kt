package com.scgts.sctrace.rack_transfer.edittransfer

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.base.model.TextEntry
import com.scgts.sctrace.base.util.formatTally
import com.scgts.sctrace.base.util.toUiModels
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import com.scgts.sctrace.rack_transfer.R
import com.scgts.sctrace.rack_transfer.edittransfer.EditRackTransferMvi.Intent
import com.scgts.sctrace.rack_transfer.edittransfer.EditRackTransferMvi.ViewState
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.RackTransfer
import util.sendErrorToDtrace

class EditRackTransferViewModel(
    private val taskId: String,
    private val rackId: String,
    private val productDescription: String,
    private val millWorkNo: String,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
    private val settingsManager: SettingsManager,
) : ViewModel(),
    MviViewModel<Intent, ViewState> {

    private val rackTransferAssetCache =  InMemoryObjectCache(RackTransfer())
    private val initialState = Supplier { ViewState() }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
            .distinctUntilChanged()
            .doOnError { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
    }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is Intent.Assets -> prev.copy(
                assetsList = intent.rackTransferAssetList,
            )
            is Intent.RackLocations -> prev.copy(rackLocations = intent.locations)
            is Intent.UpdateRackLocation -> prev.copy(
                selectedRackLocation = intent.rackLocation ?: RackLocation(
                    id = "",
                    name = "Choose location"
                )
            )
            is Intent.SetSummaryList -> prev.copy(summaryList = intent.summaryList)
            else -> prev
        }
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {

            dataIntent(tasksRepository.getTask(taskId)) {
                it.flatMapObservable { task ->
                    tasksRepository.getProjectRackLocations(task.projectId)
                        .flatMapObservable { rackLocations ->
                            Observable.just(
                                Intent.RackLocations(
                                    rackLocations
                                )
                            )
                        }
                }
            }

            dataIntent(
               rackTransferAssetCache.edit {
                   rackLocationId = rackId
                   this
               }
            )

            dataIntent(
                tasksRepository.getRackTransferAssetsForTraceEvent(
                    taskId,
                    rackId,
                    millWorkNo,
                    productDescription
                )
            ) {
                it.flatMapCompletable { assets ->
                    val ids = assets.map { asset -> asset.id }.toMutableSet()
                    rackTransferAssetCache.edit {
                        assetIds = ids
                        this
                    }
                }.toObservable()
            }

            dataIntent(rackTransferAssetCache.getObservable()) {
                it.flatMap { rackTransferAsset ->
                    Observable.combineLatest(
                        tasksRepository.getTalliesForAssets(rackTransferAsset.assetIds.toList()),
                        settingsManager.unitType(),
                        { tallies, unitType ->
                            Intent.SetSummaryList(
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

            dataIntent(rackTransferAssetCache.getObservable()) {
                it.flatMap { rackTransferAsset ->
                    tasksRepository.getAssets(rackTransferAsset.assetIds.toList())
                        .flatMapObservable { assets ->
                            Observable.just(Intent.Assets(assets.toUiModels()))
                        }
                }
            }

            dataIntent(rackTransferAssetCache.getObservable()) {
                it.flatMap { rackTransferCache ->
                    rackTransferCache.rackLocationId?.let { rackLocationId ->
                        tasksRepository.getRackLocationById(rackLocationId)
                            .flatMapObservable { rackLocation ->
                                Observable.just(
                                    Intent.UpdateRackLocation(
                                        rackLocation
                                    )
                                )
                            }
                    } ?: run {
                        Observable.just(
                            Intent.UpdateRackLocation(
                                null
                            )
                        )
                    }
                }
            }

            viewIntentCompletable<Intent.SaveClicked> {
                it.flatMapCompletable {
                    rackTransferAssetCache.get().flatMapCompletable { rackTransferCache ->
                        rackTransferCache.rackLocationId?.let { rackLocationId ->
                            tasksRepository.createRackTransferTraceEvents(
                                assetIds = rackTransferCache.assetIds.toList(),
                                taskId = taskId,
                                rackLocationId = rackLocationId,
                            )
                        } ?: kotlin.run { Completable.complete() }
                    }
                        .andThen(tasksRepository.deleteTraceEvents(rackTransferAssetCache.get()
                                .blockingGet().toDeleteAssets.toList(), taskId))
                        .andThen(clearCache())
                        .andThen(navigator.popBackStack())
                }
            }

            viewIntentCompletable<Intent.RackLocationSelected> {
                it.flatMapCompletable { intent ->
                    rackTransferAssetCache.edit {
                        rackLocationId = intent.rackLocation?.id
                        this
                    }
                }
            }

            viewIntentCompletable<Intent.OnAssetDeleteClicked> {
                it.flatMapCompletable { intent ->
                    val cache = rackTransferAssetCache.get().blockingGet()
                    val newSet = cache.assetIds.filter { id -> id != intent.id }.toMutableSet()
                    val oldDeleted = cache.toDeleteAssets
                    oldDeleted.add(intent.id)
                    rackTransferAssetCache.edit {
                        assetIds = newSet
                        toDeleteAssets = oldDeleted
                        this
                    }
                }
            }

            viewIntentCompletable<Intent.CloseClicked> {
                it.flatMapCompletable {
                    navigator.popBackStack().andThen(clearCache())
                }
            }
        }

    private fun clearCache(): Completable {
        return rackTransferAssetCache.edit {
            rackLocationId = null
            assetIds = mutableSetOf()
            toDeleteAssets = mutableSetOf()
            this
        }
    }

}