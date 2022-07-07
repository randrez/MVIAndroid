package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scgts.sctrace.feature.landing.R

@Composable
fun TotalConsumedSection(
    totalConsumed: String?,
    modifier: Modifier = Modifier
) {
    TaskDetailSection(
        label = R.string.total_consumed,
        body = totalConsumed ?: "-/-",
        modifier = modifier
    )
}