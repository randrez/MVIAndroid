package com.scgts.sctrace.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(
        value = ["id"],
        unique = true
    )]
)
data class ExpectedAmountEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val productId: String,
    val productDescription: String,
    val expectedTally: Double,
    val expectedJoints: Int,
    val contractNumber: String?,
    val shipmentNumber: String?,
    val conditionId: String?,
    val rackLocationId: String?
)
