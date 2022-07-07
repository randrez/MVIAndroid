package com.scgts.sctrace.settings.feedback

import com.scgts.sctrace.base.model.FeedbackOption
import com.scgts.sctrace.feature.settings.ui.feedback.FeedbackInput
import com.scgts.sctrace.feature.settings.ui.feedback.FeedbackInputCache
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.Intent
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.Intent.ValidateFormFeedback
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.ViewState
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackViewModel
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.queue.QueueRepository
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

class GiveFeedbackViewModelTest {

    private lateinit var viewModel: GiveFeedbackViewModel
    private var giveFeedbackIntents = PublishSubject.create<Intent>()
    private val mockFeedbackCache: FeedbackInputCache = mockk()
    private lateinit var testScheduler: TestScheduler
    private val mockQueueRepository: QueueRepository = mockk()
    private val mockNetworkListener: NetworkChangeListener = mockk()

    @Before
    fun setUpViewModel() {
        viewModel = GiveFeedbackViewModel(
            mockk(relaxed = true),
            mockFeedbackCache,
            mockQueueRepository,
            mockNetworkListener
        )
        testScheduler = TestScheduler()

        every {
            mockFeedbackCache.edit {
                feedbackType = "Medium"
                this
            }
        } returns Completable.complete()
        every {
            mockFeedbackCache.edit {
                details = "Details"
                this
            }
        } returns Completable.complete()
        every {
            mockFeedbackCache.edit {
                severity = "Very"
                this
            }
        } returns Completable.complete()
        every { mockFeedbackCache.get() } returns Single.just(FeedbackInput("Medium",
            "Very",
            "Details"))
        every { mockFeedbackCache.getObservable() } returns Single.just(giveFeedbackFormTest())
            .toObservable()
    }

    @Test
    fun setGiveFeedbackForm() {
        val expectedViewState = ViewState(
            feedbackTypeValue = mockFeedbackCache.getObservable().blockingSingle().feedbackType,
            severityValue = mockFeedbackCache.getObservable().blockingSingle().severity,
            detailsValue = mockFeedbackCache.getObservable().blockingSingle().details,
        )

        val testObserver = viewModel.bind(giveFeedbackIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        testScheduler.triggerActions()

        testObserver.assertValueCount(2)
        assertEquals(expectedViewState.feedbackTypeValue,
            testObserver.values().last().feedbackTypeValue)
        assertEquals(expectedViewState.severityValue, testObserver.values().last().severityValue)
        assertEquals(expectedViewState.detailsValue, testObserver.values().last().detailsValue)
    }

    @Test
    fun setGiveFeedbackFormFail() {
        val expectedViewState = ViewState(
            feedbackTypeValue = "",
            severityValue = "",
            detailsValue = "",
        )

        val testObserver = viewModel.bind(giveFeedbackIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        testScheduler.triggerActions()
        testObserver.assertValueCount(2)
        assertFalse(expectedViewState.feedbackTypeValue == testObserver.values()
            .last().feedbackTypeValue)
        assertFalse(expectedViewState.severityValue == testObserver.values()
            .last().severityValue)
        assertFalse(expectedViewState.detailsValue == testObserver.values().last().detailsValue)
    }

    @Test
    fun isEnableSubmit() {
        val expectedViewState = ViewState(
            enableSubmit = true,
            feedbackTypeValue = giveFeedbackFormTest().feedbackType,
            severityValue = giveFeedbackFormTest().severity,
            detailsValue = giveFeedbackFormTest().details
        )

        val testObserver = viewModel.bind(giveFeedbackIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        testScheduler.triggerActions()
        testObserver.assertValueCount(2)
        giveFeedbackIntents.onNext(ValidateFormFeedback(isEnabled = giveFeedbackFormTest().feedbackType.isNotEmpty() && giveFeedbackFormTest().details.isNotEmpty(),
            feedbackTypeValue = giveFeedbackFormTest().feedbackType,
            severityValue = giveFeedbackFormTest().severity,
            inputDetails = giveFeedbackFormTest().details))
        testScheduler.triggerActions()

        assertEquals(expectedViewState.enableSubmit, testObserver.values().last().enableSubmit)
    }

    @Test
    fun isEnableSubmitFail() {
        val expectedViewState = ViewState(
            enableSubmit = true,
            feedbackTypeValue = giveFeedbackFormTest().feedbackType,
            severityValue = giveFeedbackFormTest().severity,
            detailsValue = giveFeedbackFormTest().details
        )

        val testObserver = viewModel.bind(giveFeedbackIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        testScheduler.triggerActions()
        testObserver.assertValueCount(2)
        giveFeedbackIntents.onNext(ValidateFormFeedback(isEnabled = giveFeedbackFormTestFail().feedbackType.isNotEmpty() && giveFeedbackFormTestFail().details.isNotEmpty(),
            feedbackTypeValue = giveFeedbackFormTestFail().feedbackType,
            severityValue = giveFeedbackFormTestFail().severity,
            inputDetails = giveFeedbackFormTestFail().details))
        testScheduler.triggerActions()

        assertFalse(expectedViewState.enableSubmit == testObserver.values().first().enableSubmit)
    }

    private fun giveFeedbackFormTest(): FeedbackInput {
        return FeedbackInput(
            feedbackType = FeedbackOption.ReportABug.name,
            severity = FeedbackOption.High.name,
            details = "Details"
        )
    }

    private fun giveFeedbackFormTestFail(): FeedbackInput {
        return FeedbackInput(
            feedbackType = "",
            severity = FeedbackOption.High.name,
            details = "Details"
        )
    }
}