package com.scgts.sctrace.feature.landing.tasks

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.AdHocAction
import com.scgts.sctrace.base.model.AdHocAction.*
import com.scgts.sctrace.base.model.TaskType
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.feature.landing.tasks.TasksMvi.Intent
import com.scgts.sctrace.feature.landing.tasks.TasksMvi.Intent.*
import com.scgts.sctrace.feature.landing.tasks.TasksMvi.ViewState
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination.FilterAndSort
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.*
import com.scgts.sctrace.framework.navigation.NavDestination.Tasks
import com.scgts.sctrace.framework.navigation.ScreenAnimation.SLIDE_IN_FROM_RIGHT
import com.scgts.sctrace.framework.navigation.ScreenAnimation.SLIDE_UP_FROM_BOTTOM
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.queue.QueueRepository
import com.scgts.sctrace.tasks.TasksRepository
import com.scgts.sctrace.tasks.mappers.toTaskCardUiModel
import com.scgts.sctrace.user.UserRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.RackTransfer
import util.TasksManager

class TasksViewModel(
    private val navigator: AppNavigator,
    private val tasksRepository: TasksRepository,
    private val queueRepository: QueueRepository,
    private val userRepository: UserRepository,
    private val settingsManager: SettingsManager,
    private val networkChangeListener: NetworkChangeListener,
    private val tasksManager: TasksManager,
) : ViewModel(), MviViewModel<Intent, ViewState> {
    private val initialState = Supplier { ViewState() }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is RefreshData -> prev.copy(isRefreshing = true)
            is TasksData -> prev.copy(tasks = intent.tasks, isRefreshing = false)
            is Error -> prev.copy(error = intent.t)
            is SetUnfilteredTasksCount -> prev.copy(unfilteredTasksCount = intent.count)
            is SetFilterCount -> prev.copy(filterCount = intent.count)
            is SetPendingTaskCount -> prev.copy(pendingTaskCount = intent.count)
            is AdHocActions -> prev.copy(adHocActions = intent.adHocActions)
            is SetSelectedTask -> prev.copy(selectedTask = intent.selectedTask)
            is NoOp -> prev.copy(isRefreshing = false)
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
            .distinctUntilChanged()
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {

            val tasksObs = Observable.combineLatest(
                tasksRepository.getTasksObs(),
                tasksManager.getFilterAndSortObservable(),
                { _, filters -> tasksRepository.getFilteredTasks(filters).blockingGet() }
            )

            dataIntent(tasksObs) {
                it.map { tasks ->
                    TasksData(
                        tasks.map { task ->
                            task.toTaskCardUiModel(
                                showWarningIcon = tasksRepository.checkIfNotSubmittedTraceEventExistForTaskSingle(task.id).blockingGet(),
                                tallies = tasksRepository.getTalliesForTaskSingle(task.id).blockingGet()
                            )
                        }
                    )
                }
            }

            dataIntent(tasksRepository.getTasksCountObs()) {
                it.map { tasksCount -> SetUnfilteredTasksCount(tasksCount) }
            }

            dataIntent(tasksManager.getFilterCountObservable()) {
                it.map { filterCount -> SetFilterCount(filterCount) }
            }

            dataIntent(userRepository.getUserRolesForAllProject()) {
                it.map { user ->
                    val actions: MutableSet<AdHocAction> = mutableSetOf()
                    if (user.isDrillingEngineer) actions.addAll(DRILLING_ENGINEER_AD_HOC_ACTIONS)
                    if (user.isYardOperator) actions.addAll(YARD_OPERATOR_AD_HOC_ACTIONS)
                    if (user.isAuditor) actions.addAll(AUDITOR_AD_HOC_ACTIONS)
                    AdHocActions(actions.toList())
                }
            }

            dataIntent(tasksRepository.getTotalUnsubmittedCount()) {
                it.map { pendingTaskCount -> SetPendingTaskCount(pendingTaskCount) }
            }

            viewIntentObservable<RefreshData> {
                it.flatMapSingle {
                    if (networkChangeListener.isConnected()) {
                        queueRepository.submitMiscellaneousQueue()
                            .andThen(tasksRepository.submitQueue())
                            .andThen(Single.just(NoOp))
                    } else Single.just(NoOp)
                }
            }

            viewIntentCompletable<TaskClicked> {
                it.flatMapCompletable { intent ->
                    tasksRepository.getTask(intent.taskId).flatMapCompletable { task ->
                        settingsManager.setUnitType(task.unitOfMeasure).andThen(
                            when {
                                // ad hoc tasks go straight to summary when selected
                                task.isAdHocAction() && task.type == AD_HOC_RACK_TRANSFER -> {
                                    navigator.navigate(
                                        destination = RackTransferTaskSummary(
                                            task.id,
                                            task.orderId
                                        ),
                                        animation = SLIDE_IN_FROM_RIGHT
                                    )
                                }
                                task.isAdHocAction() -> {
                                    navigator.navigate(
                                        destination = TaskSummary(task.id, task.orderId),
                                        animation = SLIDE_IN_FROM_RIGHT
                                    )
                                }
                                else -> navigator.navigate(
                                    destination = TaskDetails(task.id, task.orderId),
                                    animation = SLIDE_IN_FROM_RIGHT
                                )
                            }
                        )
                    }
                }
            }

            /**
             * If a either a dispatch return or dispatch transfer task exists already we should
             * navigate to the summary of that task rather than start a new one. Once the existing
             * one is submitted then the user can create a new one.
             */
            val dispatchExistsSingle = Single.zip(
                tasksRepository.hasTask(AD_HOC_DISPATCH_TO_YARD.id),
                tasksRepository.hasTask(AD_HOC_DISPATCH_TO_WELL.id),
                { dispatchToYardExists, dispatchToWellExists -> dispatchToYardExists to dispatchToWellExists }
            )

            viewIntentCompletable<AdHocActionClicked> {
                it.flatMapCompletable { intent ->
                    val taskId = intent.adHocAction.taskId
                    fun navigateToAdHoc() = navigator.navigate(
                        destination = AdHocAction(intent.adHocAction.displayName),
                        animation = SLIDE_UP_FROM_BOTTOM
                    )

                    fun navigateToTaskSummary(taskId: String) =
                        tasksRepository.getTask(taskId).flatMapCompletable { task ->
                            settingsManager.setUnitType(task.unitOfMeasure).andThen(
                                navigator.navigate(
                                    destination = TaskSummary(taskId, ""),
                                    popUpTo = Tasks
                                )
                            )
                        }
                    when (intent.adHocAction) {
                        QuickScan, RejectScan -> {
                            tasksRepository.getProjects().flatMapCompletable { projects ->
                                if (projects.size == 1) {
                                    settingsManager.setUnitType(projects[0].unitOfMeasure).andThen(
                                        if (intent.adHocAction == RejectScan) {
                                            navigator.navigate(
                                                Capture(
                                                    projectId = projects[0].id,
                                                    taskId = TaskType.AD_HOC_REJECT_SCAN.id
                                                )
                                            )
                                        } else {
                                            navigator.navigate(
                                                Capture(
                                                    projectId = projects[0].id,
                                                    taskId = null
                                                )
                                            )
                                        }
                                    )
                                } else navigateToAdHoc()
                            }
                        }
                        Dispatch -> {
                            dispatchExistsSingle.flatMapCompletable { (dispatchToYardExists, dispatchToWellExists) ->
                                when {
                                    dispatchToYardExists ->
                                        navigateToTaskSummary(AD_HOC_DISPATCH_TO_YARD.id)
                                    dispatchToWellExists ->
                                        navigateToTaskSummary(AD_HOC_DISPATCH_TO_WELL.id)
                                    else -> navigateToAdHoc()
                                }
                            }
                        }
                        RackTransfer -> {
                            tasksRepository.hasTask(taskId).flatMapCompletable { taskExists ->
                                if (taskExists) {
                                    tasksRepository.getTask(taskId).flatMapCompletable { task ->
                                        settingsManager.setUnitType(task.unitOfMeasure).andThen(
                                            navigator.navigate(
                                                destination = RackTransferTaskSummary(
                                                    taskId = taskId,
                                                    orderId = ""
                                                ),
                                                popUpTo = Tasks
                                            )
                                        )
                                    }
                                } else navigateToAdHoc()
                            }
                        }
                        else -> tasksRepository.hasTask(taskId).flatMapCompletable { taskExists ->
                            if (taskExists) navigateToTaskSummary(taskId)
                            else navigateToAdHoc()
                        }
                    }
                }
            }

            viewIntentCompletable<GoToUnsyncedSubmissions> {
                it.flatMapCompletable { intent ->
                    navigator.navigate(UnsyncedSubmissions(intent.originName), SLIDE_IN_FROM_RIGHT)
                }
            }

            viewIntentCompletable<GoToSettings> {
                it.flatMapCompletable { intent ->
                    navigator.navigate(Settings(intent.originName), SLIDE_IN_FROM_RIGHT)
                }
            }

            viewIntentObservable<OnTaskSelected> {
                it.flatMap { intent ->
                    tasksRepository.getTask(intent.taskId).flatMapObservable { task ->
                        settingsManager.setUnitType(task.unitOfMeasure)
                            .andThen(Observable.just(SetSelectedTask(task)))
                    }
                }
            }

            viewIntentCompletable<FilterAndSortClicked> {
                it.flatMapCompletable { navigator.navigate(FilterAndSort, SLIDE_UP_FROM_BOTTOM) }
            }

            viewIntentPassThroughs(RefreshData::class)
        }

    companion object {
        val DRILLING_ENGINEER_AD_HOC_ACTIONS = listOf(
            QuickScan, RejectScan, Dispatch, InboundToWell
        )
        val YARD_OPERATOR_AD_HOC_ACTIONS = listOf(
            QuickScan, InboundFromMill, InboundFromWellSite, RackTransfer
        )
        val AUDITOR_AD_HOC_ACTIONS = listOf(QuickScan)
    }
}
