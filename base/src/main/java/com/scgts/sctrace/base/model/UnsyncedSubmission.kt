package com.scgts.sctrace.base.model

import org.threeten.bp.ZonedDateTime

data class UnsyncedSubmission(
    val taskOrderType: String,
    val taskTypeString: String,
    val taskDescription: String,
    val capturedAt: ZonedDateTime?,
    val assetCount: Int,
    val tallyText: String,
    val timeLabel: String
)
