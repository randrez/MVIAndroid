package com.scgts.sctrace.task_summary.ui

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.SubmitStatus.NOT_SUBMITTED
import com.scgts.sctrace.base.model.TaskType.CONSUME
import com.scgts.sctrace.base.util.toUiModels
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.*
import com.scgts.sctrace.framework.navigation.NavDestination.Tasks
import com.scgts.sctrace.framework.navigation.ScreenAnimation
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.task_summary.R
import com.scgts.sctrace.task_summary.ui.TaskSummaryMvi.Intent
import com.scgts.sctrace.task_summary.ui.TaskSummaryMvi.Intent.*
import com.scgts.sctrace.task_summary.ui.TaskSummaryMvi.ViewState
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.sendErrorToDtrace
import java.util.*

class TaskSummaryViewModel(
    private val taskId: String,
    private val orderId: String,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
    private val networkChangeListener: NetworkChangeListener,
    private val isTablet: Boolean,
    private val settingsManager: SettingsManager,
) : ViewModel(), MviViewModel<Intent, ViewState> {
    private val initialState = Supplier { ViewState(summaryExpanded = isTablet) }
    private var taskType: TaskType? = null
    private var orderType: OrderType? = null

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is TaskData -> prev.copy(task = intent.task)
            is Assets -> prev.copy(assets = intent.assets, selectableAssets = intent.selectable)
            is UnitTypeUpdate -> prev.copy(unitType = intent.unitType)
            is ToggleSummaryExpanded -> prev.copy(summaryExpanded = !prev.summaryExpanded || isTablet)
            is Submitted -> prev.copy(submitted = true)
            is OfflineSubmitted -> prev.copy(isOfflineSubmitted = true, submitted = true)
            is TalliesAndJoints -> prev.copy(totalTalliesAndJoints = intent)
            is SessionTalliesAndJoints -> prev.copy(sessionTalliesAndJoints = intent)
            is SwipeToEditEnabled -> prev.copy(swipeToEditEnabled = intent.enabled)
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
            .distinctUntilChanged()
            .doOnError { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {
            dataIntent(settingsManager.unitType()) {
                it.map { unitType -> UnitTypeUpdate(unitType) }
            }

            val getTask = tasksRepository.getTask(taskId)

            dataIntent(tasksRepository.getTaskObs(taskId)) {
                it.map { task ->
                    taskType = task.type
                    orderType = task.orderType
                    TaskData(task = task)
                }
            }

            dataIntent(getTask) {
                it.map<Intent> { task ->
                    SwipeToEditEnabled(task.swipeToEditEnabled())
                }.toObservable()
            }

            dataIntent(tasksRepository.getTalliesForTask(taskId, sessionOnly = true)) {
                it.map { tallies ->
                    SessionTalliesAndJoints(
                        sessionTotal = tallies.total,
                        sessionConsumed = tallies.totalConsumed,
                        sessionTotalJoints = tallies.totalJoints,
                        sessionConsumedJoints = tallies.consumedJoints,
                        sessionMakeUpLoss = tallies.totalMakeUpLoss
                    )
                }
            }

            dataIntent(tasksRepository.getTalliesForTask(taskId)) {
                it.map { tallies ->
                    TalliesAndJoints(
                        total = tallies.total,
                        totalConsumed = tallies.totalConsumed,
                        totalMakeUpLoss = tallies.totalMakeUpLoss,
                        totalRejected = tallies.totalRejected,
                        totalJoints = tallies.totalJoints,
                        consumedJoints = tallies.consumedJoints,
                        rejectedJoints = tallies.rejectedJoints,
                        consumedRunningLength = tallies.totalConsumedRunningLength,
                        runningLength = tallies.totalRunningLength
                    )
                }
            }

            dataIntent(tasksRepository.getTalliesForTask(taskId = taskId)) {
                it.flatMap { tallies ->
                    tasksRepository.getTalliesForTask(taskId = taskId, sessionOnly = true)
                        .flatMap { sessionTallies ->
                            val unitType = settingsManager.unitType().blockingFirst()
                            val totalRunningLength = if (taskType == CONSUME) {
                                Length(
                                    tallies.totalConsumedRunningLength,
                                    unitType
                                ).getFormattedLengthString()
                            } else {
                                Length(
                                    tallies.totalRunningLength,
                                    unitType
                                ).getFormattedLengthString()
                            }
                            val summaryListExpandable = mutableListOf<ExpandableTextEntry>()
                            Observable.just(ExpandableSummaryData(summaryListExpandable = summaryListExpandable))
                        }
                }
            }


            val getAssociatedAssetsIntent = getTask.flatMap { task ->
                tasksRepository.getAssets(task.assetIdsFromPreviousTask)
            }.toObservable()

            dataIntent(
                Observable.combineLatest(
                    tasksRepository.getTaskAssets(taskId),
                    getAssociatedAssetsIntent,
                    { unsubmitted, associated -> Pair(unsubmitted, associated) }
                )
            ) {
                it.map { (unsubmittedAssets, associatedAssets) ->
                    val mutableUnsubmittedAssets = unsubmittedAssets.toUiModels().toMutableList()
                    val mutableAssociatedAssets = associatedAssets.toUiModels(false).toMutableList()
                    unsubmittedAssets.forEach { asset ->
                        if (associatedAssets.find { asset.id == it.id } != null) {
                            mutableAssociatedAssets.removeIf { asset.id == it.id }
                        }
                    }
                    mutableUnsubmittedAssets.removeIf { asset -> asset.submitStatus != NOT_SUBMITTED }
                    Assets(
                        assets = mutableUnsubmittedAssets + mutableAssociatedAssets,
                        selectable = associatedAssets.isNotEmpty()
                    )
                }
            }

            dataIntent(
                tasksRepository.getRackTransferAssets(
                    taskId,
                    settingsManager.unitType().blockingFirst()
                ).distinctUntilChanged()
            ) {
                it.map { assets ->
                    AssetsRackTransfer(assetsRackTransfer = assets)
                }
            }


            viewIntentCompletable<BackClicked> {
                it.flatMapCompletable { navigator.popBackStack() }
            }

            viewIntentCompletable<CaptureClicked> {
                it.flatMapCompletable {
                    tasksRepository.getTask(taskId).flatMapCompletable { task ->
                        navigator.navigate(Capture(task.projectId, task.id))
                    }
                }
            }

            viewIntentCompletable<CaptureRackTransferClicked> {
                it.flatMapCompletable {
                    tasksRepository.getTask(taskId).flatMapCompletable { task ->
                        navigator.navigate(RackTransferSelection(task.projectId, taskId))
                    }
                }
            }

            viewIntentCompletable<Intent.SeeDetails> {
                it.flatMapCompletable {
                    navigator.navigate(
                        destination = NavDestination.NavDestinationArgs.SeeDetails(taskId, orderId),
                        animation = ScreenAnimation.SLIDE_UP_FROM_BOTTOM
                    )
                }
            }

            viewIntentCompletable<AssetClicked> {
                it.flatMapCompletable { intent ->
                    navigator.navigate(ShowAssetDetails(assetId = intent.assetId, taskId = taskId))
                }
            }

            viewIntentCompletable<DeleteCapturedAsset> {
                it.flatMapCompletable { intent ->
                    tasksRepository.removeCapturedAsset(taskId = taskId, assetId = intent.assetId)
                }
            }

            viewIntentCompletable<SelectAsset> {
                it.flatMapCompletable { intent ->
                    tasksRepository.traceEventExists(taskId = taskId, assetId = intent.assetId)
                        .flatMapCompletable { traceEventExists ->
                            if (traceEventExists) {
                                tasksRepository.updateOutboundTraceEventCheckedStatus(
                                    taskId = taskId,
                                    assetId = intent.assetId,
                                    checked = true
                                )
                            } else tasksRepository.getTask(taskId).flatMapCompletable { task ->
                                tasksRepository.addTraceEvent(
                                    taskId = task.id,
                                    assetId = intent.assetId,
                                    facilityId = task.toLocationId!!,
                                )
                            }
                        }
                }
            }

            viewIntentCompletable<DeselectAsset> {
                it.flatMapCompletable { intent ->
                    tasksRepository.updateOutboundTraceEventCheckedStatus(
                        taskId = taskId,
                        assetId = intent.assetId,
                        checked = false
                    )
                }
            }

            viewIntentObservable<SubmitClicked> {
                it.flatMap {
                    tasksRepository.deleteUncheckedOutboundTraceEvents(taskId).andThen(
                        Single.zip(
                            tasksRepository.getAssetIdsOfNotSubmittedTraceEventForTask(taskId),
                            tasksRepository.getTask(taskId),
                            { assetIds: List<String>, task: Task -> assetIds to task }
                        ).flatMapObservable { (assetIds, task) ->
                            val finalTaskId =
                                if (task.isAdHocAction()) UUID.randomUUID().toString() else task.id

                            val finalTask =
                                if (task.isAdHocAction()) task.copy(id = finalTaskId) else task

                            fun updateTaskIds() = if (task.isAdHocAction()) {
                                tasksRepository.updateTaskId(task.id, finalTaskId).andThen(
                                    tasksRepository.updateTraceEventTaskId(
                                        taskId = task.id,
                                        newTaskId = finalTaskId
                                    )
                                )
                            } else Completable.complete()

                            updateTaskIds()
                                .andThen(tasksRepository.addToQueue(finalTask, assetIds))
                                .andThen(
                                    if (networkChangeListener.isConnected()) Observable.just(Submitted)
                                    else Observable.just(OfflineSubmitted)
                                )
                        }
                    )
                }
            }

            viewIntentCompletable<NavigateToTasksOnPostSubmit> {
                it.flatMapCompletable { navigator.navigate(destination = Tasks, popUpTo = Tasks) }
            }

            viewIntentCompletable<EditCapturedAsset> {
                it.flatMapCompletable { intent ->
                    tasksRepository.getTask(taskId).flatMapCompletable { task ->
                        navigator.navigate(Capture(task.projectId, task.id)).andThen(
                            navigator.navigate(
                                AssetDetails(
                                    AssetDataForNavigation(
                                        assetId = intent.assetId,
                                        taskId = taskId,
                                        newAsset = false,
                                        unexpectedWarning = TypeWarnings.NO_WARNING,
                                        originPage = R.id.taskSummaryFragment
                                    )
                                )
                            )
                        )
                    }
                }
            }

            viewIntentPassThroughs(
                ToggleSummaryExpanded::class,
                SelectAsset::class,
                DeselectAsset::class
            )
        }
}
