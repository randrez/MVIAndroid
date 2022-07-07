package com.scgts.sctrace.feature.landing.composable.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.TaskCardUiModel
import com.scgts.sctrace.base.model.TextEntry
import com.scgts.sctrace.ui.components.TaskStatusTextButton
import theme.Blue500
import com.scgts.sctrace.root.components.R
import theme.Red

@Composable
fun TaskCard(
    task: TaskCardUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        shape = RectangleShape,
        border = if (isSelected) BorderStroke(2.dp, Blue500) else null,
        elevation = 4.dp,
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    TaskCardLabelText(task.label)
                    Text(
                        text = task.name,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                WarningIcon(showIcon = task.showWarningIcon)
                TaskStatusTextButton(task.status)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                TaskCardDescription(task.descriptionOne)
                task.descriptionTwo?.let { TaskCardDescription(it, Alignment.End) }
            }
        }
    }
}

@Composable
private fun TaskCardDescription(
    description: TextEntry,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    Column(horizontalAlignment = horizontalAlignment) {
        TaskCardLabelText(stringResource(description.label))
        TaskCardDescriptionText(description.body)
    }
}

@Composable
private fun TaskCardLabelText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle2
    )
}

@Composable
private fun TaskCardDescriptionText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.body1
    )
}

@Composable
private fun WarningIcon(showIcon: Boolean) {
    if (showIcon) {
        Row(modifier = Modifier.padding(end = 10.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_icon_information),
                modifier = Modifier
                    .size(24.dp),
                contentDescription = stringResource(id = R.string.info_icon_description),
                tint = Red
            )
        }
    }
}