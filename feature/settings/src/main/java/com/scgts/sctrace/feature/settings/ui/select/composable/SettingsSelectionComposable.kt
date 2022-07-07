package com.scgts.sctrace.feature.settings.ui.select.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.capture.iconResource
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionMvi
import com.scgts.sctrace.settings.R
import com.scgts.sctrace.root.components.R.*
import com.scgts.sctrace.ui.components.HeaderSettings
import theme.*

@Composable
fun SettingsSelection(
    viewState: LiveData<SettingsSelectionMvi.ViewState>,
    onSelectCaptureMethod: (CaptureMethod) -> Unit,
    onBackClick: () -> Unit
) {
    viewState.observeAsState().value?.let { state ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            HeaderSettings(
                headerTitle = stringResource(id = R.string.account),
                onBackClick = onBackClick,
                headerSubtitle = state.subtitle
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Gray)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_icon_information),
                            modifier = Modifier
                                .size(30.dp),
                            contentDescription = stringResource(id = string.back_button_icon_description)
                        )
                        Text(
                            text = stringResource(id = R.string.capture_setting_info),
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(start = 9.dp)
                        )
                    }
                }
            }
            CaptureMethodList(
                captureMethods = state.captureActions,
                captureMethodSelected = state.captureMethod,
                onSelectCaptureMethod = onSelectCaptureMethod
            )
        }
    }
}

@Composable
fun CaptureMethodList(
    captureMethods: List<CaptureMethod>,
    captureMethodSelected: CaptureMethod,
    onSelectCaptureMethod: (CaptureMethod) -> Unit
) {
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        items(captureMethods) { captureMethod ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onSelectCaptureMethod(captureMethod) })
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 13.dp, bottom = 13.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        Icon(
                            painter = painterResource(id = captureMethod.iconResource()),
                            modifier = Modifier
                                .size(24.dp),
                            contentDescription = captureMethod.name
                        )
                        Text(
                            text = captureMethod.name,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    if (captureMethod.name == captureMethodSelected.name) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_icon_check),
                            modifier = Modifier
                                .size(25.dp),
                            contentDescription = captureMethod.name,
                            tint = GreenCheck
                        )
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewSettingsSelection() {
    val viewState = MutableLiveData(
        SettingsSelectionMvi.ViewState(subtitle = "Unit of measure")
    )
    SCGTSTheme {
        SettingsSelection(viewState = viewState, onSelectCaptureMethod = {}, onBackClick = {})
    }
}