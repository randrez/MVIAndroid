package com.scgts.sctrace.feature.landing.composable.tablet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.scgts.sctrace.base.model.AdHocAction
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi
import com.scgts.sctrace.feature.landing.tasks.TasksMvi

@Composable
fun TaskAndTaskDetailsScreenComposable(
    tasksViewState: LiveData<TasksMvi.ViewState>,
    taskDetailsViewState: LiveData<TaskDetailsMvi.ViewState>,
    onFilterAndSortClicked: () -> Unit,
    onRefresh: () -> Unit,
    onUnsyncedSubmissionClicked: (String) -> Unit,
    onSettingsClicked: (String) -> Unit,
    onTaskClicked: (String) -> Unit,
    onStartClick: () -> Unit,
    onAdHocActionClicked: (AdHocAction) -> Unit
) {
    val (quickActionDialogExpanded, setQuickActionDialogVisibility) = remember {
        mutableStateOf(false)
    }
    tasksViewState.observeAsState().value?.let { tasksState ->
        Scaffold(
            topBar = {
                TabletTasksToolbar(
                    unfilteredTasksCount = tasksState.unfilteredTasksCount,
                    filterCount = tasksState.filterCount,
                    pendingTaskCount = tasksState.pendingTaskCount,
                    onFilterAndSortClicked = onFilterAndSortClicked,
                    onRefresh = onRefresh,
                    onUnsyncedSubmissionClicked = onUnsyncedSubmissionClicked,
                    onSettingsClicked = onSettingsClicked
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { setQuickActionDialogVisibility(!quickActionDialogExpanded) }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_scgts_fab),
                        contentDescription = stringResource(R.string.ad_hoc_action_fab_description),
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = true
        ) {
            Row {
                Box(Modifier.weight(44.8f)) {
                    TabletTasksList(
                        tasks = tasksState.tasks,
                        unfilteredTasksCount = tasksState.unfilteredTasksCount,
                        isRefreshing = tasksState.isRefreshing,
                        onRefresh = onRefresh,
                        onTaskClicked = onTaskClicked,
                        selectedTask = tasksState.selectedTask
                    )
                }
                Divider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.2F)
                )
                Box(Modifier.weight(55f)) {
                    TabletTaskDetails(
                        viewState = taskDetailsViewState,
                        selectedTask = tasksState.selectedTask,
                        onStartClick = onStartClick
                    )
                }
            }
        }
        if (quickActionDialogExpanded) {
            TabletAdHocActionDialog(
                adHocActions = tasksState.adHocActions,
                setQuickActionDialogVisibility = setQuickActionDialogVisibility,
                onAdHocActionClicked = onAdHocActionClicked
            )
        }
    }
}