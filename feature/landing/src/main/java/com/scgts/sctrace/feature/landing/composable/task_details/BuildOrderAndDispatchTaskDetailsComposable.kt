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
import com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections.*
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.ViewState
import com.scgts.sctrace.ui.components.SCTraceButton
import com.scgts.sctrace.ui.components.TaskTitle

@Composable
fun BuildOrderAndDispatchTaskDetails(
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
            ExpectedTallySection(
                expectedTally = state.expectedTally,
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            FromLocationSection(
                fromLocationName = state.fromLocation?.name,
                modifier = Modifier.weight(1f)
            )
            ToLocationSection(
                toLocationName = state.toLocation?.name,
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            DeliveryDateSection(
                deliveryDate = state.deliveryDate,
                modifier = Modifier.weight(1f)
            )
        }
        ProgressBarSection(
            percentCompletion = state.percentCompletion,
            lastUpdatedAt = state.lastUpdatedAt,
            totalTally = state.totalTally,
            expectedTally = state.expectedTally
        )
    }
    Column {
        Text(
            text = stringResource(R.string.order_details),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        Divider()
        SpecialInstructionsSection(specialInstructions = state.specialInstructions)
        AssetListSection(assetProductInformation = state.assetProductDescription)
    }
}