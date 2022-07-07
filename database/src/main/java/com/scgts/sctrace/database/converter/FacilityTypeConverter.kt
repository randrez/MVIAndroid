package com.scgts.sctrace.database.converter

import androidx.room.TypeConverter
import com.scgts.sctrace.base.model.FacilityType

class FacilityTypeConverter {
    @TypeConverter
    fun fromFacilityType(value: FacilityType): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toFacilityType(value: Int): FacilityType? {
        return when(value) {
            0 -> FacilityType.MILL
            1 -> FacilityType.YARD
            2 -> FacilityType.RIG
            3 -> FacilityType.WELL
            else -> null
        }
    }
}