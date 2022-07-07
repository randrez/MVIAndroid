package com.scgts.sctrace.base.model

data class TasksFilterAndSort(
    var sortCategory: SortCategory = SortCategory.defaultCategory(),
    var projectFilter: List<String> = emptyList(),      // List of Project IDs
    var taskFilter: List<String> = emptyList(),         // List of Task Types
    var statusFilter: List<String> = emptyList(),       // List of Task Statuses
    var fromFilter: List<String> = emptyList(),         // List of Facility IDs
    var toFilter: List<String> = emptyList(),           // List of Facility IDs
)