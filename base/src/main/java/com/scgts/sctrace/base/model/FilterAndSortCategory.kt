package com.scgts.sctrace.base.model

import com.scgts.sctrace.base.model.FilterAndSortOption.*

sealed class FilterAndSortCategory(
    val name: String,
    val options: List<FilterAndSortOption>,
) {
    object None : FilterAndSortCategory("Filter & Sort", emptyList())

    data class Sort(val list: List<SortOption> = emptyList()) :
        FilterAndSortCategory("Sort by", list)

    data class Project(val list: List<FilterOption> = emptyList()) :
        FilterAndSortCategory("Project", list)

    data class TaskType(val list: List<FilterOption> = emptyList()) :
        FilterAndSortCategory("Task", list)

    data class Status(val list: List<StatusFilterOption> = emptyList()) :
        FilterAndSortCategory("Status", list)

    data class FromLocation(val list: List<LocationFilterOption> = emptyList()) :
        FilterAndSortCategory("From", list)

    data class ToLocation(val list: List<LocationFilterOption> = emptyList()) :
        FilterAndSortCategory("To", list)

    fun clearEnabled(): Boolean {
        return when (this) {
            is None -> false
            is Sort -> list.find { it.checked }?.category != SortCategory.defaultCategory()
            is Project, is TaskType, is Status, is FromLocation, is ToLocation -> options.any { it.checked }
        }
    }

    fun selectedOptions(): List<String> = options.filter { it.checked }.map { it.name }

    companion object {
        fun toList(): List<FilterAndSortCategory> {
            return listOf(Sort(), Project(), TaskType(), Status(), FromLocation(), ToLocation())
        }
    }
}