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
import com.scgts.sctrace.capture.R

@Composable
fun DetectingText(
    show: Boolean,
    modifier: Modifier = Modifier,
) {
    if (show) {
        Text(
            text = stringResource(R.string.detecting),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.body2,
            modifier = modifier
                .background(
                    color = Color.White,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}