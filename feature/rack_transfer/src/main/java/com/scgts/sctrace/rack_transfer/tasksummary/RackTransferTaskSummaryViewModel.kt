package com.scgts.sctrace.rack_transfer.tasksummary

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.ExpandableTextEntry
import com.scgts.sctrace.base.model.Length
import com.scgts.sctrace.base.model.Task
import com.scgts.sctrace.base.util.formatTally
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination
import com.scgts.sctrace.framework.navigation.ScreenAnimation
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.rack_transfer.R
import com.scgts.sctrace.rack_transfer.tasksummary.RackTransferTaskSummaryMvi.Intent
import com.scgts.sctrace.rack_transfer.tasksummary.RackTransferTaskSummaryMvi.ViewState
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.sendErrorToDtrace
import java.util.*
import java.util.concurrent.TimeUnit

class RackTransferTaskSummaryViewModel(
    private val taskId: String,
    private val orderId: String,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
    private val networkChangeListener: NetworkChangeListener,
    private val isTablet: Boolean,
    private val settingsManager: SettingsManager,
) : ViewModel(), MviViewModel<Intent, ViewState> {
    private val initialState = Supplier { ViewState(summaryExpanded = isTablet) }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is Intent.TaskData -> prev.copy(task = intent.task)
            is Intent.Submitted -> prev.copy(submitted = true)
            is Intent.OfflineSubmitted -> prev.copy(isOfflineSubmitted = true, submitted = true)
            is Intent.ExpandableSummaryData -> prev.copy(summaryListExpandable = intent.summaryListExpandable)
            is Intent.RackTransferData -> prev.copy(rackTransfers = intent.rackTransfers)
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
            val getTask = tasksRepository.getTask(taskId)

            dataIntent(tasksRepository.getTaskObs(taskId)) {
                it.map { task ->
                    Intent.TaskData(task = task)
                }
            }

            dataIntent(tasksRepository.getTalliesForTask(taskId = taskId)) {
                it.flatMap { tallies ->
                    tasksRepository.getTalliesForTask(taskId = taskId, sessionOnly = true)
                        .flatMap { sessionTallies ->
                            val unitType = settingsManager.unitType().blockingFirst()
                            val totalRunningLength = Length(tallies.totalRunningLength, unitType)
                                .getFormattedLengthString()
                            val summaryListExpandable = mutableListOf<ExpandableTextEntry>()
                            summaryListExpandable.addAll(
                                listOf(
                                    ExpandableTextEntry(
                                        label = R.string.total_tally,
                                        body = formatTally(
                                            tallies.total,
                                            tallies.totalJoints,
                                            unitType
                                        )
                                    ),
                                    ExpandableTextEntry(
                                        label = R.string.session_tally,
                                        body = formatTally(
                                            sessionTallies.total,
                                            sessionTallies.totalJoints,
                                            unitType
                                        )
                                    ),
                                    ExpandableTextEntry(
                                        label = R.string.total_run_length,
                                        body = totalRunningLength,
                                        expandable = true
                                    ),
                                    ExpandableTextEntry(
                                        label = R.string.order,
                                        body = "See Details",
                                        expandable = true
                                    )
                                )
                            )
                            Observable.just(
                                Intent.ExpandableSummaryData(
                                    summaryListExpandable = summaryListExpandable
                                )
                            )
                        }
                }
            }

            dataIntent(
                tasksRepository.getRackTransferAssets(
                    taskId,
                    settingsManager.unitType().blockingFirst()
                ).distinctUntilChanged()
            ) {
                it.map { assets ->
                    Intent.RackTransferData(rackTransfers = assets)
                }
            }


            viewIntentCompletable<Intent.BackClicked> {
                it.flatMapCompletable { navigator.popBackStack() }
            }

            viewIntentCompletable<Intent.CaptureClicked> {
                it.flatMapCompletable {
                    tasksRepository.getTask(taskId).flatMapCompletable { task ->
                        navigator.navigate(
                            NavDestination.NavDestinationArgs.RackTransferSelection(
                                task.projectId,
                                taskId
                            )
                        )
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

            viewIntentCompletable<Intent.RackTransferClicked> {
                it.flatMapCompletable { intent ->
                    navigator.navigate(
                        NavDestination.NavDestinationArgs.RackDetails(
                            rackId = intent.rackId,
                            taskId = taskId,
                            millWorkNum = intent.millWorkNum,
                            productDescription = intent.productDescription
                        )
                    )
                }
            }

            viewIntentObservable<Intent.SubmitClicked> {
                it.flatMap {
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
                                if (networkChangeListener.isConnected())
                                    navigator.navigate(
                                        destination = NavDestination.Tasks,
                                        popUpTo = NavDestination.Tasks
                                    )
                                        .delay(3, TimeUnit.SECONDS)
                                        .startWith(Observable.just(Intent.Submitted))
                                else Observable.just(Intent.OfflineSubmitted)
                            )
                    }
                }
            }

            viewIntentCompletable<Intent.NavigateToTasksOnPostSubmit> {
                it.flatMapCompletable {
                    navigator.navigate(
                        destination = NavDestination.Tasks,
                        popUpTo = NavDestination.Tasks
                    )
                }
            }

            viewIntentCompletable<Intent.EditClicked> {
                it.flatMapCompletable { intent ->
                    navigator.navigate(
                        NavDestination.NavDestinationArgs.EditRackTransfer(
                            rackId = intent.rackId,
                            taskId = taskId,
                            millWorkNum = intent.millWorkNum,
                            productDescription = intent.productDescription
                        )
                    )
                }
            }
        }
}
