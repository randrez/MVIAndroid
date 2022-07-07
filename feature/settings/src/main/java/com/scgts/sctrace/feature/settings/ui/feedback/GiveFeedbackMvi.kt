package com.scgts.sctrace.feature.settings.ui.feedback

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.FeedbackOption
import com.scgts.sctrace.base.model.FeedbackOption.*
import com.scgts.sctrace.feature.settings.ui.SettingsMvi

interface GiveFeedbackMvi {
    sealed class Intent : MviIntent {
        object OnCancelPressed : Intent()
        data class ValidateFormFeedback(
            val isEnabled: Boolean,
            val feedbackType: FeedbackOption?,
            val severity: FeedbackOption?,
            val inputDetails: String
        ) : Intent()

        object OnSubmitPressed : Intent()
        object OfflineSubmitted : Intent()
        object NavigateToTasksOnPostSubmit : Intent()
        object NoOp : Intent()

        data class FeedbackTypeSelected(val feedbackType: FeedbackOption) : Intent()
        data class SeveritySelected(val severity: FeedbackOption) : Intent()
        data class InputDetails(val detailsValue: String) : Intent()
    }

    data class ViewState(
        val feedbackTypes: List<FeedbackOption> = FEEDBACK_TYPE,
        val severities: List<FeedbackOption> = SEVERITY,
        val feedbackType: FeedbackOption? = null,
        val severity:FeedbackOption? = null,
        val detailsValue: String = "",
        val enableSubmit: Boolean = false,
        val isOfflineSubmitted: Boolean = false,
        override val loading: Boolean = false,
        override val error: Throwable? = null,
    ) : MviViewState

    companion object {
        val FEEDBACK_TYPE = listOf(
            ReportABug,
            NewFeatureRequest,
            SubmitAQuestion,
            GeneralFeedback
        )
        val SEVERITY = listOf(
            Critical, High, Medium, Low
        )
    }
}