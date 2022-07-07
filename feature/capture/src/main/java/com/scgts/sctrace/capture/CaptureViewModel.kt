package com.scgts.sctrace.capture

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.base.model.CurrentTask
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.capture.CaptureMvi.Intent
import com.scgts.sctrace.capture.CaptureMvi.Intent.*
import com.scgts.sctrace.capture.CaptureMvi.ViewState
import com.scgts.sctrace.capture.CaptureUseCase.EndResult
import com.scgts.sctrace.capture.tag_conflict.ConflictIdsCache
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.queue.QueueRepository
import com.scgts.sctrace.tasks.TasksRepository
import com.scgts.sctrace.base.model.TaskType
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import io.reactivex.rxjava3.kotlin.flatMapIterable
import util.*
import util.CaptureMode.Consumption.Consume
import util.CaptureMode.Consumption.Reject

class CaptureViewModel(
    private val currentTask: CurrentTask,
    private val captureModeManager: CaptureModeManager,
    private val settingsManager: SettingsManager,
    private val tasksRepository: TasksRepository,
    private val queueRepository: QueueRepository,
    private val topToastManager: TopToastManager,
    private val navigator: AppNavigator,
    private val conflictIdsCache: ConflictIdsCache,
    private val captureUseCase: CaptureUseCase,
    private val captureMethodManager: CaptureMethodManager,
    private val scanStateManager: ScanStateManager,
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val tagActions = listOf(
        CaptureMethod.Camera,
//        CaptureMethod.Laser,
//        CaptureMethod.Rfid,
    )
    private val assetActions = mutableListOf<CaptureMethod>().apply {
        addAll(tagActions)
        add(CaptureMethod.Manual)
    }.toList()

    private val initialState =
        Supplier {
            ViewState(selectedCaptureMethod = settingsManager.captureMethod().blockingFirst())
        }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is CaptureMethodButtonClicked -> prev.copy(showCaptureMethodOptions = !prev.showCaptureMethodOptions)
            is SetCaptureMethod -> prev.copy(
                showCaptureMethodOptions = false,
                selectedCaptureMethod = intent.captureMethod
            )
            is SetToolbar -> prev.copy(
                captureMethods = intent.actions,
                screenTitle = intent.title,
            )
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents).scanWith(initialState, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {
        dataIntent(captureMethodManager.resetMethodToDefault())

        if (currentTask.id != null) {
            when {
                currentTask.quickReject -> {
                    dataIntent(captureModeManager.setReject(quickReject = true))
                }

                else -> {
                    dataIntent(tasksRepository.getTask(currentTask.id as String)) {
                        it.flatMapCompletable { task ->
                            when {
                                task.type == TaskType.CONSUME -> {
                                    captureModeManager.setConsume()
                                }
                                else -> {
                                    captureModeManager.setAsset()
                                }
                            }
                        }.toObservable()
                    }
                }
            }
        } else {
            dataIntent(captureModeManager.setAsset())
        }

        dataIntent(settingsManager.captureMethod()) {
            it.flatMapCompletable { captureMethod ->
                captureMethodManager.setCaptureMethod(captureMethod)
            }.toObservable()
        }

        dataIntent(captureMethodManager.captureMethodObs()) {
            it.flatMap { captureMethod ->
                when (captureMethod) {
                    CaptureMethod.Camera -> scanStateManager.setScanning()
                    CaptureMethod.Manual -> scanStateManager.setPause()
                    else -> Completable.complete()
                }.andThen(Observable.just(SetCaptureMethod(captureMethod)))
            }
        }

        dataIntent(captureModeManager.stateObs()) {
            it.map { captureMode ->
                if (captureMode is CaptureMode.Tags) SetToolbar(tagActions, R.string.add_tag)
                else SetToolbar(assetActions, R.string.capture_screen_title)
            }
        }

        dataIntent(captureModeManager.stateObs()) {
            it.flatMapCompletable { captureMode ->
                if (captureMode is CaptureMode.Tags) captureMethodManager.enterTagMode()
                else Completable.complete()
            }.toObservable()
        }

        dataIntent(conflictIdsCache.assetSelectionObs()) {
            it.flatMap { (selectedAsset, tag) ->
                captureUseCase.handleCapture(selectedAsset.id, tag).flatMap { endResult ->
                    when (endResult) {
                        EndResult.Continue -> Observable.just(NoOp)
                        EndResult.ConsumptionDuplicate ->
                            captureModeManager.setShowConsumptionDuplicate(Consume).toObservable()
                        EndResult.RejectDuplicate ->
                            captureModeManager.setShowConsumptionDuplicate(Reject()).toObservable()
                    }
                }
            }
        }

        dataIntent(conflictIdsCache.assetSelectionObs()) {
            it.flatMap { (selectedAsset, tag) ->
                conflictIdsCache.getObservable().flatMapIterable().flatMap { asset ->
                    if (asset.id != selectedAsset.id) tasksRepository.updateAssetTagList(
                        assetId = asset.id,
                        tags = asset.tags.filter { assetTag -> assetTag != tag }
                    ).andThen(queueRepository.addDeleteTagToMiscQueue(asset.id, tag))
                        .toObservable()
                    else Observable.just(NoOp)
                }
            }
        }

        viewIntentCompletable<CaptureMethodSelected> {
            it.flatMapCompletable { intent ->
                captureMethodManager.setCaptureMethod(intent.captureMethod)
            }
        }

        viewIntentCompletable<Exit> {
            it.flatMapCompletable {
                navigator.popBackStack().andThen(topToastManager.hideAssetAddedToast())
            }
        }

        viewIntentPassThroughs(CaptureMethodButtonClicked::class)
    }
}
