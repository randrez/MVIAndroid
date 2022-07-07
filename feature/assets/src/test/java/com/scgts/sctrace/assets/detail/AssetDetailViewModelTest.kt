package com.scgts.sctrace.assets.detail

import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.tasks.TasksRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Test

class AssetDetailViewModelTest {

    lateinit var model: AssetDetailViewModel
    private val assetIntents = PublishSubject.create<AssetDetailMvi.Intent>()
    private val mockRepo: TasksRepository = mockk()
    private val mockSettingsManager: SettingsManager = mockk()

    val testScheduler = TestScheduler()

    @Test
    fun assetDescriptionNaming() {

        //setup
        val expectedViewState = descriptionSetup("0.0 0.0 grade endFinish range commodity")

        val testObserver = model.bind(assetIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testScheduler.triggerActions()

        val asset = getTestAsset()

        //execute

        assetIntents.onNext(
            AssetDetailMvi.Intent.AssetData(
                asset.toAssetDetailList(
                    condition = "",
                    rackLocation = "",
                    unitType = mockSettingsManager.unitType().blockingFirst(),
                    showShipmentContract = false
                ),
                asset.productDescription()
            )
        )

        //verify
        testObserver.assertValueCount(2)
        verifyViewState(testObserver.values().last(), expectedViewState)

    }

    @Test
    fun assetDescription_fail() {
        //setup
        val expectedViewState = descriptionSetup("shouldFail")

        val testObserver = model.bind(assetIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testScheduler.triggerActions()

        val asset = getTestAsset()

        //execute

        assetIntents.onNext(
            AssetDetailMvi.Intent.AssetData(
                asset.toAssetDetailList(
                    condition = "",
                    rackLocation = "",
                    unitType = mockSettingsManager.unitType().blockingFirst(),
                    showShipmentContract = false
                ),
                asset.productDescription()
            )
        )

        //verify
        testObserver.assertValueCount(2)
        assertFalse(
            testObserver.values().last().assetDescription == expectedViewState.assetDescription
        )
    }

    private fun descriptionSetup(description: String): AssetDetailMvi.ViewState {
        model = AssetDetailViewModel(
            "testId",
            taskId = "taskId",
            tasksRepository = mockRepo,
            navigator = mockk(relaxed = true),
            settingsManager = mockSettingsManager,
        )
        val expectedViewState = AssetDetailMvi.ViewState(
            assetDescription = description
        )

        every { mockRepo.getAssetWithTraceEventData("testId", "taskId") } returns Single.just(
            getTestAsset()
        )
        every { mockRepo.getConditionByIdAndProjectId(any(), any()) } returns Single.just(Condition(
            "",
            ""))
        every { mockRepo.getRackLocationById(any()) } returns Single.just(RackLocation("", ""))
        every { mockSettingsManager.unitType() } returns Observable.just(UnitType.FEET)
        every { mockRepo.getTask(any()) } returns Single.just(getTestTask())
        every { mockRepo.validateTaskIsOutboundDispatchOrBuildOrder(any()) } returns Single.just(true)
        return expectedViewState
    }

    private fun verifyViewState(
        viewStateActual: AssetDetailMvi.ViewState,
        viewStateExpected: AssetDetailMvi.ViewState
    ) {
        assertEquals(viewStateActual.assetDescription, viewStateExpected.assetDescription)
    }

    private fun getTestAsset(): Asset {
        return Asset(
            id = "testId",
            length = 0.0,
            weight = 0.0, // in lbs per ft
            millName = "millName",
            rackLocationId = "rackLocId",
            tags = emptyList(),
            heatNumber = "heatNumber",
            pipeNumber = "pipeNumber",
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