package com.scgts.sctrace.rack_transfer.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.rack_transfer.tasksummary.RackTransferTaskSummaryMvi
import com.scgts.sctrace.ui.components.BottomSummary
import com.scgts.sctrace.ui.components.ExpandableTaskSummary
import com.scgts.sctrace.ui.components.TaskHeader
import com.scgts.sctrace.ui.components.TaskTitle
import com.scgts.sctrace.ui.components.rackTransferList.RackTransferList
import theme.SCGTSTheme

@Composable
fun RackTransferTaskSummary(
    viewState: LiveData<RackTransferTaskSummaryMvi.ViewState>,
    onBackClick: () -> Unit,
    onSeeDetails: () -> Unit,
    onRackTransferClicked: (RackTransferModel) -> Unit,
    onEditClicked: ((RackTransferModel) -> Unit),
    onSubmitClick: () -> Unit,
    onCaptureClick: () -> Unit
) {
    viewState.observeAsState().value?.let { state ->
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            ) {
                TaskHeader(onBackClick = onBackClick)
                Column(modifier = Modifier.weight(1f)) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        TaskTitle(
                            taskType = state.task?.orderAndTask() ?: "",
                            taskTitle = state.task?.description ?: ""
                        )
                        Spacer(modifier = Modifier.padding(16.dp))
                        if (state.task != null) {
                            ExpandableTaskSummary(
                                task = state.task,
                                summaryListExpandable = state.summaryListExpandable,
                                onSeeDetails = onSeeDetails
                            )
                        }
                    }
                    RackTransferList(
                        rackTransfers = state.rackTransfers,
                        task = state.task,
                        onRackTransferClicked = onRackTransferClicked,
                        onEditClicked = onEditClicked
                    )
                }
                BottomSummary(
                    submitted = state.submitted,
                    assets = state.rackTransfers,
                    task = state.task,
                    onSubmitClick = onSubmitClick,
                    onCaptureClick = onCaptureClick
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun Preview() {
    val viewState = MutableLiveData(
        RackTransferTaskSummaryMvi.ViewState(
            task = Task(
                id = "taskId1",
                type = TaskType.RACK_TRANSFER,
                status = TaskStatus.NOT_STARTED,
                totalExpectedLength = 0.0,
                totalNumJoints = 0,
                projectId = "projectId",
                orderId = "orderId",
                unitOfMeasure = UnitType.FEET,
                orderType = OrderType.INBOUND,
                specialInstructions = null,
                description = "descriptionTest",
                toLocationId = "locationId",
                toLocationName = "locationName",
                fromLocationId = "fromLocationId",
                fromLocationName = "fromLocationName",
                arrivalDate = "11/06/2021",
                deliveryDate = "11/06/2021",
                dispatchDate = null,
                defaultRackLocationId = "defaultRackLocationId",
                wellSection = "wellSection",
                organizationName = "organizationName"
            ),
        )
    )
    SCGTSTheme {
        RackTransferTaskSummary(
            viewState = viewState,
            onBackClick = {},
            onSeeDetails = {},
            onRackTransferClicked = {},
            onEditClicked = {},
            onSubmitClick = {},
            onCaptureClick = {}
        )
    }
}