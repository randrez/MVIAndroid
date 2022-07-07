package com.scgts.sctrace.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scgts.sctrace.base.model.OrderType
import com.scgts.sctrace.base.model.TaskStatus
import com.scgts.sctrace.base.model.TaskType
import com.scgts.sctrace.base.model.TaskTypeForFiltering
import org.threeten.bp.ZonedDateTime

@Entity(
    indices = [Index(
        value = ["id"],
        unique = true
    )]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val createdAt: ZonedDateTime,
    val type: TaskType,
    val typeForFiltering: TaskTypeForFiltering,
    val totalExpectedLength: Double,
    val totalNumJoints: Int,
    val projectId: String,
    val orderId: String,
    val status: TaskStatus,
    val orderType: OrderType,
    val unitOfMeasure: String,
    val specialInstructions: String?,
    val description: String?,
    val toLocationId: String?,
    val toLocationName: String?,
    val fromLocationId: String?,
    val fromLocationName: String?,
    val deliveryDate: String?,
    val arrivalDate: String?,
    val dispatchDate: String?,
    val defaultRackLocationId: String?,
    val wellSection: String? = null,
    val organizationName: String? = null,
    val assetIdsFromPreviousTask: List<String> = emptyList(),
)
