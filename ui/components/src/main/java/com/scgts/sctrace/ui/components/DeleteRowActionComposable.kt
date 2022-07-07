package com.scgts.sctrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.root.components.R
import theme.Red300

@Composable
fun DeleteRowAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxHeight()
            .background(Red300)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_scgts_delete),
            contentDescription = stringResource(R.string.delete_icon_description),
            modifier = Modifier.size(27.dp)
        )
    }
}