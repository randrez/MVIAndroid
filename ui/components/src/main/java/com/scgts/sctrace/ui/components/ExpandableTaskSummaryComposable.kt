package com.scgts.sctrace.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.root.components.R
import theme.Blue500
import theme.Gray
import theme.N900
import theme.SCGTSTypography

@Composable
fun ExpandableTaskSummary(
    isTablet: Boolean = false,
    task: Task,
    summaryListExpandable: List<ExpandableTextEntry>,
    onSeeDetails: () -> Unit,
) {
    var expandState by remember { mutableStateOf(isTablet) }
    val rotateState by animateFloatAsState(targetValue = if (expandState) 180f else 0f)
    if (summaryListExpandable.isNotEmpty()) {
        Card(
            shape = RectangleShape,
            backgroundColor = Gray,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(
                        delayMillis = 50,
                        easing = LinearOutSlowInEasing
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Status(status = task.status, isTablet = isTablet)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    items(summaryListExpandable) { textEntrySummary ->
                        if (stringResource(textEntrySummary.label) == "Order") {
                            if (expandState) {
                                SeeDetails(
                                    title = stringResource(textEntrySummary.label),
                                    value = textEntrySummary.body,
                                    onSeeDetails = onSeeDetails
                                )
                            }
                        } else if (textEntrySummary.expandable) {
                            if (expandState) {
                                TotalsSummary(
                                    title = stringResource(textEntrySummary.label),
                                    value = textEntrySummary.body
                                )
                            }
                        } else {
                            TotalsSummary(
                                title = stringResource(textEntrySummary.label),
                                value = textEntrySummary.body
                            )
                        }
                    }
                }
                if (!isTablet) {
                    Divider(color = N900.copy(0.1f))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(ContentAlpha.medium)
                            .rotate(rotateState)
                            .clickable(onClick = { expandState = !expandState })
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_chevron_down),
                            contentDescription = "Arrow Down",
                            Modifier
                                .padding(5.dp)
                                .size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Status(status: TaskStatus, isTablet: Boolean) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(top = if (isTablet) 5.dp else 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.status),
            textAlign = TextAlign.End,
            color = N900.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(150.dp)
                .align(Alignment.CenterVertically)
        )
        Row(modifier = Modifier
            .weight(1f)
            .padding(start = 20.dp)) {
            TaskStatusTextButton(status = status)
        }
    }
}

@Composable
fun TotalsSummary(title: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            textAlign = TextAlign.End,
            color = N900.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(150.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = value,
            modifier = Modifier
                .padding(start = 20.dp, bottom = 8.dp, end = 8.dp, top = 8.dp)
                .weight(1f),
            style = SCGTSTypography.body1
        )
    }
}


@Composable
fun SeeDetails(
    title: String,
    value: String,
    onSeeDetails: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            textAlign = TextAlign.End,
            color = N900.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(150.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = value,
            modifier = Modifier
                .padding(start = 20.dp, bottom = 8.dp, end = 8.dp, top = 8.dp)
                .weight(1f)
                .clickable(enabled = true, onClick = onSeeDetails),
            color = Blue500,
            fontSize = 14.sp
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun PreviewExpandable() {
    val task = Task(
        id = "taskId1",
        type = TaskType.INBOUND_FROM_MILL,
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
    ExpandableTaskSummary(
        task = task,
        summaryListExpandable = emptyList(),
        onSeeDetails = {})
}
