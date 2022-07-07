package com.scgts.sctrace.assets.length

import com.scgts.sctrace.assets.confirmation.AssetInputCache
import com.scgts.sctrace.assets.confirmation.AssetInputs
import com.scgts.sctrace.assets.length.EditAssetLengthMvi.Intent
import com.scgts.sctrace.assets.length.EditAssetLengthMvi.Intent.IsEditing
import com.scgts.sctrace.assets.length.EditAssetLengthMvi.ViewState
import com.scgts.sctrace.base.model.Length
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.UnitType
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class EditAssetLengthViewModelTest {
    private lateinit var viewModel: EditAssetLengthViewModel
    private var assetIntents = PublishSubject.create<Intent>()
    private val mockInputCache: AssetInputCache = mockk()
    private val mockSettingsManager: SettingsManager = mockk()
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUpViewModel() {
        viewModel = EditAssetLengthViewModel(
            mockk(relaxed = true),
            mockInputCache,
            mockSettingsManager
        )
        testScheduler = TestScheduler()

        every { mockSettingsManager.unitType() } returns Single.just(UnitType.FEET).toObservable()
        every { mockInputCache.getObservable() } returns Single.just(AssetInputs(length = getTestLength()))
            .toObservable()
    }

    @After
    fun cleanUp() {
        assetIntents = PublishSubject.create()
        testScheduler.shutdown()
    }

    @Test
    fun setAssetLength() {
        //setup
        val expectedViewState = ViewState(
            length = getTestLength(),
            unitType = mockSettingsManager.unitType().blockingFirst()
        )

        val testObserver = viewModel.bind(assetIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        //execute

        //assetInputCache.getObservables dataIntent fires in setUpViewModel which fires SetLength Intent
        testScheduler.triggerActions()

        //verify
        testObserver.assertValueCount(2)
        assertEquals(expectedViewState.length, testObserver.values().last().length)
    }

    @Test
    fun setAssetLength_fail() {
        //setup
        val expectedViewState = ViewState(
            unitType = mockSettingsManager.unitType().blockingFirst()
        )

        val testObserver = viewModel.bind(assetIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        //execute

        //assetInputCache.getObservables dataIntent fires in setUpViewModel which fires SetLength Intent
        testScheduler.triggerActions()

        //verify
        testObserver.assertValueCount(2)
        assertFalse(expectedViewState.length == testObserver.values().last().length)
    }

    @Test
    fun isEditing() {
        //setup
        val expectedViewState = ViewState(
            isEditing = true,
            unitType = mockSettingsManager.unitType().blockingFirst()
        )

        val testObserver = viewModel.bind(assetIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)
        testScheduler.triggerActions()

        //execute
        assetIntents.onNext(IsEditing(true))
        testObserver.assertValueCount(2)
        testScheduler.triggerActions()

        //verify
        assertEquals(expectedViewState.isEditing, testObserver.values().last().isEditing)
    }

    private fun getTestLength(): Length =
        Length(30.3458, mockSettingsManager.unitType().blockingFirst())
}