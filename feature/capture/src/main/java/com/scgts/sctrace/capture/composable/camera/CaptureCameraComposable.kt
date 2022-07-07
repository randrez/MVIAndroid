package com.scgts.sctrace.capture.composable.camera

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.capture.scan.*
import theme.SCGTSTheme
import util.ScanState

@Composable
fun CaptureCameraScreen(
    viewState: LiveData<CaptureCameraMvi.ViewState>,
    onCaptureButtonClicked: () -> Unit,
    onAutoScanToggle: () -> Unit,
) {
    val state = viewState.observeAsState().value
    if (state != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AutoScanTextNotification(
                text = state.autoScanNotificationMessage,
                modifier = Modifier.align(Alignment.Center)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                DetectingText(show = state.scanState == ScanState.SCANNING)
                CaptureButton(
                    show = !state.autoScanEnabled,
                    isScanning = state.scanState == ScanState.SCANNING,
                    onCaptureButtonClicked = onCaptureButtonClicked,
                )
            }
            AutoScanIconButton(
                autoScanEnabled = state.autoScanEnabled,
                iconSize = 24.dp,
                onToggle = onAutoScanToggle,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

@Preview
@Composable
private fun CaptureCameraScreenPreview() {
    val viewState = MutableLiveData(CaptureCameraMvi.ViewState())
    SCGTSTheme {
        Surface(color = Color.LightGray) {
            CaptureCameraScreen(
                viewState = viewState,
                onCaptureButtonClicked = { },
                onAutoScanToggle = { }
            )
        }
    }
}