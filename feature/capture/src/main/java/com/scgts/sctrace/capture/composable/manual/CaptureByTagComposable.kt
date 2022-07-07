package com.scgts.sctrace.capture.composable.manual

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.capture.R
import com.scgts.sctrace.capture.manual.ManualCaptureMvi
import com.scgts.sctrace.ui.components.SCTraceButton
import com.scgts.sctrace.ui.components.SCTraceTextField
import com.scgts.sctrace.ui.components.assetsList.AssetsList
import theme.Green200
import theme.Red
import theme.SCGTSTheme
import util.CaptureMode.Assets
import util.CaptureMode.Consumption

@Composable
fun CaptureByTagScreen(
    viewState: LiveData<ManualCaptureMvi.ViewState>,
    isTablet: Boolean,
    onFindAssetClicked: (String) -> Unit,
    onConsumeClicked: (String) -> Unit,
    onRejectClicked: (String) -> Unit,
    onAssetClicked: (String) -> Unit,
) {
    val state = viewState.observeAsState().value
    val (tag, setTag) = remember { mutableStateOf("") }
    if (state != null) {
        val quickRejectMode =
            state.captureMode is Consumption.Reject && state.captureMode.quickReject
        Surface(Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                SCTraceTextField(
                    value = tag,
                    onValueChange = setTag,
                    placeholder = { Text(stringResource(R.string.enter_tag_id)) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.captureMode == Assets) {
                    SCTraceButton(
                        enabled = tag.isNotEmpty(),
                        onClick = {
                            setTag("")
                            onFindAssetClicked(tag)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(R.string.find_asset_button_text)
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (!quickRejectMode)
                            SCTraceButton(
                                enabled = tag.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(Green200),
                                onClick = {
                                    setTag("")
                                    onConsumeClicked(tag)
                                },
                                modifier = Modifier.weight(1f),
                                text = stringResource(R.string.consume)
                            )
                        SCTraceButton(
                            enabled = tag.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(Red),
                            onClick = {
                                setTag("")
                                onRejectClicked(tag)
                            },
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.reject)
                        )
                    }
                }
                if (state.assets.isNotEmpty() && !isTablet) {
                    AssetsList(
                        assets = state.assets,
                        showConsumptionStatus = state.captureMode is Consumption,
                        onAssetClicked = onAssetClicked
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CaptureByTagScreenEmptyListPreview() {
    SCGTSTheme {
        val viewState = MutableLiveData(ManualCaptureMvi.ViewState())
        CaptureByTagScreen(
            viewState = viewState,
            isTablet = false,
            onFindAssetClicked = { },
            onConsumeClicked = { },
            onRejectClicked = { },
            onAssetClicked = { }
        )
    }
}

@Preview
@Composable
private fun CaptureByTagScreenPreview() {
    SCGTSTheme {
        val viewState = MutableLiveData(
            ManualCaptureMvi.ViewState(
                assets = listOf(
                    AssetCardUiModel(
                        id = "",
                        name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
                        heatNumber = "847563",
                        pipeNumber = "102",
                        numTags = 3,
                        tally = 30.3,
                    )
                )
            )
        )
        CaptureByTagScreen(
            viewState = viewState,
            isTablet = false,
            onFindAssetClicked = { },
            onConsumeClicked = { },
            onRejectClicked = { },
            onAssetClicked = { }
        )
    }
}

@Preview
@Composable
private fun CaptureByTagScreenConsumptionPreview() {
    SCGTSTheme {
        val viewState = MutableLiveData(
            ManualCaptureMvi.ViewState(
                assets = listOf(
                    AssetCardUiModel(
                        id = "",
                        name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
                        heatNumber = "847563",
                        pipeNumber = "102",
                        numTags = 3,
                        tally = 30.3,
                        consumed = true
                    ),
                    AssetCardUiModel(
                        id = "",
                        name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
                        heatNumber = "847563",
                        pipeNumber = "103",
                        numTags = 3,
                        tally = 30.3,
                        consumed = false
                    )
                )
            )
        )
        CaptureByTagScreen(
            viewState = viewState,
            isTablet = false,
            onFindAssetClicked = { },
            onConsumeClicked = { },
            onRejectClicked = { },
            onAssetClicked = { }
        )
    }
}