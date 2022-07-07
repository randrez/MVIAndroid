package com.scgts.sctrace.base.model

data class Tallies(
    val total: Double,
    val totalConsumed: Double,
    val totalMakeUpLoss: Double,
    val totalRejected: Double,
    val totalJoints: Int,
    val consumedJoints: Int,
    val rejectedJoints: Int,
    val totalConsumedRunningLength: Double,
    val totalRunningLength: Double,
)
