package com.scgts.sctrace.base.model

data class UserFeedbackPayload(
    val feedbackType: String,
    val feedbackSeverity: String?,
    val feedback: String,
    val timeStamp: Long,
)

