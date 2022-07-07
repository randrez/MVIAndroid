package com.scgts.sctrace.rack_transfer.rackdetails

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.util.toUiModels
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination
import com.scgts.sctrace.rack_transfer.rackdetails.RackDetailsMvi.Intent
import com.scgts.sctrace.rack_transfer.rackdetails.RackDetailsMvi.ViewState
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.sendErrorToDtrace

class RackDetailsViewModel(
    private val taskId: String,
    private val rackId: String,
    private val productDescription: String,
    private val millWorkNo: String,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
    private val settingsManager: SettingsManager,
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
            is Intent.Assets -> prev.copy(assets = intent.assets)
            is Intent.RackTransferData -> prev.copy(assetsRackTransfer = intent.assetsRackTransfer)
            else -> prev
        }
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {
            dataIntent(
                tasksRepository.getRackTransferAssets(
                    taskId,
                    settingsManager.unitType().blockingFirst()
                )
            ) {
                it.map { rackTransferList ->
                    rackTransferList.find { model ->
                        model.rackLocationId == rackId &&
                                model.millWorkNum == millWorkNo &&
                                model.productDescription == productDescription
                    }?.let { rack -> Intent.RackTransferData(rack) } ?: Intent.NoOp
                }
            }

            dataIntent(tasksRepository.getRackTransferAssetsForTraceEvent(taskId,
                rackId, millWorkNo, productDescription)) {
                it.flatMapObservable { assets ->
                    Observable.just(Intent.Assets(assets.toUiModels()))
                }
            }

            viewIntentCompletable<Intent.AssetClicked> {
                it.flatMapCompletable { intent ->
                    navigator.navigate(
                        NavDestination.NavDestinationArgs.ShowAssetDetails(
                            assetId = intent.assetId,
                            taskId = taskId
                        )
                    )
                }
            }

            viewIntentCompletable<Intent.BackClicked> {
                it.flatMapCompletable {
                    navigator.popBackStack()
                }
            }
        }
}