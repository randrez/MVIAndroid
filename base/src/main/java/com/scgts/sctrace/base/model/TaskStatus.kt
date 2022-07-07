package com.scgts.sctrace.base.model

enum class TaskStatus(val uiName: String, val serverName: String) {
    NOT_STARTED("Not Started", "NOT_STARTED"),
    IN_PROGRESS("In Progress", "IN_PROGRESS"),
    PENDING("Pending", "PENDING"),              // This is a mobile only status.
    IN_REVIEW("In Review", "REVIEW"),
    COMPLETED("Completed", "COMPLETED");         // Technically mobile should never get this status.

    val dbName = "${ordinal}_${name}"

    companion object {
        private val map = values().associateBy(TaskStatus::serverName)
        operator fun get(serverName: String) = map[serverName] ?: COMPLETED

        fun toList(): List<TaskStatus> = listOf(
            NOT_STARTED,
            IN_PROGRESS,
            PENDING,
            IN_REVIEW,
        )
    }
}