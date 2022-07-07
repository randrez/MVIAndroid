package com.scgts.sctrace.capture.composable.camera

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CaptureButton(
    show: Boolean,
    isScanning: Boolean,
    onCaptureButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val size: Dp by animateDpAsState(if (isScanning) 26.dp else 66.dp)
    if (show) {
        Button(
            onClick = { onCaptureButtonClicked() },
            elevation = ButtonDefaults.elevation(0.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            contentPadding = PaddingValues(3.dp),
            border = BorderStroke(3.dp, Color.White),
            modifier = modifier.size(78.dp)
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.Center)
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .align(Alignment.Center)
                )
            }
        }
    }
}