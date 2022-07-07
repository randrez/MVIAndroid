package com.scgts.sctrace.feature.landing.composable.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.feature.landing.R
import theme.SCGTSTheme

@Composable
fun TasksToolbar(
    filterCount: Int,
    pendingTasksCount: Int,
    enableFilterAndSort: Boolean,
    onFilterAndSortClicked: () -> Unit,
    onRefreshClicked: () -> Unit,
    onUnsyncedSubmissionClicked: (String) -> Unit,
    onSettingsClicked: (String) -> Unit,
) {
    val tasksString = stringResource(R.string.tasks)
    Surface(
        color = Color.White,
        elevation = 4.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.tasks),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h5,
                )
                TasksToolbarActions(
                    pendingTasksCount = pendingTasksCount,
                    onRefreshClicked = onRefreshClicked,
                    onUnsyncedSubmissionClicked = { onUnsyncedSubmissionClicked(tasksString) },
                    onSettingsClicked = { onSettingsClicked(tasksString) },
                )
            }
            FilterAndSortButton(
                onClick = onFilterAndSortClicked,
                enabled = enableFilterAndSort,
                filterAndSortCount = filterCount,
            )
        }
    }
}

@Preview
@Composable
private fun TasksToolbarPreview() {
    SCGTSTheme {
        TasksToolbar(
            filterCount = 6,
            pendingTasksCount = 2,
            enableFilterAndSort = true,
            onFilterAndSortClicked = { },
            onRefreshClicked = { },
            onUnsyncedSubmissionClicked = { },
            onSettingsClicked = { },
        )
    }
}