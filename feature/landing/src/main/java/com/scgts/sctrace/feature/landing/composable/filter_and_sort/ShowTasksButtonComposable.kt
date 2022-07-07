package com.scgts.sctrace.feature.landing.composable.filter_and_sort

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.ui.components.SCTraceButton

@Composable
fun ShowTaskButton(
    numOfTasks: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SCTraceButton(
        onClick = { onClick() },
        text = stringResource(R.string.show_tasks, numOfTasks),
        modifier = modifier
    )
}