package com.scgts.sctrace.feature.landing.composable.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.scgts.sctrace.base.model.Task
import com.scgts.sctrace.base.model.TaskCardUiModel
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.ui.components.EmptyScreenMessage

@Composable
fun TasksList(
    tasks: List<TaskCardUiModel>,
    unfilteredTasksCount: Int,
    isRefreshing: Boolean,
    onSwipeRefresh: () -> Unit,
    onTaskClicked: (String) -> Unit,
    selectedTask: Task? = null
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { onSwipeRefresh() }
    ) {
        when {
            unfilteredTasksCount == 0 -> EmptyScreenMessage(
                title = R.string.no_tasks_listed_yet,
                message = R.string.refresh_your_list,
            )
            tasks.isEmpty() -> EmptyScreenMessage(
                title = R.string.no_tasks_found,
                message = R.string.change_your_filter,
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            item {}
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    isSelected = task.id == selectedTask?.id,
                    onClick = { onTaskClicked(task.id) },
                )
            }
            item {}
        }
    }
}