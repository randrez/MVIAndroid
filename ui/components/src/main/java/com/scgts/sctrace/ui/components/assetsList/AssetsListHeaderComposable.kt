package com.scgts.sctrace.ui.components.assetsList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.root.components.R
import theme.Gray
import theme.SCGTSTheme

@Composable
fun AssetsListHeader(showConsumptionStatus: Boolean) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Gray)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            HeaderText(
                text = stringResource(R.string.pound_sign),
                modifier = Modifier.padding(end = 24.dp)
            )
            HeaderText(
                text = stringResource(R.string.product_description),
                modifier = Modifier.weight(3f)
            )
            HeaderText(
                text = stringResource(R.string.tally),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            HeaderText(
                text = stringResource(
                    if (showConsumptionStatus) R.string.consumption_status
                    else R.string.tags
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
        Divider()
    }
}

@Composable
private fun HeaderText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle2,
        textAlign = textAlign,
        modifier = modifier
    )
}

@Preview
@Composable
private fun AssetsListHeaderPreview() {
    SCGTSTheme {
        AssetsListHeader(false)
    }
}

@Preview
@Composable
private fun AssetsListHeaderConsumptionPreview() {
    SCGTSTheme {
        AssetsListHeader(true)
    }
}