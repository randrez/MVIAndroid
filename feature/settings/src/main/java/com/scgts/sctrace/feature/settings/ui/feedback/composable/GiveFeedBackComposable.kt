package com.scgts.sctrace.feature.settings.ui.feedback.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.base.model.FeedbackOption
import com.scgts.sctrace.base.model.Named
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.ViewState
import com.scgts.sctrace.settings.R
import com.scgts.sctrace.ui.components.SCTraceDropdown
import theme.*

@Composable
fun GiveFeedback(
    viewState: LiveData<ViewState>,
    onFeedbackDetail: (String) -> Unit,
    onSubmitFeedback: () -> Unit,
    onSelectFeedbackType: (FeedbackOption) -> Unit,
    onSelectSeverity: (FeedbackOption) -> Unit,
    onCancel: () -> Unit
) {
    viewState.observeAsState().value?.let { state ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 38.dp, horizontal = 16.dp)
        ) {
            HeaderGiveFeedback(
                enableSubmit = state.enableSubmit,
                onCancel = onCancel,
                onSubmitFeedback = onSubmitFeedback
            )
            Text(
                text = stringResource(id = R.string.message_feedback),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 33.5.dp)
            )
            Column(modifier = Modifier.padding(top = 36.5.dp)) {
                SCTraceDropdown(
                    label = R.string.feedback_type,
                    list = state.feedbackTypes,
                    selectedItem = state.feedbackType,
                    onItemSelected = onSelectFeedbackType as (Named) -> Unit
                )
                Text(
                    text = stringResource(id = R.string.required),
                    color = Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            Column(modifier = Modifier.padding(top = 24.dp)) {
                SCTraceDropdown(
                    label = R.string.feedback_severity,
                    list = state.severities,
                    selectedItem = state.severity,
                    onItemSelected = onSelectSeverity as (Named) -> Unit,
                )
            }
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Text(
                    text = stringResource(id = R.string.feedback_details),
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Start
                )
                TextField(
                    value = state.detailsValue,
                    onValueChange = { onFeedbackDetail(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(87.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White
                    )
                )
                Text(
                    text = stringResource(id = R.string.required),
                    color = Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

@Composable
fun HeaderGiveFeedback(
    enableSubmit: Boolean,
    onCancel: () -> Unit,
    onSubmitFeedback: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onCancel) {
            Text(
                text = stringResource(id = R.string.cancel),
                style = MaterialTheme.typography.h6,
            )
        }
        Text(
            text = stringResource(id = R.string.give_feedback),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold
        )
        TextButton(onClick = onSubmitFeedback, enabled = enableSubmit) {
            Text(
                text = stringResource(id = R.string.submit),
                style = MaterialTheme.typography.h6,
                color = if (enableSubmit) N900 else Gray
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    SCGTSTheme {
        val viewState = MutableLiveData(ViewState())
        GiveFeedback(
            viewState = viewState,
            onFeedbackDetail = {},
            onSubmitFeedback = { },
            onSelectFeedbackType = {},
            onSelectSeverity = {},
            onCancel = {}
        )
    }
}