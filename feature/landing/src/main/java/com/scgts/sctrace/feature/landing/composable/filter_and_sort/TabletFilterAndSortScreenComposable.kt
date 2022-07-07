package com.scgts.sctrace.feature.landing.composable.filter_and_sort

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.scgts.sctrace.base.model.FilterAndSortCategory
import com.scgts.sctrace.base.model.FilterAndSortCategory.None
import com.scgts.sctrace.base.model.FilterAndSortOption
import com.scgts.sctrace.feature.landing.filter_and_sort.FilterAndSortMvi.ViewState

@Composable
fun TabletFilterAndSortScreen(
    viewState: LiveData<ViewState>,
    onCloseClicked: () -> Unit,
    onClearClicked: (FilterAndSortCategory) -> Unit,
    onCategoryClicked: (FilterAndSortCategory) -> Unit,
    onOptionClicked: (FilterAndSortCategory, FilterAndSortOption) -> Unit,
    onShowTasksClicked: () -> Unit,
) {
    viewState.observeAsState().value?.let { state ->
        Surface {
            Row(Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(40.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(44.8F)
                        .padding(20.dp)
                ) {
                    FilterAndSortHeader(
                        selectedCategory = None,
                        clearEnabled = state.clearAllEnabled,
                        isTablet = true,
                        onCloseClicked = onCloseClicked,
                        onClearClicked = onClearClicked,
                    )
                    FilterAndSortCategoryList(
                        categories = state.categories,
                        selectedCategory = state.selectedCategory,
                        onCategoryClicked = onCategoryClicked,
                    )
                }
                Divider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.2F)
                )
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(55F)
                        .padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(40.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        FilterAndSortHeader(
                            selectedCategory = state.selectedCategory,
                            isTablet = true,
                            clearEnabled = state.selectedCategory.clearEnabled(),
                            onClearClicked = onClearClicked,
                        )
                        FilterAndSortOptionList(
                            selectedCategory = state.selectedCategory,
                            onOptionClicked = onOptionClicked,
                        )
                    }
                    ShowTaskButton(
                        numOfTasks = state.numOfFilteredTasks,
                        onClick = { onShowTasksClicked() },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}