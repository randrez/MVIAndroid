package com.scgts.sctrace.capture.composable.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AutoScanTextNotification(
    text: Int?,
    modifier: Modifier = Modifier,
) {
    if (text != null) {
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            modifier = modifier
                .background(
                    color = Color.White.copy(alpha = 0.90F),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}