package com.scgts.sctrace.ui.components.rackTransferList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.root.components.R
import theme.Gray
import theme.N900

@Composable
fun RackTransferListHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            HeaderText(
                text = stringResource(id = R.string.transfer_location),
                modifier = Modifier.weight(1f)
            )
            HeaderText(
                text = stringResource(id = R.string.tally), modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp)
            )
        }
        Divider(color = N900.copy(0.1f))
    }
}

@Composable
fun HeaderText(text: String, modifier: Modifier) {
    Text(
        text = text,
        textAlign = TextAlign.Start,
        color = N900.copy(alpha = 0.6f),
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}