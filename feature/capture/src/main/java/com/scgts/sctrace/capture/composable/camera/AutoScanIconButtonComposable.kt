package com.scgts.sctrace.capture.composable.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.scgts.sctrace.capture.R
import theme.N900

@Composable
fun AutoScanIconButton(
    autoScanEnabled: Boolean,
    iconSize: Dp,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        checked = autoScanEnabled,
        onCheckedChange = { onToggle() },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(
                if (autoScanEnabled) R.drawable.ic_icon_auto_scan_on
                else R.drawable.ic_icon_auto_scan_off
            ),
            contentDescription = stringResource(R.string.auto_scan_icon_description),
            tint = Color.White,
            modifier = Modifier
                .background(N900, CircleShape)
                .padding(iconSize.times(0.25f))
                .size(iconSize)
        )
    }
}