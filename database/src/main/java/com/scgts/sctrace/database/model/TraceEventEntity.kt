package com.scgts.sctrace.database.model

import androidx.room.Entity
import com.scgts.sctrace.base.model.SubmitStatus
import org.threeten.bp.ZonedDateTime

@Entity(primaryKeys = ["taskId", "assetId"])
data class TraceEventEntity(
    val taskId: String,
    val assetId: String,
    val submitStatus: SubmitStatus,
    val facilityId: String,
    val rackLocationId: String? = null,
    val fromLocationId: String? = null,
    val toLocationId: String? = null,
    val updatedAt: ZonedDateTime? = null,
    val scannedAt: ZonedDateTime? = null,
    val conditionId: String? = null,
    val laserLength: Double? = null,
    val checkedForOutbound: Boolean = true,
    val consumed: Boolean? = null,
    val rejectReason: String? = null,
    val rejectComment: String? = null,
    val adHocActionTaskType: String? = null,
    val userId: String,
    val userName: String
)
