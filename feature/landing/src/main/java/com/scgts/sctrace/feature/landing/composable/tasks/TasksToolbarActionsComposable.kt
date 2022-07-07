package com.scgts.sctrace.feature.landing.composable.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scgts.sctrace.feature.landing.R
import theme.SCGTSTheme
import theme.Yellow

@Composable
fun TasksToolbarActions(
    pendingTasksCount: Int,
    onRefreshClicked: () -> Unit,
    onUnsyncedSubmissionClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Row {
        TasksActionIconButton(
            onClick = onRefreshClicked,
            icon = painterResource(R.drawable.ic_buttons_icon_only_refresh),
        )
        TasksActionIconButton(
            onClick = onUnsyncedSubmissionClicked,
            icon = painterResource(R.drawable.ic_file_syncing),
            pendingTasksCount = pendingTasksCount,
        )
        TasksActionIconButton(
            onClick = onSettingsClicked,
            icon = painterResource(R.drawable.ic_icon_profile),
        )
    }
}

@Composable
private fun TasksActionIconButton(
    onClick: () -> Unit,
    icon: Painter,
    pendingTasksCount: Int? = null,
) {
    IconButton(
        onClick = { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (pendingTasksCount != null && pendingTasksCount > 0) {
                Text(
                    text = pendingTasksCount.toString(),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .offset(x = (-4).dp, 4.dp)
                        .align(Alignment.TopEnd)
                        .background(color = Yellow, shape = CircleShape)
                        .size(14.dp)
                )
            }
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center),
            )
        }
    }
}

@Preview
@Composable
private fun TasksToolbarActionsPreview() {
    SCGTSTheme {
        TasksToolbarActions(
            pendingTasksCount = 2,
            onRefreshClicked = { },
            onUnsyncedSubmissionClicked = { },
            onSettingsClicked = { }
        )
    }
}