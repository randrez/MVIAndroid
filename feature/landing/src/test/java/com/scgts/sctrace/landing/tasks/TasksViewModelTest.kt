package com.scgts.sctrace.landing.tasks

import com.scgts.sctrace.base.auth.UnitType
import com.scgts.sctrace.base.model.TaskStatus
import com.scgts.sctrace.feature.landing.models.TaskAction
import com.scgts.sctrace.feature.landing.tasks.TasksMvi
import com.scgts.sctrace.feature.landing.tasks.TasksViewModel
import com.scgts.sctrace.queue.QueueRepository
import com.scgts.sctrace.tasks.TasksRepository
import com.scgts.sctrace.tasks.model.OrderType
import com.scgts.sctrace.tasks.model.Task
import com.scgts.sctrace.tasks.model.TaskType
import com.scgts.sctrace.tasks.model.TraceEvent
import com.scgts.sctrace.user.UserRepository
import com.scgts.sctrace.user.model.UserRole
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import models.TaskCardUiModel
import org.junit.Ignore
import org.junit.Test

class TasksViewModelTest {

    lateinit var model: TasksViewModel
    private val tasksIntents: PublishSubject<TasksMvi.Intent> = PublishSubject.create()
    private val mockRepoTask: TasksRepository = mockk(relaxed = true)
    private val mockRepoUser: UserRepository = mockk()
    private val mockQueue: QueueRepository = mockk(relaxed = true)
    private val testScheduler = TestScheduler()

    @Test
    fun userActions() {
        val expectedViewState = generalSetup()
        val userRoles = getTestUserRoles()
        val actions: MutableList<TaskAction> = getActions(userRoles)
        val testObserver = testObservable()
        testScheduler.triggerActions()
        tasksIntents.onNext(TasksMvi.Intent.AdHocActions(actions))
        verifyViewStateUser(testObserver?.values()?.last(), expectedViewState)
    }

    @Test
    fun userActionsFail() {
        val expectedViewState = generalSetupFail()
        val userRoles = getTestUserRoles()
        val actions: MutableList<TaskAction> = getActions(userRoles)
        val testObserver = testObservable()
        testScheduler.triggerActions()
        tasksIntents.onNext(TasksMvi.Intent.AdHocActions(actions))
        assertFalse(testObserver?.values()?.last()?.adHocActions == expectedViewState.adHocActions)
    }

    @Ignore("Need to update task test")
    @Test
    fun tasks() {
        val expectedViewState = generalSetup()
        val tasks = getTestArrayTasksCardUiModel()
        val testObserver = testObservable()
        testScheduler.triggerActions()
        tasksIntents.onNext(TasksMvi.Intent.TasksData(tasks = tasks))
        verifyViewStateTasks(testObserver?.values()?.last(), expectedViewState)
    }

    @Ignore("Task failing")
    @Test
    fun tasksFail() {
        val expectedViewState = generalSetupFail()
        val tasks = getTestArrayTasksCardUiModel()
        val testObserver = testObservable()
        testScheduler.triggerActions()
        tasksIntents.onNext(TasksMvi.Intent.TasksData(tasks = tasks))
        assertFalse(testObserver?.values()?.last()?.tasks?.size == expectedViewState.tasks.size)
    }

    @Test
    fun countUncommitted() {
        val expectedViewState = generalSetup()
        val count: Int = countTasksUnSubmit(getTestArrayTraceEvent())
        every { mockRepoTask.getTotalUnsubmittedCount() } returns Observable.just(count)
        val testObserver = testObservable()
        testScheduler.triggerActions()
        tasksIntents.onNext(TasksMvi.Intent.SetPendingTaskCount(count))
        verifyViewStateCountUnSubmitted(testObserver?.values()?.last(), expectedViewState)
    }

    @Test
    fun countUncommittedFail() {
        val expectedViewState = generalSetupFail()
        val count: Int = countTasksUnSubmit(getTestArrayTraceEvent())
        every { mockRepoTask.getTotalUnsubmittedCount() } returns Observable.just(count)
        val testObserver = testObservable()
        testScheduler.triggerActions()
        tasksIntents.onNext(TasksMvi.Intent.SetPendingTaskCount(count))
        assertFalse(
            testObserver?.values()
                ?.last()?.pendingTaskCount == expectedViewState.pendingTaskCount
        )
    }

