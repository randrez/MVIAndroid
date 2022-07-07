package com.scgts.sctrace.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scgts.sctrace.base.model.FacilityType

@Entity(
    indices = [Index(
        value = ["id"],
        unique = true
    )]
)
data class FacilityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val facilityType: FacilityType,
    val selectable: Boolean
)