package com.scgts.sctrace.capture.composable.camera

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.scgts.sctrace.capture.scan.CaptureCameraMvi.ViewState
import com.scgts.sctrace.ui.components.ScannedSummary
import theme.N100
import util.CaptureMode

@Composable
fun CaptureContentScreen(
    viewState: LiveData<ViewState>,
    isTablet: Boolean,
    onAddTagSaveClicked: (String) -> Unit,
    onAddTagDeleteClicked: (String) -> Unit,
    onAddTagDoneClicked: () -> Unit,
    onAssetClicked: (String) -> Unit,
    onEditClicked: ((String) -> Unit)? = null,
    onDeleteClicked: ((String) -> Unit)? = null,
) {
    val state = viewState.observeAsState().value
    if (state != null) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxWidth()) {
                if (!isTablet) {
                    Divider(
                        color = N100,
                        thickness = 4.dp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                            .width(112.dp)
                    )
                }
                if (state.captureMode is CaptureMode.Tags) {
                    AddTag(
                        tags = state.tags,
                        onSaveClicked = onAddTagSaveClicked,
                        onDeleteClicked = onAddTagDeleteClicked,
                        onDoneClicked = onAddTagDoneClicked,
                    )
                } else {
                    ScannedSummary(
                        assets = state.assets,
                        summaryList = state.summaryList,
                        isConsumption = state.captureMode is CaptureMode.Consumption,
                        onAssetClicked = onAssetClicked,
                        onEditClicked = if (state.swipeToEditEnabled) onEditClicked else null,
                        onDeleteClicked = onDeleteClicked,
                    )
                }
            }
        }
    }
}