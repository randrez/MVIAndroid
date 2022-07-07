package com.scgts.sctrace.database.model

import androidx.room.Entity

@Entity
data class ProjectPartialEntity(
    val id: String,
    val name: String,
    val unitOfMeasure: String,
    val itimsProjectCode1: String?,
    val itimsProjectCode2: String?
)