package com.scgts.sctrace.base.model

data class AssetProductInformationCardUiModel(
    val productDescription: String,
    val contractNumber: String?,
    val shipmentNumber: String?,
    val conditionName: String?,
    val rackLocationName: String?,
    val percentCompletion: Float,
    val expectedTally: String,
    val totalTally: String
)
