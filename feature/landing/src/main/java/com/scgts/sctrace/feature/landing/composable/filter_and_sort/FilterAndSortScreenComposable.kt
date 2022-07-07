package com.scgts.sctrace.feature.landing.composable.filter_and_sort

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.scgts.sctrace.base.model.FilterAndSortCategory
import com.scgts.sctrace.base.model.FilterAndSortCategory.None
import com.scgts.sctrace.base.model.FilterAndSortOption
import com.scgts.sctrace.feature.landing.filter_and_sort.FilterAndSortMvi.ViewState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FilterAndSortScreen(
    viewState: LiveData<ViewState>,
    onBackClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onClearClicked: (FilterAndSortCategory) -> Unit,
    onCategoryClicked: (FilterAndSortCategory) -> Unit,
    onOptionClicked: (FilterAndSortCategory, FilterAndSortOption) -> Unit,
    onShowTasksClicked: () -> Unit,
) {
    viewState.observeAsState().value?.let { state ->
        AnimatedVisibility(
            visible = state.selectedCategory is None,
            enter = slideInHorizontally({ -it }),
            exit = slideOutHorizontally({ -it }),
        ) {
            Surface {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        FilterAndSortHeader(
                            selectedCategory = None,
                            clearEnabled = state.clearAllEnabled,
                            onCloseClicked = onCloseClicked,
                            onClearClicked = onClearClicked,
                        )
                        FilterAndSortCategoryList(
                            categories = state.categories,
                            selectedCategory = state.selectedCategory,
                            onCategoryClicked = onCategoryClicked,
                        )
                    }
                    ShowTaskButton(
                        numOfTasks = state.numOfFilteredTasks,
                        onClick = { onShowTasksClicked() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = state.selectedCategory !is None,
            enter = slideInHorizontally({ it }),
            exit = slideOutHorizontally({ it }),
        ) {
            Surface {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        FilterAndSortHeader(
                            selectedCategory = state.selectedCategory,
                            clearEnabled = state.selectedCategory.clearEnabled(),
                            onClearClicked = onClearClicked,
                            onBackClicked = onBackClicked,
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
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
