package com.scgts.sctrace.feature.landing.composable.tablet

import androidx.compose.runtime.Composable
import com.scgts.sctrace.base.model.Task
import com.scgts.sctrace.base.model.TaskCardUiModel
import com.scgts.sctrace.feature.landing.composable.tasks.TasksList

@Composable
fun TabletTasksList(
    tasks: List<TaskCardUiModel>,
    unfilteredTasksCount: Int,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onTaskClicked: (String) -> Unit,
    selectedTask: Task?
) {
    TasksList(
        tasks = tasks,
        unfilteredTasksCount = unfilteredTasksCount,
        isRefreshing = isRefreshing,
        onSwipeRefresh = onRefresh,
        onTaskClicked = onTaskClicked,
        selectedTask = selectedTask
    )
}