package com.scgts.sctrace.base.model

sealed class FeedbackOption(override val id: String, override val name: String) :
    Identifiable, Named {
    object ReportABug : FeedbackOption(id = "report_a_bug", name = "Report a Bug")
    object NewFeatureRequest :
        FeedbackOption(id = "new_feature_request", name = "New Feature Request")

    object SubmitAQuestion : FeedbackOption(id = "submit_a_question", name = "Submit a Question")
    object GeneralFeedback : FeedbackOption(id = "general_feedback", name = "General Feedback")
    object Critical : FeedbackOption(id = "critical", name = "Critical")
    object High : FeedbackOption(id = "high", name = "High")
    object Medium : FeedbackOption(id = "medium", name = "Medium")
    object Low : FeedbackOption(id = "low", name = "Low")
}