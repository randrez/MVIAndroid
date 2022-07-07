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
data class ConditionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val conditionCode: String,
)
