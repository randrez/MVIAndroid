package com.scgts.sctrace.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(
    indices = [Index(
        value = ["id"],
        unique = true
    )]
)
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val unitOfMeasure: String,
    val lastUpdated: ZonedDateTime? = null,
    val itimsProjectCode1: String?,
    val itimsProjectCode2: String?
)