package com.scgts.sctrace.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["projectId", "facilityId"])
data class ProjectFacilityEntity(
    val projectId: String,
    val facilityId: String
)
