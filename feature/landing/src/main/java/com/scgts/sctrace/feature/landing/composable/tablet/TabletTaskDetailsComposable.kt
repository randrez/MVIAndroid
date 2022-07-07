package com.scgts.sctrace.feature.landing.composable.tablet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.scgts.sctrace.base.model.OrderType.*
import com.scgts.sctrace.base.model.Task
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.composable.task_details.*
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi

@Composable
fun TabletTaskDetails(
    viewState: LiveData<TaskDetailsMvi.ViewState>,
    selectedTask: Task?,
    onStartClick: () -> Unit,
) {
    if (selectedTask == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_empty_state_illustration_instructions),
                    contentDescription = stringResource(R.string.empty_task_detail_background_image_description)
                )
                Text(
                    text = stringResource(R.string.empty_task_detail_background_text),
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    } else {
        viewState.observeAsState().value?.let { state ->
            Box(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    when (state.orderType) {
                        INBOUND -> {
                            when (state.taskType) {
                                INBOUND_FROM_MILL, AD_HOC_INBOUND_FROM_MILL ->
                                    InboundFromMillTaskDetails(state, onStartClick)
                            }
                        }
                        OUTBOUND -> {
                            when (state.taskType) {
                                BUILD_ORDER, DISPATCH ->
                                    BuildOrderAndDispatchTaskDetails(state, onStartClick)
                                INBOUND_TO_WELL -> InboundToWellTaskDetails(state, onStartClick)
                            }
                        }
                        CONSUMPTION -> {
                            when (state.taskType) {
                                CONSUME -> ConsumeTaskDetails(state, onStartClick)
                            }
                        }
                        RETURN_TRANSFER -> {
                            when (state.taskType) {
                                DISPATCH, DISPATCH_TO_YARD, AD_HOC_DISPATCH_TO_YARD, DISPATCH_TO_WELL,
                                AD_HOC_DISPATCH_TO_WELL, INBOUND_TO_WELL, AD_HOC_INBOUND_TO_WELL,
                                INBOUND_FROM_WELL_SITE, AD_HOC_INBOUND_FROM_WELL_SITE ->
                                    ReturnAndTransferTaskDetails(state, onStartClick)
                                RACK_TRANSFER -> RackTransferTaskDetails(state, onStartClick)
                            }
                        }
                    }
                }
            }
        }
    }
}