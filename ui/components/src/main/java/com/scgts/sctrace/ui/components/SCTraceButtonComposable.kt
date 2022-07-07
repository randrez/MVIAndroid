package com.scgts.sctrace.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import theme.N100
import theme.SCGTSTheme

@Composable
fun SCTraceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    text: String,
    textColor: Color = Color.White,
    textStyle: TextStyle = MaterialTheme.typography.button
) {
    Button(
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = if (enabled) textColor else N100,
            style = textStyle
        )
    }
}

@Preview
@Composable
private fun SCTraceButtonPreview() {
    SCGTSTheme {
        SCTraceButton(
            onClick = { },
            text = "Button"
        )
    }
}