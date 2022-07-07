package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scgts.sctrace.feature.landing.R

@Composable
fun ExpectedTallySection(
    expectedTally: String?,
    modifier: Modifier = Modifier
) {
    TaskDetailSection(
        label = R.string.expected_tally,
        body = expectedTally ?: "-/-",
        modifier = modifier
    )
}