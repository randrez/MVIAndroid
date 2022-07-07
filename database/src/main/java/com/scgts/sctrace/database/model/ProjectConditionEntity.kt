package com.scgts.sctrace.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["projectId", "conditionId"])
data class ProjectConditionEntity(
    val projectId: String,
    val conditionId: String,
)