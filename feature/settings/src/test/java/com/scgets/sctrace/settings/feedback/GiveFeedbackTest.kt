package com.scgets.sctrace.settings.feedback


import com.scgts.sctrace.feature.settings.ui.feedback.FeedbackInput
import com.scgts.sctrace.feature.settings.ui.feedback.FeedbackInputCache
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackViewModel
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.queue.QueueRepository

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test

class GiveFeedbackTest {

    lateinit var model: GiveFeedbackViewModel
    private val intents = PublishSubject.create<GiveFeedbackMvi.Intent>()
    private val mockNavigator = mockk<AppNavigator>(relaxed = true)
    private val feedbackInputCache = mockk<FeedbackInputCache>(relaxed = true)
    private val mockRepository = mockk<QueueRepository>(relaxed = true)
    private val mockListener = mockk<NetworkChangeListener>(relaxed =true)
    val testScheduler = TestScheduler()

    @Test
    fun feedbackIsSubmitted() {
        //setup
        model = GiveFeedbackViewModel(mockNavigator, feedbackInputCache, mockRepository, mockListener)
        every { feedbackInputCache.get() } returns Single.just(FeedbackInput("Medium", "Very", "Details"))

        //execute
        val testObserver = model.bind(intents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testScheduler.triggerActions()

        intents.onNext(GiveFeedbackMvi.Intent.OnSubmitPressed)
        //verify
        testObserver.assertValueCount(1)
        verify { mockRepository.addFeedbackToMiscQueue("Medium", "Very", "Details") }
        verify { mockListener.isConnected() }
    }
}