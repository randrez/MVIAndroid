package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.TaskStatus
import com.scgts.sctrace.ui.components.TaskStatusTextButton

@Composable
fun TaskDetailSection(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    @StringRes label: Int,
    body: Any?,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        icon?.let { icon -> icon() }
        Column {
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.subtitle2
            )
            when (body) {
                is TaskStatus -> TaskStatusTextButton(status = body)
                is String -> Text(
                    text = body,
                    style = MaterialTheme.typography.body1
                )
                else -> Text(
                    text = "",
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}