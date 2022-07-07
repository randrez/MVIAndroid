package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scgts.sctrace.feature.landing.R

@Composable
fun TotalTallySection(
    totalTally: String?,
    modifier: Modifier = Modifier
) {
    TaskDetailSection(
        label = R.string.total_tally,
        body = totalTally ?: "-/-",
        modifier = modifier
    )
}