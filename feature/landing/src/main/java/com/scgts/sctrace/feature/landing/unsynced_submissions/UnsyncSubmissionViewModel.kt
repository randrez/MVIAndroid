package com.scgts.sctrace.feature.landing.unsynced_submissions

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.TaskType.AD_HOC_REJECT_SCAN
import com.scgts.sctrace.base.model.TaskType.AD_HOC_QUICK_SCAN
import com.scgts.sctrace.base.util.formatTally
import com.scgts.sctrace.feature.landing.unsynced_submissions.UnsyncedSubmissionMvi.Intent
import com.scgts.sctrace.feature.landing.unsynced_submissions.UnsyncedSubmissionMvi.Intent.SetUnsyncSubmissions
import com.scgts.sctrace.feature.landing.unsynced_submissions.UnsyncedSubmissionMvi.ViewState
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.queue.QueueRepository
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class UnsyncSubmissionViewModel(
    private val tasksRepository: TasksRepository,
    private val appNavigator: AppNavigator,
    private val queueRepository: QueueRepository
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val SCAN_TIME = "Scan time"
    private val SUBMIT_DATE = "Submit date"
    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is SetUnsyncSubmissions -> {
                val newList = prev.unsyncedSubmissionList.plus(intent.unsyncedSubmissionList)
                prev.copy(unsyncedSubmissionList = newList)
            }
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith({ ViewState() }, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {
            dataIntent(tasksRepository.getPendingTraceEvents()) {
                it.flatMapSingle { traceEvents ->
                    if (traceEvents.isEmpty()) {
                        return@flatMapSingle Single.just(
                            Triple(
                                listOf<Asset>(),
                                listOf<Task>(),
                                listOf<TraceEvent>()
                            )
                        )
                    }
                    val distinctAssetIds = traceEvents
                        .distinctBy { traceEvent -> traceEvent.assetId }
                        .map { traceEvent -> traceEvent.assetId }
                    val distinctTaskIds = traceEvents
                        .distinctBy { traceEvent -> traceEvent.taskId }
                        .filter { traceEvent ->
                            traceEvent.taskId != AD_HOC_QUICK_SCAN.id && traceEvent.taskId != AD_HOC_REJECT_SCAN.id
                        }
                        .map { traceEvent -> traceEvent.taskId }
                    Single.zip(
                        tasksRepository.getAssets(distinctAssetIds),
                        tasksRepository.getTasks(distinctTaskIds),
                        { assets, tasks ->
                            Triple(assets, tasks, traceEvents)
                        }
                    )
                }.flatMap { (assets, tasks, traceEvents) ->
                    val taskIdsToTask = tasks.associateBy { task -> task.id }
                    val assetIdsToLength =
                        assets.associateBy({ asset -> asset.id }, { asset -> asset.length })
                    val taskIdsWithTraces = traceEvents.groupBy { traceEvent -> traceEvent.taskId }
                    val data = taskIdsWithTraces.entries.mapNotNull { entry ->
                        when (entry.key) {
                            AD_HOC_QUICK_SCAN.id -> UnsyncedSubmission(
                                taskOrderType = AD_HOC_QUICK_SCAN.id,
                                taskTypeString = AD_HOC_QUICK_SCAN.id,
                                taskDescription = AD_HOC_QUICK_SCAN.id,
                                capturedAt = null,
                                assetCount = entry.value.size,
                                "",
                                SCAN_TIME
                            )
                            AD_HOC_REJECT_SCAN.id -> UnsyncedSubmission(
                                taskOrderType = AD_HOC_REJECT_SCAN.id,
                                taskTypeString = AD_HOC_REJECT_SCAN.id,
                                taskDescription = AD_HOC_REJECT_SCAN.id,
                                capturedAt = null,
                                entry.value.size,
                                "",
                                SCAN_TIME
                            )
                            else -> {
                                var assetLength = 0.0
                                val latestCapturedTime = entry.value.mapNotNull { traceEvent ->
                                    assetLength += assetIdsToLength[traceEvent.assetId] ?: 0.0
                                    traceEvent.capturedAt
                                }.maxOrNull()
                                taskIdsToTask[entry.key]?.let { task ->
                                    val orderType =
                                        if (task.isAdHocAction()) "" else task.orderType.displayName
                                    UnsyncedSubmission(
                                        taskOrderType = orderType,
                                        taskTypeString = task.type.displayName,
                                        taskDescription = task.description ?: "",
                                        capturedAt = latestCapturedTime,
                                        assetCount = entry.value.size,
                                        tallyText = formatTally(assetLength, entry.value.size, task.unitOfMeasure),
                                    SCAN_TIME
                                    )
                                }
                            }
                        }
                    }
                    Observable.just(SetUnsyncSubmissions(data))
                }
            }

            dataIntent(queueRepository.hasMiscellaneousQueue()) {
                it.flatMap {
                    queueRepository.getUnSubmittedFeedback()
                        .flatMapObservable {
                            val list = mutableListOf<UnsyncedSubmission>()
                            for (entity in it) {
                                val data = UnsyncedSubmission(
                                    "",
                                    "Feedback",
                                    entity.feedbackType,
                                    ZonedDateTime.ofInstant(Instant.ofEpochSecond(entity.timeStamp), ZoneId.of("UTC")),
                                    0,
                                    "",
                                    SUBMIT_DATE
                                )
                                list.add(data)
                            }
                            Observable.just(SetUnsyncSubmissions(list))
                        }
                }
            }

            viewIntentCompletable<Intent.OnBackPressed> {
                it.flatMapCompletable { appNavigator.popBackStack() }
            }
        }
}