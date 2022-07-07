package com.scgts.sctrace.base.model

data class DetailFeedbackDtrace(
    val type: String,
    val appVersionNumber: String,
    val timestamp: String,
    val deviceType: String,
    val userEmail: String,
    val projectCodes: String,
    val roles: String,
    val userName: String
) {
    fun getInformationDetailFeedbackDtrace() =
        "App Version Number: $appVersionNumber, Timestamp: $timestamp, Device Type: $deviceType, User Email: $userEmail, Project Code(s): $projectCodes, User Role(s):$roles"

    fun getTitleDetailFeedbackDtrace() = "Feedback $userName"
}
