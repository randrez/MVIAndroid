package com.scgts.sctrace.assets.confirmation

import com.scgts.sctrace.assets.confirmation.AssetConfirmationMvi.Intent
import com.scgts.sctrace.assets.confirmation.AssetConfirmationMvi.Intent.*
import com.scgts.sctrace.assets.confirmation.AssetConfirmationMvi.ViewState
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.auth.UserPreferences
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.tasks.TasksRepository
import com.scgts.sctrace.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import util.CaptureModeManager
import util.ScanStateManager
import util.TopToastManager

class AssetConfirmationViewModelTest {

    private lateinit var viewModel: AssetConfirmationViewModel
    private var assetConfirmationIntents = PublishSubject.create<Intent>()
    private val mockTasksRepository: TasksRepository = mockk()
    private val mockScanStateManager: ScanStateManager = mockk()
    private val mockSettingsManager: SettingsManager = mockk()
    private val mockCaptureModeManager: CaptureModeManager = mockk()
    private val mockTopToastManager: TopToastManager = mockk()
    private val mockUserRepository: UserRepository = mockk()
    private val mockUserPreferences: UserPreferences = mockk()
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUpViewModel() {
        viewModel = AssetConfirmationViewModel(
            data = mockk(relaxed = true),
            scanStateManager = mockScanStateManager,
            tasksRepository = mockTasksRepository,
            navigator = mockk(relaxed = true),
            userPreferences = mockUserPreferences,
            networkChangeListener = mockk(relaxed = true),
            assetInputCache = mockk(relaxed = true),
            settingsManager = mockSettingsManager,
            userRepository = mockUserRepository,
            captureModeManager = mockCaptureModeManager,
            topToastManager = mockTopToastManager,
            assetDataCache = mockk(relaxed = true),
        )

        testScheduler = TestScheduler()

        every { mockTasksRepository.getAsset(any()) } returns Single.just(getTestAsset())
        every { mockTasksRepository.getProjectConditions(any()) } returns Single.just(
            getTestConditions())
        every { mockTasksRepository.getAssetWithTraceEventData(any(), any()) } returns Single.just(
            getTestAsset())
        every { mockTasksRepository.getConditionById(any()) } returns Single.just(
            getTestCondition())
        every { mockTasksRepository.getDefaultCondition(any()) } returns Single.just(
            getTestCondition())
        every { mockUserRepository.getUser() } returns Single.just(getTestUser()).toObservable()
        every { mockTasksRepository.getTask(any()) } returns Single.just(getTestTask())
        every { mockTasksRepository.getRacksForYard(any()) } returns Single.just(
            getTestRackLocations())
        every { mockTasksRepository.getRackLocationById(any()) } returns Single.just(
            getTestRackLocation())
        every { mockTasksRepository.getAssetLength(any(), any()) } returns Single.just(0.02)
        every { mockSettingsManager.unitType() } returns Single.just(UnitType.FEET).toObservable()
        every { mockTasksRepository.getRacksForYard(any()) } returns Single.just(
            getTestRackLocations())
        every { mockTasksRepository.getProjectRackLocations(any()) } returns Single.just(
            getTestRackLocations())
        every { mockTasksRepository.validateTaskIsOutboundDispatchOrBuildOrder(any()) } returns Single.just(
            true)
        every {
            mockTasksRepository.getConditionByIdAndProjectId(any(),
                any())
        } returns Single.just(getTestCondition())
        every {
            mockTasksRepository.removeCapturedAsset(any(),
                any())
        } returns Completable.complete()
        every { mockScanStateManager.setScanning() } returns Completable.complete()
        every { mockScanStateManager.setPause() } returns Completable.complete()
        every { mockTopToastManager.showAssetAddedToast() } returns Completable.complete()
        every { mockTopToastManager.setShowAssetAddedToastFlag(true) } returns Completable.complete()
        every { mockUserRepository.getUserRolesByProject(any()) } returns Single.just(
            UserRole(isDrillingEngineer = true, isYardOperator = false, isAuditor = false)
        )
    }

