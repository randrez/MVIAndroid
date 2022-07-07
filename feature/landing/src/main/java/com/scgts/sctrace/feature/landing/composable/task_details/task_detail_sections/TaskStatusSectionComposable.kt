package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scgts.sctrace.base.model.TaskStatus
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections.TaskDetailSection

@Composable
fun TaskStatusSection(
    taskStatus: TaskStatus,
    modifier: Modifier = Modifier
) {
    TaskDetailSection(
        label = R.string.status,
        body = taskStatus,
        modifier = modifier
    )
}