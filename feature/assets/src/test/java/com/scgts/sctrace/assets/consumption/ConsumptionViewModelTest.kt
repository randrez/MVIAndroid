package com.scgts.sctrace.assets.consumption

import com.scgts.sctrace.assets.consumption.ConsumptionIntent.Intent
import com.scgts.sctrace.assets.consumption.ConsumptionIntent.Intent.*
import com.scgts.sctrace.assets.consumption.ConsumptionIntent.ViewState
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType.ConsumedToRejected
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType.RejectedToConsumed
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import com.scgts.sctrace.tasks.TasksRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import util.ScanStateManager
import util.TopToastManager

class ConsumptionViewModelTest {

    private lateinit var viewModel: ConsumptionViewModel
    private var consumptionIntents = PublishSubject.create<Intent>()
    private val mockTasksRepository: TasksRepository = mockk()
    private val mockScanStateManager: ScanStateManager = mockk()
    private val mockTopToastManager: TopToastManager = mockk()
    private lateinit var testScheduler: TestScheduler
    private var inputCache = InMemoryObjectCache<Pair<Reason?, String>>(Pair(null, ""))

    @Before
    fun setUpViewModel() {
        viewModel = ConsumptionViewModel(
            assetId = "assetId",
            taskId = "taskId",
            tasksRepository = mockTasksRepository,
            navigator = mockk(relaxed = true),
            statusChange = ConsumedToRejected.name,
            scanStateManager = mockScanStateManager,
            topToastManager = mockTopToastManager,
            quickReject = false
        )
        testScheduler = TestScheduler()
        every { mockTasksRepository.getAsset("assetId") } returns Single.just(getTestAsset())
        every { mockTasksRepository.getTask("taskId") } returns Single.just(getTestTask())
        every {
            mockTasksRepository.addTraceEvent(
                assetId = "assetId",
                taskId = "taskId",
                facilityId = "locationId",
                consumed = false,
                rejectReason = "",
                rejectComment = "",
            )
        } returns Completable.complete()
        every { mockTasksRepository.addToQueue("taskId", "assetId") } returns Completable.complete()
        every { mockScanStateManager.setScanning() } returns Completable.complete()
    }

    @Test
    fun setPipeNo() {
        val expectedViewState = ViewState(
            pipeNo = "5",
        )

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        consumptionIntents.onNext(AssetData(getTestAsset().pipeNumber))
        testScheduler.triggerActions()

        testObserver.assertValueCount(3)
        assertEquals(expectedViewState.pipeNo, testObserver.values().last().pipeNo)
    }

    @Test
    fun setPipeNoFail() {
        val expectedViewState = ViewState(
            pipeNo = "",
        )

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        consumptionIntents.onNext(AssetData(getTestAsset().pipeNumber))
        testScheduler.triggerActions()
        testObserver.assertValueCount(3)
        assertFalse(expectedViewState.pipeNo == testObserver.values().last().pipeNo)
    }

    @Test
    fun setReason() {
        val expectedViewState = ViewState(
            reason = Reason.Other.name,
        )

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        consumptionIntents.onNext(ReasonSelected(Reason.Other))
        testObserver.assertValueCount(0)
        testScheduler.triggerActions()


        assertEquals(expectedViewState.reason, Reason.Other.name)
    }

    @Test
    fun setReasonFail() {
        val expectedViewState = ViewState(
            reason = Reason.Other.name
        )

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        consumptionIntents.onNext(ReasonSelected(Reason.BoxThreadDamage))
        testScheduler.triggerActions()
        testObserver.assertValueCount(4)
        assertFalse(expectedViewState.reason == testObserver.values().last().reason)
    }

    @Test
    fun setStatusChange() {
        val expectedViewState = ViewState(
            statusChange = ConsumedToRejected
        )

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assertEquals(expectedViewState.statusChange, testObserver.values().last().statusChange)
    }

    @Test
    fun setStatusChangeFail() {
        val expectedViewState = ViewState(
            statusChange = RejectedToConsumed
        )

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assertFalse(expectedViewState.statusChange == testObserver.values().last().statusChange)
    }

    @Test
    fun setCommentCache() {
        val testObserverCache = inputCache.edit { copy(second = "comment") }
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testObserverCache.assertValueCount(0)
        testScheduler.triggerActions()

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        consumptionIntents.onNext(CommentUpdated("comment"))
        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assertEquals(inputCache.get().blockingGet().second, "comment")
    }

    @Test
    fun setCommentCacheFail() {
        val testObserverCache = inputCache.edit { copy(second = "commentFail") }
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testObserverCache.assertValueCount(0)
        testScheduler.triggerActions()

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        consumptionIntents.onNext(CommentUpdated("commentFail"))
        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assertFalse(inputCache.get().blockingGet().second == "comment")
    }

    @Test
    fun setReasonCache() {
        val expectedViewState = ViewState(
            reason = Reason.Other.name
        )
        val testObserverCache = inputCache.edit { copy(first = Reason.Other) }
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testObserverCache.assertValueCount(0)
        testScheduler.triggerActions()

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        consumptionIntents.onNext(ReasonSelected(Reason.Other))
        testScheduler.triggerActions()
        testObserver.assertValueCount(3)
        assertEquals(inputCache.get().blockingGet().first?.name, expectedViewState.reason)
    }

    @Test
    fun setReasonCacheFail() {
        val expectedViewState = ViewState(
            reason = Reason.Other.name
        )
        val testObserverCache = inputCache.edit { copy(first = Reason.FailedDraft) }
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testObserverCache.assertValueCount(0)
        testScheduler.triggerActions()

        val testObserver = viewModel.bind(consumptionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        consumptionIntents.onNext(ReasonSelected(Reason.FailedDraft))
        testScheduler.triggerActions()
        testObserver.assertValueCount(3)

        assertFalse(inputCache.get().blockingGet().first?.name == expectedViewState.reason)
    }

    private fun getTestAsset(): Asset {
        return Asset(
            id = "assetId",
            length = 0.0,
            weight = 0.0, // in lbs per ft
            millName = "millName",
            rackLocationId = "rackLocId",
            tags = emptyList(),
            heatNumber = "heatNumber",
            pipeNumber = "5",
            outerDiameter = 0.0,
            grade = "grade",
            range = "range",
            endFinish = "endFinish",
            runningLength = 0.0,
            millWorkNumber = "millWorkNumber",
            productId = "productId",
            commodity = "commodity",
            expectedInOrder = true,
            consumed = false,
            makeUpLossFt = 0.0,
            conditionId = "condition",
            shipmentNumber = "shipmentNumber",
            contractNumber = "contractNumber",
            projectId = "projectId"
        )
    }

    private fun getTestTask(): Task {
        return Task(
            id = "taskId1",
            type = TaskType.BUILD_ORDER,
            status = TaskStatus.IN_PROGRESS,
            totalExpectedLength = 0.0,
            totalNumJoints = 0,
            projectId = "projectId",
            orderId = "orderId",
            orderType = OrderType.OUTBOUND,
            unitOfMeasure = UnitType.FEET,
            specialInstructions = null,
            description = "descriptionTest",
            toLocationId = "locationId",
            toLocationName = "locationName",
            fromLocationId = "fromLocationId",
            fromLocationName = "fromLocationName",
            arrivalDate = "17/06/2021",
            deliveryDate = "17/06/2021",
            dispatchDate = null,
            defaultRackLocationId = "defaultRackLocationId",
            wellSection = "wellSection",
            organizationName = "organizationName"
        )
    }
}