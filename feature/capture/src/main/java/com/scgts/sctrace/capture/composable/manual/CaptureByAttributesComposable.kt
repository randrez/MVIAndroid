package com.scgts.sctrace.capture.composable.manual

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
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
import com.scgts.sctrace.base.model.AssetAttribute
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.capture.R
import com.scgts.sctrace.capture.manual.ManualCaptureMvi
import com.scgts.sctrace.ui.components.SCTraceButton
import com.scgts.sctrace.ui.components.assetsList.AssetsList
import com.scgts.sctrace.ui.components.attributesList.AttributesList
import theme.Green200
import theme.Red
import theme.SCGTSTheme
import util.CaptureMode

@Composable
fun CaptureByAttributes(
    viewState: LiveData<ManualCaptureMvi.ViewState>,
    isTablet: Boolean,
    onFindAssetClicked: () -> Unit,
    onConsumeClicked: () -> Unit,
    onRejectClicked: () -> Unit,
    onAssetClicked: (String) -> Unit,
    onAttributeClicked: (AssetAttribute) -> Unit,
) {
    val state = viewState.observeAsState().value
    if (state != null) {
        val quickRejectMode =
            state.captureMode is CaptureMode.Consumption.Reject && state.captureMode.quickReject

        Surface(Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                AttributesList(
                    attributes = state.attributes,
                    onAttributeClicked = onAttributeClicked,
                ) {
                    if (state.captureMode == CaptureMode.Assets) {
                        SCTraceButton(
                            enabled = state.findButtonEnabled,
                            onClick = { onFindAssetClicked() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            text = stringResource(R.string.find_asset_button_text),
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            if (!quickRejectMode) {
                                SCTraceButton(
                                    enabled = state.findButtonEnabled,
                                    colors = ButtonDefaults.buttonColors(Green200),
                                    onClick = { onConsumeClicked() },
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(R.string.consume)
                                )
                            }
                            SCTraceButton(
                                enabled = state.findButtonEnabled,
                                colors = ButtonDefaults.buttonColors(Red),
                                onClick = { onRejectClicked() },
                                modifier = Modifier.weight(1f),
                                text = stringResource(R.string.reject)
                            )
                        }
                    }
                }
                if (state.assets.isNotEmpty() && !isTablet) {
                    AssetsList(
                        assets = state.assets,
                        showConsumptionStatus = state.captureMode is CaptureMode.Consumption,
                        onAssetClicked = onAssetClicked
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewNoAssets() {
    val attributesPreviews = arrayListOf(Pair(AssetAttribute.Manufacturer, ""),
        Pair(AssetAttribute.MillWorkNumber, ""),
        Pair(AssetAttribute.HeatNumber, ""),
        Pair(AssetAttribute.PipeNumber, ""),
        Pair(AssetAttribute.ExMillDate, ""))
    SCGTSTheme {
        val viewState = MutableLiveData(ManualCaptureMvi.ViewState(attributes = attributesPreviews))
        CaptureByAttributes(
            viewState = viewState,
            isTablet = false,
            onFindAssetClicked = { },
            onConsumeClicked = { },
            onRejectClicked = { },
            onAssetClicked = { },
            onAttributeClicked = { }
        )
    }
}

@Preview
@Composable
fun PreviewAssets() {
    val attributesPreviews = arrayListOf(Pair(AssetAttribute.Manufacturer, ""),
        Pair(AssetAttribute.MillWorkNumber, ""),
        Pair(AssetAttribute.HeatNumber, ""),
        Pair(AssetAttribute.PipeNumber, ""),
        Pair(AssetAttribute.ExMillDate, ""))

    val assets = listOf(
        AssetCardUiModel(
            id = "",
            name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
            heatNumber = "847563",
            pipeNumber = "102",
            numTags = 3,
            tally = 30.3,
        )
    )

    val viewState = MutableLiveData(ManualCaptureMvi.ViewState(attributes = attributesPreviews,
        assets = assets))
    SCGTSTheme {
        CaptureByAttributes(
            viewState = viewState,
            isTablet = false,
            onFindAssetClicked = { },
            onConsumeClicked = { },
            onRejectClicked = { },
            onAssetClicked = { },
            onAttributeClicked = { }
        )
    }
}