package com.scgts.sctrace.feature.landing.composable.tasks

import androidx.compose.foundation.Image
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.LiveData
import com.scgts.sctrace.base.model.AdHocAction
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.tasks.TasksMvi.ViewState

@Composable
fun TasksScreen(
    viewState: LiveData<ViewState>,
    onFilterAndSortClicked: () -> Unit,
    onRefresh: () -> Unit,
    onUnsyncedSubmissionClicked: (String) -> Unit,
    onSettingsClicked: (String) -> Unit,
    onTaskClicked: (String) -> Unit,
    onAdHocActionClicked: (AdHocAction) -> Unit
) {
    val (quickActionDialogExpanded, setQuickActionDialogVisibility) = remember {
        mutableStateOf(false)
    }
    viewState.observeAsState().value?.let { state ->
        Scaffold(
            topBar = {
                TasksToolbar(
                    filterCount = state.filterCount,
                    pendingTasksCount = state.pendingTaskCount,
                    enableFilterAndSort = state.unfilteredTasksCount > 0,
                    onFilterAndSortClicked = onFilterAndSortClicked,
                    onRefreshClicked = onRefresh,
                    onUnsyncedSubmissionClicked = onUnsyncedSubmissionClicked,
                    onSettingsClicked = onSettingsClicked,
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { setQuickActionDialogVisibility(!quickActionDialogExpanded) }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_scgts_fab),
                        contentDescription = null,
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = true
        ) {
            TasksList(
                tasks = state.tasks,
                unfilteredTasksCount = state.unfilteredTasksCount,
                isRefreshing = state.isRefreshing,
                onSwipeRefresh = onRefresh,
                onTaskClicked = onTaskClicked,
            )
        }
        if (quickActionDialogExpanded) {
            AdHocActionDialog(
                adHocActions = state.adHocActions,
                setQuickActionDialogVisibility = setQuickActionDialogVisibility,
                onAdHocActionClicked = onAdHocActionClicked
            )
        }
    }
}