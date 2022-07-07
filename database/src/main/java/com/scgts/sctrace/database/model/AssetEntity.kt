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
data class AssetEntity(
    @PrimaryKey val id: String,
    val pipeNumber: String,
    val heatNumber: String,
    val commodity: String,
    val millWorkNumber: String,
    val exMillDate: String,
    val length: Double,
    val weight: Double, // in lbs per ft
    val projectId: String,
    val runningLength: Double,
    val endFinish: String,
    val grade: String,
    val range: String,
    val outerDiameter: Double,
    val millName: String,
    val rackLocationId: String?,
    val tags: List<String>,
    val productId: String,
    val makeUpLossFt: Double,
    val conditionId: String?,
    val shipmentNumber: String?,
    val contractNumber: String
)