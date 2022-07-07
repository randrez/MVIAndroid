package com.scgts.sctrace.feature.landing.filter_and_sort

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.FilterAndSortCategory
import com.scgts.sctrace.base.model.FilterAndSortCategory.None
import com.scgts.sctrace.base.model.FilterAndSortOption

interface FilterAndSortMvi {
    sealed class Intent : MviIntent {
        //view intents
        object BackClicked : Intent()
        object CloseClicked : Intent()
        object ShowTasksClicked : Intent()
        data class ClearClicked(val category: FilterAndSortCategory) : Intent()
        data class CategorySelected(val category: FilterAndSortCategory) : Intent()
        data class OptionClicked(
            val selectedCategory: FilterAndSortCategory,
            val option: FilterAndSortOption,
        ) : Intent()

        //data intents
        data class UpdateCategories(val category: FilterAndSortCategory) : Intent()
        data class SetNumOfFilteredTasks(val numOfTasks: Int) : Intent()
        data class SetClearAllEnability(val enabled: Boolean) : Intent()
    }

    data class ViewState(
        val categories: List<FilterAndSortCategory> = FilterAndSortCategory.toList(),
        val clearAllEnabled: Boolean = false,
        val selectedCategory: FilterAndSortCategory = None,
        val numOfFilteredTasks: Int = 0,
        override val loading: Boolean = false,
        override val error: Throwable? = null,
    ) : MviViewState
}