    @Test
    fun setName() {
        val expectedViewState = ViewState(
            name = getTestAsset().productDescription(),
            isAdHoc = false,
            newAsset = true,
            unitType = UnitType.FEET
        )

        val testObserver = viewModel.bind(assetConfirmationIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assetConfirmationIntents.onNext(AssetData(getTestAsset(), true))

        testScheduler.triggerActions()

        //verify
        testObserver.assertValueCount(7)
        Assert.assertEquals(expectedViewState.name, getTestAsset().productDescription())
    }

    @Test
    fun setNameFail() {
        val expectedViewState = ViewState(
            name = "Fail",
            isAdHoc = false,
            newAsset = true,
            unitType = UnitType.FEET
        )

        val testObserver = viewModel.bind(assetConfirmationIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assetConfirmationIntents.onNext(AssetData(getTestAsset(), true))

        testScheduler.triggerActions()

        //verify
        testObserver.assertValueCount(7)
        assertFalse(expectedViewState.name == getTestAsset().productDescription())
    }

    @Test
    fun setSelectedCondition() {
        val expectedViewState = ViewState(
            selectedCondition = getTestCondition(),
            isAdHoc = false,
            newAsset = true,
            unitType = UnitType.FEET
        )

        val testObserver = viewModel.bind(assetConfirmationIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assetConfirmationIntents.onNext(ConditionSelected(condition = getTestCondition()))
        assetConfirmationIntents.onNext(SetInputData(condition = getTestCondition(),
            rackLocation = getTestRackLocation(),
            length = getTestLength()))

        testScheduler.triggerActions()

        //verify
        testObserver.assertValueCount(7)
        assertEquals(expectedViewState.selectedCondition?.name,
            getTestCondition().name)
    }

    @Test
    fun setSelectedConditionFail() {
        val expectedViewState = ViewState(
            selectedCondition = null,
            isAdHoc = false,
            newAsset = true,
            unitType = UnitType.FEET
        )

        val testObserver = viewModel.bind(assetConfirmationIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assetConfirmationIntents.onNext(ConditionSelected(condition = getTestCondition()))
        assetConfirmationIntents.onNext(SetInputData(condition = getTestCondition(),
            rackLocation = getTestRackLocation(),
            length = getTestLength()))

        testScheduler.triggerActions()

        //verify
        testObserver.assertValueCount(7)
        assertFalse(expectedViewState.selectedCondition?.name == getTestCondition().name)
    }

    @Test
    fun setSelectedLocation() {

        val expectedViewState = ViewState(
            selectedLocation = getTestRackLocation(),
            isAdHoc = false,
            newAsset = true,
            unitType = UnitType.FEET
        )

        val testObserver = viewModel.bind(assetConfirmationIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assetConfirmationIntents.onNext(LocationSelected(rackLocation = getTestRackLocation()))
        assetConfirmationIntents.onNext(SetInputData(condition = getTestCondition(),
            rackLocation = getTestRackLocation(),
            length = getTestLength()))

        testScheduler.triggerActions()

        //verify
        testObserver.assertValueCount(7)
        assertEquals(expectedViewState.selectedLocation?.name,
            getTestRackLocation().name)
    }

    @Test
    fun setConditions() {
        val expectedViewState = ViewState(
            conditions = getTestConditions(),
            isAdHoc = false,
            newAsset = true,
            unitType = UnitType.FEET
        )

        val testObserver = viewModel.bind(assetConfirmationIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assetConfirmationIntents.onNext(Conditions(conditions = getTestConditions()))
        testScheduler.triggerActions()

        //verify
        testObserver.assertValueCount(7)
        assertEquals(expectedViewState.conditions.size,
            testObserver.values().last().conditions.size)
    }

    @Test
    fun setRackLocationData() {
        val expectedViewState = ViewState(
            rackLocations = getTestRackLocations(),
            isAdHoc = false,
            newAsset = true,
            unitType = UnitType.FEET
        )

        val testObserver = viewModel.bind(assetConfirmationIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        assetConfirmationIntents.onNext(RackLocationData(rackLocations = getTestRackLocations()))
        testScheduler.triggerActions()

        //verify
        testObserver.assertValueCount(7)
        assertEquals(expectedViewState.rackLocations.size,
            testObserver.values().last().rackLocations.size)
    }

    private fun getTestAsset(): Asset {
        return Asset(
            id = "assetId",
            length = 0.0,
            weight = 0.0, // in lbs p er ft
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
            projectId = "projectId",
        )
    }

    private fun getTestCondition(): Condition {
        return Condition(
            id = "condition1",
            name = "conditionName1"
        )
    }

    private fun getTestConditions(): List<Condition> {
        val condition1 = Condition(
            id = "condition1",
            name = "conditionName1"
        )

        val condition2 = Condition(
            id = "condition2",
            name = "conditionName2"
        )

        return arrayListOf(condition1, condition2)
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
            unitOfMeasure = UnitType.FEET,
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

    private fun getTestUser(): User {
        return User(
            id = "userTestId",
            name = "nameUser",
            email = "emailUser"
        )
    }

    private fun getTestRackLocations(): List<RackLocation> {
        val rackLocation1 = RackLocation(
            id = "rack1",
            name = "rackLocation1"
        )

        val rackLocation2 = RackLocation(
            id = "rack2",
            name = "rackLocation2"
        )

        return arrayListOf(rackLocation1, rackLocation2)
    }

    private fun getTestRackLocation(): RackLocation {
        return RackLocation(
            id = "rack1",
            name = "rackLocation1"
        )
    }

    private fun getTestLength(): Length {
        return Length(30.3458, mockSettingsManager.unitType().blockingFirst())
    }
}