package com.scgts.sctrace.database.converter

import androidx.room.TypeConverter
import com.scgts.sctrace.base.model.TaskStatus
import com.scgts.sctrace.base.model.TaskStatus.*

class TaskStatusConverter {
    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String {
        return value.dbName
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus? {
        return when (value) {
            NOT_STARTED.dbName -> NOT_STARTED
            IN_PROGRESS.dbName -> IN_PROGRESS
            PENDING.dbName -> PENDING
            IN_REVIEW.dbName -> IN_REVIEW
            COMPLETED.dbName -> COMPLETED
            else -> null
        }
    }
}