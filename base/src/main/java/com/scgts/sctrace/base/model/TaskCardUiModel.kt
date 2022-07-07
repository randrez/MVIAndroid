package com.scgts.sctrace.base.model

data class TaskCardUiModel(
    val id: String,
    val label: String,
    val name: String,
    val status: TaskStatus,
    val descriptionOne: TextEntry,
    val descriptionTwo: TextEntry?,
    val showWarningIcon: Boolean
)