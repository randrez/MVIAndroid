package com.scgts.sctrace.database.converter

import androidx.room.TypeConverter
import com.scgts.sctrace.base.model.TaskTypeForFiltering
import com.scgts.sctrace.base.model.TaskTypeForFiltering.*

class TaskTypeForFilteringConverter {
    @TypeConverter
    fun fromTaskType(value: TaskTypeForFiltering): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toTaskType(value: Int): TaskTypeForFiltering? {
        return when (value) {
            0 -> AD_HOC
            1 -> BUILD_ORDER
            2 -> CONSUME
            3 -> DISPATCH
            4 -> INBOUND_FROM_MILL
            5 -> INBOUND_FROM_WELL_SITE
            6 -> INBOUND_TO_WELL
            7 -> RACK_TRANSFER
            else -> null
        }
    }
}