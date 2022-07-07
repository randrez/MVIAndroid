package com.scgts.sctrace.assets.confirmation.discard

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.assets.confirmation.discard.AssetDiscardConfirmationMvi.Intent
import com.scgts.sctrace.assets.confirmation.discard.AssetDiscardConfirmationMvi.Intent.*
import com.scgts.sctrace.assets.confirmation.discard.AssetDiscardConfirmationMvi.ViewState
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import util.RackTransferCache
import util.ScanStateManager

class AssetDiscardConfirmationViewModel(
    private val assetId: String,
    private val taskId: String,
    private val tasksRepository: TasksRepository,
    private val scanStateManager: ScanStateManager,
    private val rackTransferCache: RackTransferCache,
) : ViewModel(), MviViewModel<Intent, ViewState> {
    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(
                {
                    ViewState()
                },
                { prev, intent ->
                    when (intent) {
                        CancelDiscardClick, Dismiss -> prev.copy(dismiss = true)
                        else -> prev
                    }
                }
            )
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {
        dataIntent(scanStateManager.setPause())

        viewIntentObservable<ConfirmDiscardClick> {
            it.flatMap {
                tasksRepository.getTask(taskId = taskId)
                    .flatMapObservable { task ->
                        tasksRepository.removeCapturedAsset(taskId, assetId)
                            .andThen(
                                if (task.type == RACK_TRANSFER) {
                                    rackTransferCache.edit {
                                        assetIds.remove(assetId)
                                        this
                                    }
                                } else {
                                    Completable.complete()
                                })
                            .andThen(scanStateManager.setScanning())
                            .andThen(Observable.just(Dismiss))
                    }
            }
        }

        viewIntentCompletable<OnDismiss> {
            it.flatMapCompletable { scanStateManager.setScanning() }
        }

        viewIntentPassThroughs(CancelDiscardClick::class)
    }
}