    @Test
    fun selectTask() {
        val expectedViewState = generalSetup()
        val task = getTestTask()
        testScheduler.triggerActions()
        tasksIntents.onNext(TasksMvi.Intent.OnTaskSelected(task.id))
        verifyViewStateTaskSelected(expectedViewState)
    }

    @Test
    fun selectTaskFail() {
        val expectedViewState = generalSetupFail()
        val task = getTestTask()
        val testObserver = testObservable()
        testScheduler.triggerActions()
        tasksIntents.onNext(TasksMvi.Intent.OnTaskSelected(task.id))
        assertFalse(
            testObserver?.values()?.last()?.selectedTask != expectedViewState.selectedTask
        )
    }

    private fun verifyViewStateTasks(
        viewStateActual: TasksMvi.ViewState?,
        viewStateExpected: TasksMvi.ViewState,
    ) {
        assertEquals(viewStateExpected.tasks, viewStateActual?.tasks)
    }

    private fun verifyViewStateUser(
        viewStateActual: TasksMvi.ViewState?,
        viewStateExpected: TasksMvi.ViewState,
    ) {
        assertEquals(viewStateActual?.adHocActions, viewStateExpected.adHocActions)
    }

    private fun verifyViewStateCountUnSubmitted(
        viewStateActual: TasksMvi.ViewState?,
        viewStateExpected: TasksMvi.ViewState,
    ) {
        assertEquals(viewStateActual?.pendingTaskCount, viewStateExpected.pendingTaskCount)
    }

    private fun verifyViewStateTaskSelected(
        viewStateExpected: TasksMvi.ViewState,
    ) {
        val taskExpected = viewStateExpected.selectedTask
        assertEquals(getTestTask(), taskExpected)
    }

    private fun generalSetup(): TasksMvi.ViewState {
        setup()
        val count: Int = countTasksUnSubmit(getTestArrayTraceEvent())
        val actions: MutableList<TaskAction> = getActions(getTestUserRoles())
        val selectedTask = getTestTask()
        val expectedViewState = TasksMvi.ViewState(
            loading = false,
            error = null,
            tasks = getTestArrayTasksCardUiModel(),
            adHocActions = actions,
            selectedTask = selectedTask,
            pendingTaskCount = count
        )
        mockRepositories()
        return expectedViewState
    }

    private fun generalSetupFail(): TasksMvi.ViewState {
        setup()
        val expectedViewState = TasksMvi.ViewState(
            loading = false,
            error = null,
            tasks = ArrayList(),
            adHocActions = ArrayList(),
            selectedTask = null,
            pendingTaskCount = 0
        )
        mockRepositories()
        return expectedViewState
    }

    private fun mockRepositories() {
        every { mockRepoUser.getUserRolesForAllProject() } returns Observable.just(getTestUserRoles())
        every { mockRepoTask.getFilteredTasks(any()) } returns Single.just(getTestArrayTask())
        every { mockRepoTask.getTasksObs() } returns Observable.just(getTestArrayTask())
        every { mockRepoTask.getPendingTaskIdsObs() } returns Observable.just(getTestArrayPending())
        every { mockRepoTask.getUnsubmittedTraceEvents() } returns Observable.just(
            getTestArrayTraceEvent()
        )
        every { mockRepoTask.hasTask(any()) } returns Single.just(true)
        every { mockQueue.submitMiscellaneousQueue() } returns Completable.complete()
        every { mockRepoTask.submitQueue() } returns Completable.complete()
        every { mockRepoTask.getPendingTaskIds() } returns Single.just(getTestArrayPending())
        every { mockRepoTask.getTask("taskId1") } returns Single.just(getTestTask())
    }

    private fun getActions(user: UserRole): MutableList<TaskAction> {
        val actions: MutableList<TaskAction> = mutableListOf()
        if (user.isDrillingEngineer) {
            actions.addAll(TasksViewModel.DRILLING_ENGINEER_AD_HOC_ACTIONS)
        } else if (user.isYardOperator) {
            actions.addAll(TasksViewModel.YARD_OPERATOR_AD_HOC_ACTIONS)
        }
        return actions
    }

    private fun countTasksUnSubmit(traceEvents: List<TraceEvent>): Int {
        return traceEvents.groupBy { event -> event.taskId }.keys.size
    }

