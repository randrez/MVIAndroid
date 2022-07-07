package com.scgts.sctrace.database.converter

import androidx.room.TypeConverter
import com.scgts.sctrace.base.model.SubmitStatus
import com.scgts.sctrace.base.model.SubmitStatus.*

class SubmitStatusConverter {
    @TypeConverter
    fun fromStatus(value: SubmitStatus): String {
        return value.dbName
    }

    @TypeConverter
    fun toStatus(value: String): SubmitStatus? {
        return when (value) {
            NOT_SUBMITTED.dbName -> NOT_SUBMITTED
            PENDING.dbName -> PENDING
            SUBMITTED.dbName -> SUBMITTED
            else -> null
        }
    }
}
