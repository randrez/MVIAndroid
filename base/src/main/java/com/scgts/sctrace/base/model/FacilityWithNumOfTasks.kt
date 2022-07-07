package com.scgts.sctrace.base.model

data class FacilityWithNumOfTasks(
    val id: String,
    val name: String,
    val numOfTasks: Int,
    val facilityType: FacilityType,
)
