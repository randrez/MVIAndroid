package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.ui.components.SCTraceProgressBar

@Composable
fun ProgressBarSection(
    percentCompletion: Float,
    lastUpdatedAt: String?,
    totalTally: String?,
    expectedTally: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.task_progress_bar_title),
                style = MaterialTheme.typography.subtitle2
            )
            lastUpdatedAt?.let { lastUpdatedAt ->
                Text(
                    text = stringResource(R.string.task_progress_last_updated, lastUpdatedAt),
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }
        Text(
            text = stringResource(
                id = R.string.task_progress_bar_percent_complete_text,
                percentCompletion * 100
            ),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold
        )
        SCTraceProgressBar(
            progress = percentCompletion,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = totalTally ?: "-/-",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = expectedTally ?: "-/-",
                style = MaterialTheme.typography.body2
            )
        }
    }
}