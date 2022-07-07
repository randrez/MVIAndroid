package com.scgts.sctrace.feature.landing.composable.task_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.base.model.AssetProductInformationCardUiModel
import com.scgts.sctrace.base.model.Facility
import com.scgts.sctrace.base.model.OrderType.*
import com.scgts.sctrace.base.model.TaskStatus.NOT_STARTED
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.ViewState
import com.scgts.sctrace.ui.components.SCTraceButton
import com.scgts.sctrace.ui.components.TaskHeader
import theme.SCGTSTheme

@Composable
fun TaskDetailsScreen(
    viewState: LiveData<ViewState>,
    onStartClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    viewState.observeAsState().value?.let { state ->
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            ) {
                TaskHeader(onBackClick = onBackClick)
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    when (state.orderType) {
                        INBOUND -> {
                            when (state.taskType) {
                                INBOUND_FROM_MILL -> InboundFromMillTaskDetails(state)
                            }
                        }
                        OUTBOUND -> {
                            when (state.taskType) {
                                BUILD_ORDER, DISPATCH ->
                                    BuildOrderAndDispatchTaskDetails(state)
                                INBOUND_TO_WELL -> InboundToWellTaskDetails(state)
                            }
                        }
                        CONSUMPTION -> {
                            when (state.taskType) {
                                CONSUME -> ConsumeTaskDetails(state)
                            }
                        }
                        RETURN_TRANSFER -> {
                            when (state.taskType) {
                                DISPATCH, DISPATCH_TO_YARD, DISPATCH_TO_WELL, INBOUND_TO_WELL, INBOUND_FROM_WELL_SITE ->
                                    ReturnAndTransferTaskDetails(state)
                                RACK_TRANSFER -> RackTransferTaskDetails(state)
                            }
                        }
                    }
                }
                SCTraceButton(
                    onClick = onStartClick,
                    text = stringResource(if (state.taskStatus == NOT_STARTED) R.string.start else R.string.continue_string),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun TaskDetailsScreenPreview() {
    val viewState = MutableLiveData(
        ViewState(
            orderType = OUTBOUND,
            taskType = BUILD_ORDER,
            orderAndTaskType = "Outbound / Build Order",
            taskDescription = "Task Detail Preview",
            fromLocation = Facility("", "Task Detail Yard"),
            toLocation = Facility("", "Task Detail Well"),
            deliveryDate = "Sep 23, 2021",
            percentCompletion = 0.75f,
            lastUpdatedAt = "Sep 22 at 8:24am",
            totalTally = "3 JT / 93.54 FT",
            expectedTally = "4 JT / 124.32 FT",
            specialInstructions = "Test Instructions",
            assetProductDescription = listOf(
                AssetProductInformationCardUiModel(
                    productDescription = "9-5/8” 53.50# P110 R-3 NEW VAM SF MED",
                    contractNumber = "AR-MSCI-101-7778",
                    shipmentNumber = "WYYK10908",
                    conditionName = "Prime",
                    rackLocationName = "561",
                    percentCompletion = 1f,
                    totalTally = "1 JT / 30.0 FT",
                    expectedTally = "1 JT / 30.0 FT"
                ),
                AssetProductInformationCardUiModel(
                    productDescription = "9-5/8” 53.50# P110 R-3 NEW VAM SF MED",
                    contractNumber = "AR-MSCI-101-7778",
                    shipmentNumber = "WYYK10908",
                    conditionName = "Prime",
                    rackLocationName = "562",
                    percentCompletion = 1f,
                    totalTally = "1 JT / 30.0 FT",
                    expectedTally = "1 JT / 30.0 FT"
                ),
                AssetProductInformationCardUiModel(
                    productDescription = "9-5/8” 53.50# P110 R-3 NEW VAM SF MED",
                    contractNumber = "AR-MSCI-101-7778",
                    shipmentNumber = "WYYK10908",
                    conditionName = "Prime",
                    rackLocationName = "563",
                    percentCompletion = 1f,
                    totalTally = "1 JT / 30.0 FT",
                    expectedTally = "1 JT / 30.0 FT"
                ),
                AssetProductInformationCardUiModel(
                    productDescription = "9-5/8” 53.50# P110 R-3 NEW VAM SF MED",
                    contractNumber = "AR-MSCI-101-7778",
                    shipmentNumber = "WYYK10908",
                    conditionName = "Prime",
                    rackLocationName = "564",
                    percentCompletion = 0f,
                    totalTally = "0 JT / 0.0 FT",
                    expectedTally = "1 JT / 30.0 FT"
                )
            )
        )
    )
    SCGTSTheme {
        TaskDetailsScreen(viewState = viewState, onStartClick = { }, onBackClick = { })
    }
}

