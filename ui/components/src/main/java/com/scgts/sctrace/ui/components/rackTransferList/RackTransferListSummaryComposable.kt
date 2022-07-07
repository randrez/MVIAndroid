package com.scgts.sctrace.ui.components.rackTransferList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.task_summary.composable.RackTransferListRow
import com.scgts.sctrace.ui.components.EmptyScreenMessage

@Composable
fun RackTransferList(
    isTablet: Boolean = false,
    task: Task?,
    rackTransfers: List<RackTransferModel>,
    onRackTransferClicked: (RackTransferModel) -> Unit,
    onEditClicked: ((RackTransferModel) -> Unit),
    onDeleteClicked: ((String) -> Unit)? = null,
) {
    if (task != null) {
        val (listSize, setListSize) = remember { mutableStateOf(rackTransfers.size) }
        val (expandedCardIndex, setExpandedCardIndex) = remember { mutableStateOf(-1) }
        if (listSize != rackTransfers.size) {
            setExpandedCardIndex(-1)
            setListSize(rackTransfers.size)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = if (isTablet) 0.dp else 22.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            if (rackTransfers.isNotEmpty()) {
                LazyColumn(Modifier.fillMaxWidth()) {
                    item {
                        RackTransferListHeader()
                    }
                    itemsIndexed(rackTransfers) { index, transfers ->
                        RackTransferListRow(
                            rackTransfer = transfers,
                            onRackTransferClicked = onRackTransferClicked,
                            onEditClicked = onEditClicked,
                            expanded = index == expandedCardIndex,
                            modifier = Modifier.height(IntrinsicSize.Max),
                            onCardCollapsed = { if (index == expandedCardIndex) setExpandedCardIndex(-1) },
                            onCardExpanded = { setExpandedCardIndex(index) },
                        )
                    }
                }
            } else {
                RackTransferListHeader()
                EmptyScreenMessage(
                    title = R.string.start_transferring,
                    message = R.string.message_new_transfer,
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun Preview() {
    val task = Task(
        id = "taskId1",
        type = TaskType.RACK_TRANSFER,
        status = TaskStatus.IN_REVIEW,
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
    )
    RackTransferList(task = task, rackTransfers = emptyList(), onRackTransferClicked = {}, onEditClicked = {})
}



