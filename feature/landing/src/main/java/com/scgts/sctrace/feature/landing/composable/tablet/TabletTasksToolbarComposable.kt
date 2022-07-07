package com.scgts.sctrace.feature.landing.composable.tablet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.composable.tasks.FilterAndSortButton
import com.scgts.sctrace.feature.landing.composable.tasks.TasksToolbarActions

@Composable
fun TabletTasksToolbar(
    unfilteredTasksCount: Int,
    filterCount: Int,
    pendingTaskCount: Int,
    onFilterAndSortClicked: () -> Unit,
    onRefresh: () -> Unit,
    onUnsyncedSubmissionClicked: (String) -> Unit,
    onSettingsClicked: (String) -> Unit,
) {
    val tasksString = stringResource(R.string.tasks)
    Surface(
        color = Color.White,
        elevation = 4.dp,
        modifier = Modifier.padding(bottom = 2.dp).fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.tasks),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h4,
                )
                FilterAndSortButton(
                    onClick = onFilterAndSortClicked,
                    enabled = unfilteredTasksCount > 0,
                    filterAndSortCount = filterCount,
                )
            }
            TasksToolbarActions(
                pendingTasksCount = pendingTaskCount,
                onRefreshClicked = onRefresh,
                onUnsyncedSubmissionClicked = { onUnsyncedSubmissionClicked(tasksString) },
                onSettingsClicked = { onSettingsClicked(tasksString) },
            )
        }
    }
}
