package com.scgts.sctrace.assets.consumption

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.assets.consumption.ConsumptionIntent.Intent
import com.scgts.sctrace.assets.consumption.ConsumptionIntent.Intent.*
import com.scgts.sctrace.assets.consumption.ConsumptionIntent.ViewState
import com.scgts.sctrace.base.model.Reason
import com.scgts.sctrace.base.model.TaskType.AD_HOC_REJECT_SCAN
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType.ConsumedToRejected
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import util.ScanStateManager
import util.TopToastManager

class ConsumptionViewModel(
    private val assetId: String,
    private val taskId: String,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
    private val statusChange: String?,
    private val scanStateManager: ScanStateManager,
    private val topToastManager: TopToastManager,
    private val quickReject: Boolean = false,
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val inputCache = InMemoryObjectCache<Pair<Reason?, String>>(Pair(null, ""))

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(
                { ViewState(statusChange = statusChange?.let { ConsumptionSwitchType.valueOf(it) }) },
                { prev, intent ->
                    when (intent) {
                        is AssetData -> prev.copy(pipeNo = intent.pipeNo)
                        is InputUpdated -> prev.copy(reason = intent.reason)
                        else -> prev
                    }
                }
            )
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {
        dataIntent(tasksRepository.getAsset(assetId)) {
            it.map<Intent> { asset -> AssetData(asset.pipeNumber) }.toObservable()
        }

        dataIntent(inputCache.getObservable()) {
            it.map { (reason, _) -> InputUpdated(reason?.uiName) }
        }

        viewIntentCompletable<ReasonSelected> {
            it.flatMapCompletable { inputCache.edit { copy(first = it.reason) } }
        }

        viewIntentCompletable<CommentUpdated> {
            it.flatMapCompletable { inputCache.edit { copy(second = it.comment) } }
        }

        viewIntentCompletable<DiscardClick> {
            it.flatMapCompletable {
                scanStateManager.setScanning()
                    .andThen(topToastManager.setShowAssetAddedToastFlag(false))
                    .andThen(navigator.popBackStack())
            }
        }

        viewIntentCompletable<RejectClick> {
            it.flatMapCompletable {
                inputCache.get().flatMapCompletable { (reason, comment) ->
                    if (quickReject) {
                        addConsumptionTraceEvent(
                            reason = reason?.uiName,
                            comment = comment,
                            quickReject = true,
                        ).andThen(tasksRepository.addToQueue(taskId, assetId))
                    } else {
                        tasksRepository.getTask(taskId).flatMapCompletable { task ->
                            addConsumptionTraceEvent(
                                reason = reason?.uiName,
                                comment = comment,
                                locationId = task.toLocationId!!,
                            )
                        }
                    }
                        .andThen(topToastManager.showAssetAddedToast())
                        .andThen(scanStateManager.setScanning())
                        .andThen(navigator.popBackStack())
                }
            }
        }
    }

    private fun addConsumptionTraceEvent(
        reason: String?,
        comment: String?,
        locationId: String? = null,
        quickReject: Boolean = false,
    ): Completable {
        val statusChange = statusChange?.let { ConsumptionSwitchType.valueOf(it) }
        return tasksRepository.addTraceEvent(
            taskId = taskId,
            assetId = assetId,
            facilityId = locationId ?: "",
            consumed = !(statusChange == null || statusChange == ConsumedToRejected),
            rejectReason = reason,
            rejectComment = comment,
            adHocActionTaskType = if (quickReject) AD_HOC_REJECT_SCAN.serverName else null,
        )
    }
}