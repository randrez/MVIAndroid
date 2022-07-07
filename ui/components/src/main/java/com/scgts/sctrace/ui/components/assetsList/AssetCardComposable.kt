package com.scgts.sctrace.ui.components.assetsList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.root.components.R
import theme.Green200
import theme.Red
import theme.SCGTSTheme

@Composable
fun AssetCard(
    index: String,
    asset: AssetCardUiModel,
    onAssetClicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAssetClicked(asset.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 24.dp)
            ) {
                Text(
                    text = index,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                if (asset.expectedInOrder.not()) {
                    Image(
                        painter = painterResource(R.drawable.ic_warning),
                        contentDescription = stringResource(R.string.warning_icon_description),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(16.dp)
                    )
                }
            }
            Column(Modifier.weight(3f)) {
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = asset.millWorkNum?.let { millWorkNum ->
                        stringResource(R.string.mill_work_no_format, millWorkNum)
                    } ?: run { stringResource(R.string.heat_no_format, asset.heatNumber) },
                    style = MaterialTheme.typography.subtitle2
                )
                Text(
                    text = stringResource(R.string.pipe_no_format, asset.pipeNumber),
                    style = MaterialTheme.typography.subtitle2
                )
            }
            Text(
                text = String.format("%.2f", asset.tally),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
            )
            asset.consumed?.let { consumed ->
                Text(
                    text = stringResource(if (consumed) R.string.consumed else R.string.rejected),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (consumed) Green200 else Red,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(6.dp)
                )
            } ?: run {
                Text(
                    text = asset.numTags.toString(),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                )
            }
        }
        Divider()
    }
}

@Preview
@Composable
private fun AssetCardPreview() {
    SCGTSTheme {
        AssetCard(
            index = "1",
            asset = AssetCardUiModel(
                id = "",
                name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
                heatNumber = "847563",
                pipeNumber = "102",
                numTags = 3,
                tally = 30.3,
            ),
            onAssetClicked = { }
        )
    }
}

@Preview
@Composable
private fun AssetCardConsumePreview() {
    SCGTSTheme {
        AssetCard(
            index = "1",
            asset = AssetCardUiModel(
                id = "",
                name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
                heatNumber = "847563",
                pipeNumber = "102",
                numTags = 3,
                tally = 30.3,
                consumed = true
            ),
            onAssetClicked = { }
        )
    }
}

@Preview
@Composable
private fun AssetCardRejectPreview() {
    SCGTSTheme {
        AssetCard(
            index = "1",
            asset = AssetCardUiModel(
                id = "",
                name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
                heatNumber = "847563",
                pipeNumber = "102",
                numTags = 3,
                tally = 30.3,
                consumed = false
            ),
            onAssetClicked = { }
        )
    }
}