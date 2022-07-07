package com.scgts.sctrace.base.model

sealed class FilterAndSortOption(val name: String, val checked: Boolean) {
    data class SortOption(
        private val sortName: String,
        private val selected: Boolean,
        val category: SortCategory,
    ) : FilterAndSortOption(sortName, selected)

    data class FilterOption(
        private val filterName: String,
        private val selected: Boolean,
        val numOfTasks: Int,
        val filterId: String,
    ) : FilterAndSortOption(filterName, selected)

    data class StatusFilterOption(
        val status: TaskStatus,
        private val selected: Boolean,
        val numOfTasks: Int,
    ) : FilterAndSortOption(status.uiName, selected)

    data class LocationFilterOption(
        private val locationName: String,
        private val selected: Boolean,
        val facilityType: FacilityType,
        val numOfTasks: Int,
        val facilityId: String,
    ) : FilterAndSortOption(locationName, selected)
}