package com.scgts.sctrace.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.base.model.TextEntry
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.ui.components.assetsList.AssetsList
import theme.Blue500
import theme.N500
import theme.SCGTSTheme

@Composable
fun ScannedSummary(
    assets: List<AssetCardUiModel>,
    summaryList: List<TextEntry>,
    isConsumption: Boolean,
    onAssetClicked: (String) -> Unit,
    onEditClicked: ((String) -> Unit)? = null,
    onDeleteClicked: ((String) -> Unit)? = null,
    onScanAssetsClicked: (() -> Unit)? = null,
) {
    Column(Modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.scanned_summary),
                    color = N500,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6,
                )
                if (onScanAssetsClicked != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(
                            enabled = true,
                            onClick = { onScanAssetsClicked() }
                        ),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_icon_scan_plus),
                            contentDescription = stringResource(R.string.scan_assets_icon_description)
                        )
                        Text(
                            text = stringResource(R.string.scan_assets),
                            color = Blue500,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(summaryList) { textEntry ->
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text(
                            text = stringResource(textEntry.label),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.subtitle1,
                        )
                        Text(
                            text = textEntry.body,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.body1,
                        )
                    }
                }
            }
        }
        AssetsList(
            assets = assets,
            showConsumptionStatus = isConsumption,
            onAssetClicked = onAssetClicked,
            onEditClicked = onEditClicked,
            onDeleteClicked = onDeleteClicked,
        )
    }
}

@Preview
@Composable
private fun ScannedSummaryPreview() {
    SCGTSTheme {
        Surface {
            ScannedSummary(
                assets = listOf(
                    AssetCardUiModel(
                        id = "",
                        name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
                        heatNumber = "847563",
                        pipeNumber = "102",
                        numTags = 3,
                        tally = 30.3,
                    )
                ),
                summaryList = listOf(TextEntry(R.string.total_tally, "2 JT / 95.1 FT")),
                isConsumption = false,
                onAssetClicked = { }
            )
        }
    }
}

@Preview
@Composable
private fun ScannedSummaryConsumptionPreview() {
    SCGTSTheme {
        Surface {
            ScannedSummary(
                assets = listOf(
                    AssetCardUiModel(
                        id = "",
                        name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
                        heatNumber = "847563",
                        pipeNumber = "102",
                        numTags = 3,
                        tally = 30.3,
                        consumed = true
                    ),
                    AssetCardUiModel(
                        id = "",
                        name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
                        heatNumber = "847563",
                        pipeNumber = "103",
                        numTags = 3,
                        tally = 30.3,
                        consumed = false
                    )
                ),
                summaryList = listOf(
                    TextEntry(R.string.session_consumed, "2 JT / 95.1 FT"),
                    TextEntry(R.string.session_running_length, "95.1 FT"),
                    TextEntry(R.string.session_rejected, "2 JT / 95.1 FT")
                ),
                isConsumption = true,
                onAssetClicked = { }
            )
        }
    }
}