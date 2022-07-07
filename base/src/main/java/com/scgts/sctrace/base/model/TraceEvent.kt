package com.scgts.sctrace.base.model

import org.threeten.bp.ZonedDateTime

data class TraceEvent(
    val taskId: String,
    val assetId: String,
    val capturedAt: ZonedDateTime?,
    val conditionId: String?,
    val rackLocationId: String?,
    val consumed: Boolean? = null,
    val laserLength: Double? = null,
    val userId: String,
    val userName: String
)