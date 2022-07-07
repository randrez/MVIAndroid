package com.scgts.sctrace.capture.composable.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun TabletCaptureCameraScreen(
    viewState: LiveData<CaptureCameraMvi.ViewState>,
    onCaptureButtonClicked: () -> Unit,
    onAutoScanToggle: () -> Unit,
) {
    val state = viewState.observeAsState().value
    if (state != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            AutoScanTextNotification(
                text = state.autoScanNotificationMessage,
                modifier = Modifier.align(Alignment.Center)
            )
            DetectingText(
                show = state.scanState == ScanState.SCANNING,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            CaptureButton(
                show = !state.autoScanEnabled,
                isScanning = state.scanState == ScanState.SCANNING,
                onCaptureButtonClicked = onCaptureButtonClicked,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            AutoScanIconButton(
                autoScanEnabled = state.autoScanEnabled,
                iconSize = 34.dp,
                onToggle = onAutoScanToggle,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Preview
@Composable
private fun TabletCaptureCameraScreenPreview() {
    val viewState = MutableLiveData(CaptureCameraMvi.ViewState())
    SCGTSTheme {
        Surface(color = Color.LightGray) {
            TabletCaptureCameraScreen(
                viewState = viewState,
                onCaptureButtonClicked = { },
                onAutoScanToggle = { }
            )
        }
    }
}