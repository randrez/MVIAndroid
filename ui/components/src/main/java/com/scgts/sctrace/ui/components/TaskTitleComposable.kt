package com.scgts.sctrace.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskTitle(
    taskType: String,
    taskTitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Text(
            text = taskType,
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = taskTitle,
            style = MaterialTheme.typography.h4
        )
    }
}
