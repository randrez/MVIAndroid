package com.scgts.sctrace.settings.select

import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionMvi
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionMvi.Intent
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionViewModel
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class SettingsSelectionViewModelTest {

    private lateinit var viewModel: SettingsSelectionViewModel
    private var settingsSelectionIntents = PublishSubject.create<Intent>()
    private val mockSettingsManager: SettingsManager = mockk()
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUpViewModel() {
        viewModel = SettingsSelectionViewModel(
            mockk(relaxed = true),
            settingsManager = mockSettingsManager
        )

        testScheduler = TestScheduler()
        every { mockSettingsManager.captureMethod() } returns Observable.just(CaptureMethod.Camera)
        every { mockSettingsManager.setDefaultCaptureMethod(CaptureMethod.Camera) } returns Completable.complete()
    }

    @Test
    fun setCaptureMethod(){
        val expectedViewState = SettingsSelectionMvi.ViewState(
            captureMethod = CaptureMethod.Camera
        )

        val testObserver = viewModel.bind(settingsSelectionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        testScheduler.triggerActions()

        testObserver.assertValueCount(2)
        assertEquals(expectedViewState.captureMethod.name, testObserver.values().last().captureMethod.name)
    }

    @Test
    fun setCaptureMethodFail(){
        val expectedViewState = SettingsSelectionMvi.ViewState(
            captureMethod = CaptureMethod.Manual
        )

        val testObserver = viewModel.bind(settingsSelectionIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        testScheduler.triggerActions()

        testObserver.assertValueCount(2)
        assertFalse(expectedViewState.captureMethod.name == testObserver.values().last().captureMethod.name)
    }
}