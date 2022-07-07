package com.scgts.sctrace.base.model

data class AssetProductInformation(
    val productDescription: String,
    val expectedJoints: Int,
    val expectedTally: Double,
    val capturedJoints: Int,
    val capturedTally: Double,
    val contractNumber: String?,
    val shipmentNumber: String?,
    val conditionName: String?,
    val rackLocationName: String?
)