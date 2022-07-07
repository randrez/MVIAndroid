package com.scgts.sctrace.base.model

data class TaskTypeWithNumOfTasks(
    val type: TaskTypeForFiltering,
    val numOfTasks: Int,
)
