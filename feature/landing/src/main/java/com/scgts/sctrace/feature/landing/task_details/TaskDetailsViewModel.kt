package com.scgts.sctrace.feature.landing.task_details

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.model.TaskType.RACK_TRANSFER
import com.scgts.sctrace.base.util.formatTally
import com.scgts.sctrace.base.util.toUiModels
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.Intent
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.Intent.*
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.ViewState
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RackTransferTaskSummary
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.TaskSummary
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class TaskDetailsViewModel(
    private var taskId: String?,
    private var orderId: String?,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val initialState = Supplier { ViewState() }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is TaskDetailsData -> prev.copy(
                taskType = intent.task.type,
                orderType = intent.task.orderType,
                orderAndTaskType = intent.task.orderAndTask(),
                taskDescription = intent.task.description ?: "",
                taskStatus = intent.task.status,
                wellSection = intent.task.wellSection,
                deliveryDate = intent.task.deliveryDate,
                percentCompletion = calculatePercentCompletion(
                    totalNumOfJoints = intent.tallies.totalJoints,
                    expectedNumOfJoints = intent.task.totalNumJoints
                ),
                totalTally = formatTally(
                    expectedLength = intent.tallies.total,
                    numJoints = intent.tallies.totalJoints,
                    unitType = intent.task.unitOfMeasure,
                    decimalPlaces = 2
                ),
                totalConsumed = formatTally(
                    expectedLength = intent.tallies.totalConsumed,
                    numJoints = intent.tallies.consumedJoints,
                    unitType = intent.task.unitOfMeasure,
                    decimalPlaces = 2
                ),
                expectedTally = formatTally(
                    expectedLength = intent.task.totalExpectedLength,
                    numJoints = intent.task.totalNumJoints,
                    unitType = intent.task.unitOfMeasure,
                    decimalPlaces = 2
                ),
                specialInstructions = intent.task.specialInstructions,
                assetProductDescription = intent.productInfoList
            )
            is TaskLocationsData -> prev.copy(
                fromLocation = intent.fromLocation,
                toLocation = intent.toLocation
            )
            is LastUpdatedAt -> prev.copy(lastUpdatedAt = intent.updatedAt)
            is ShowNotSubmittedTraceEventsWarning -> prev.copy(showNotSubmittedTraceEventsWarning = intent.show)
            else -> prev
        }
    }

    fun setNewTaskAndOrderId(taskId: String, orderId: String): TaskDetailsViewModel {
        this.taskId = taskId
        this.orderId = orderId
        return this
    }

    fun getTaskId() = taskId!!
    fun getOrderId() = orderId!!
    fun hasTaskAndOrderId() = taskId != null && orderId != null

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {
        taskId?.let { taskId ->
            orderId?.let { orderId ->
                val taskDetailsDataObs = Observable.combineLatest(
                    tasksRepository.getTaskObs(taskId),
                    tasksRepository.getTalliesForTask(taskId),
                    tasksRepository.getAssetProductInformations(orderId, taskId),
                    { task, tallies, productInfoList -> Triple(task, tallies, productInfoList) }
                )

                dataIntent(taskDetailsDataObs) {
                    it.map { (task, tallies, productInfoList) ->
                        TaskDetailsData(
                            task = task,
                            tallies = tallies,
                            productInfoList = productInfoList.toUiModels(task.unitOfMeasure)
                        )
                    }
                }

                dataIntent(tasksRepository.getTaskObs(taskId)) {
                    it.map { task ->
                        TaskLocationsData(
                            fromLocation = task.fromLocationId?.let { fromLocationId ->
                                tasksRepository.getFacilityById(fromLocationId).blockingGet()
                            },
                            toLocation = task.toLocationId?.let { toLocationId ->
                                tasksRepository.getFacilityById(toLocationId).blockingGet()
                            }
                        )
                    }
                }

                dataIntent(tasksRepository.getTaskLastUpdatedDate(taskId)) {
                    it.map { lastUpdatedDate ->
                        LastUpdatedAt(
                            updatedAt = lastUpdatedDate.withZoneSameInstant(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("MMM dd 'at' hh:mma"))
                                ?.replace("AM", "am")
                                ?.replace("PM", "pm")
                        )
                    }
                }

                dataIntent(tasksRepository.checkIfNotSubmittedTraceEventExistForTaskObs(taskId)) {
                    it.map { hasNotSubmittedTask ->
                        ShowNotSubmittedTraceEventsWarning(hasNotSubmittedTask)
                    }
                }

                viewIntentCompletable<BackClicked> {
                    it.flatMapCompletable { navigator.popBackStack() }
                }

                viewIntentCompletable<ContinueClicked> {
                    it.flatMapCompletable {
                        tasksRepository.getTask(taskId).flatMapCompletable { task ->
                            navigator.navigate(
                                when (task.type) {
                                    RACK_TRANSFER -> RackTransferTaskSummary(taskId, orderId)
                                    else -> TaskSummary(taskId, orderId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun calculatePercentCompletion(totalNumOfJoints: Int, expectedNumOfJoints: Int): Float {
        return if (totalNumOfJoints == 0) 0f
        else (totalNumOfJoints / expectedNumOfJoints.toFloat()).coerceAtMost(1f)
    }
}