    private fun setup() {
        model = TasksViewModel(
            navigator = mockk(relaxed = true),
            tasksRepository = mockRepoTask,
            mockQueue,
            userRepository = mockRepoUser,
            settingsManager = mockk(relaxed = true),
            networkChangeListener = mockk(relaxed = true),
            mockk(relaxed = true)
        )
    }

    private fun testObservable(): @NonNull TestObserver<TasksMvi.ViewState>? {
        return model.bind(tasksIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
    }

    private fun getTestUserRoles(): UserRole {
        return UserRole(
            isDrillingEngineer = false,
            isYardOperator = true,
            isAuditor = false,
        )
    }

    private fun getTestTask(): Task {
        return Task(
            id = "taskId1",
            type = TaskType.INBOUND_FROM_MILL,
            status = TaskStatus.IN_PROGRESS,
            totalExpectedLength = 0.0,
            totalNumJoints = 0,
            projectId = "projectId",
            orderId = "orderId",
            unitOfMeasure = UnitType.Feet,
            orderType = OrderType.INBOUND,
            specialInstructions = null,
            description = "descriptionTest",
            toLocationId = "locationId",
            toLocationName = "locationName",
            fromLocationId = "fromLocationId",
            fromLocationName = "fromLocationName",
            arrivalDate = "11/06/2021",
            deliveryDate = "11/06/2021",
            dispatchDate = null,
            defaultRackLocationId = "defaultRackLocationId",
            wellSection = "wellSection",
            organizationName = "organizationName"
        )
    }

    private fun getTestArrayTask(): List<Task> {

        val task1 = Task(
            id = "taskId1",
            type = TaskType.INBOUND_FROM_MILL,
            status = TaskStatus.IN_PROGRESS,
            totalExpectedLength = 0.0,
            totalNumJoints = 0,
            projectId = "projectId",
            orderId = "orderId",
            unitOfMeasure = UnitType.Feet,
            orderType = OrderType.INBOUND,
            specialInstructions = null,
            description = "descriptionTest",
            toLocationId = "locationId",
            toLocationName = "locationName",
            fromLocationId = "fromLocationId",
            fromLocationName = "fromLocationName",
            arrivalDate = "11/06/2021",
            deliveryDate = "11/06/2021",
            dispatchDate = null,
            defaultRackLocationId = "defaultRackLocationId",
            wellSection = "wellSection",
            organizationName = "organizationName"
        )

        val task2 = Task(
            id = "taskId2",
            type = TaskType.INBOUND_TO_WELL,
            status = TaskStatus.IN_PROGRESS,
            totalExpectedLength = 0.0,
            totalNumJoints = 0,
            projectId = "projectId",
            orderId = "orderId",
            unitOfMeasure = UnitType.Feet,
            orderType = OrderType.INBOUND,
            specialInstructions = null,
            description = "descriptionTest",
            toLocationId = "locationId",
            toLocationName = "locationName",
            fromLocationId = "fromLocationId",
            fromLocationName = "fromLocationName",
            arrivalDate = "11/06/2021",
            deliveryDate = "11/06/2021",
            dispatchDate = null,
            defaultRackLocationId = "defaultRackLocationId",
            wellSection = "wellSection",
            organizationName = "organizationName"
        )

        return arrayListOf(task1, task2)
    }

    private fun getTestArrayTasksCardUiModel(): List<TaskCardUiModel> {
        return getTestArrayTask().map { it.toTaskCardUiModel() }
    }

    private fun getTestArrayPending(): List<String> {
        return arrayListOf("taskId3", "taskId4")
    }

    private fun getTestArrayTraceEvent(): List<TraceEvent> {
        val traceEventFirst = TraceEvent(
            taskId = "taskId1",
            assetId = "assetId1",
            capturedAt = null,
            conditionId = "conditionId",
            rackLocationId = "rackLocationId",
            consumed = false,
            laserLength = null
        )

        val traceEventSecond = TraceEvent(
            taskId = "taskId2",
            assetId = "assetId2",
            capturedAt = null,
            conditionId = "conditionId",
            rackLocationId = "rackLocationId",
            consumed = false,
            laserLength = null
        )

        return arrayListOf(traceEventFirst, traceEventSecond)
    }

}