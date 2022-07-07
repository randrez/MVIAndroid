package com.scgts.sctrace.feature.landing.composable.task_details

import NotSubmittedTraceEventWarningSection
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.TaskStatus.NOT_STARTED
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections.SpecialInstructionsSection
import com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections.TaskStatusSection
import com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections.TotalConsumedSection
import com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections.WellSection
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.ViewState
import com.scgts.sctrace.ui.components.SCTraceButton
import com.scgts.sctrace.ui.components.TaskTitle


@Composable
fun ConsumeTaskDetails(
    state: ViewState,
    onStartClick: (() -> Unit)? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row {
            TaskTitle(
                taskType = state.orderAndTaskType,
                taskTitle = state.taskDescription,
                modifier = Modifier.weight(1f)
            )
            onStartClick?.let {
                SCTraceButton(
                    onClick = onStartClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    text = stringResource(if (state.taskStatus == NOT_STARTED) R.string.start else R.string.continue_string),
                    textStyle = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
        NotSubmittedTraceEventWarningSection(show = state.showNotSubmittedTraceEventsWarning)
        Row {
            TaskStatusSection(
                taskStatus = state.taskStatus,
                modifier = Modifier.weight(1f)
            )
            TotalConsumedSection(
                totalConsumed = state.totalConsumed,
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            WellSection(
                wellSection = state.wellSection,
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            Column(modifier = Modifier.weight(1f)) {
                state.lastUpdatedAt?.let { lastUpdatedAt ->
                    Text(
                        text = stringResource(R.string.task_progress_last_updated, lastUpdatedAt),
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            }
        }
    }
    state.specialInstructions?.let { specialInstructions ->
        Column {
            Text(
                text = stringResource(R.string.order_details),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            Divider()
            SpecialInstructionsSection(specialInstructions = specialInstructions)
        }
    }
}