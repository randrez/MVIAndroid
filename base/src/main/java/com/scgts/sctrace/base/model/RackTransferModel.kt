package com.scgts.sctrace.base.model

data class RackTransferModel(
    val productDescription: String,
    val millWorkNum: String = "",
    val rackLocationId: String = "",
    val rackLocationName: String = "",
    val totalJointsAndLength: String = "",
)