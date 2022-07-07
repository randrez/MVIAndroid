package com.scgts.sctrace.rack_transfer.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.scgts.sctrace.base.model.RackTransferModel
import com.scgts.sctrace.base.model.TaskType
import com.scgts.sctrace.rack_transfer.tasksummary.RackTransferTaskSummaryMvi
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.ui.components.ExpandableTaskSummary
import com.scgts.sctrace.ui.components.rackTransferList.RackTransferList
import theme.Green
import theme.N900
import theme.SCGTSTypography

@Composable
fun RackTransferTabletTaskSummary(
    viewState: LiveData<RackTransferTaskSummaryMvi.ViewState>,
    onBackClick: () -> Unit,
    onSeeDetails: () -> Unit,
    onRackTransferClicked: (RackTransferModel) -> Unit,
    onEditClicked: ((RackTransferModel) -> Unit),
    onSubmitClick: () -> Unit,
    onCaptureClick: () -> Unit
) {
    val state = viewState.observeAsState().value
    if (state != null) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 20.dp)
                    .clickable(true, onClick = onBackClick)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_chevron_left),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
            }
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(1f)) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(start = 18.dp, end = 15.dp)
                    ) {
                        Text(
                            text = state.task?.orderType?.displayName?.plus(" / ".plus(state.task.type.displayName))
                                ?: "",
                            style = SCGTSTypography.subtitle1,
                            modifier = Modifier.padding(top = 7.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Text(
                                    text = state.task?.description ?: "",
                                    style = SCGTSTypography.h4
                                )
                                Spacer(modifier = Modifier.padding(16.dp))
                            }
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.weight(0.8f)
                            ) {
                                if (state.submitted) {
                                    Button(
                                        enabled = !state.rackTransfers.isNullOrEmpty(),
                                        onClick = onSubmitClick,
                                        colors = ButtonDefaults.buttonColors(backgroundColor = Green)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_icon_check),
                                            contentDescription = null,
                                            modifier = Modifier.size(ButtonDefaults.IconSize)
                                        )
                                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                        Text(
                                            text = stringResource(id = R.string.submitted),
                                            color = N900,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        enabled = !state.rackTransfers.isNullOrEmpty(),
                                        onClick = onSubmitClick
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.submit),
                                            color = Color.White,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                        if (state.task != null) {
                            ExpandableTaskSummary(
                                isTablet = true,
                                task = state.task,
                                summaryListExpandable = state.summaryListExpandable,
                                onSeeDetails = onSeeDetails,
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ) {
                        FloatingActionButton(
                            onClick = onCaptureClick,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            if (state.task?.type == TaskType.RACK_TRANSFER) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_buttons_floating_action_transfer),
                                    modifier = Modifier
                                        .wrapContentSize(),
                                    contentDescription = "icon button"
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_scgts_fab),
                                    modifier = Modifier
                                        .wrapContentSize(),
                                    contentDescription = "icon button"
                                )
                            }

                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    RackTransferList(
                        isTablet = true,
                        rackTransfers = state.rackTransfers,
                        task = state.task,
                        onRackTransferClicked = onRackTransferClicked,
                        onEditClicked = onEditClicked,
                    )
                }
            }
        }
    }
}