package com.scgts.sctrace.rack_transfer.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.base.model.Named
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.base.model.TextEntry
import com.scgts.sctrace.rack_transfer.R
import com.scgts.sctrace.rack_transfer.edittransfer.EditRackTransferMvi.ViewState
import com.scgts.sctrace.ui.components.SCTraceDropdown
import com.scgts.sctrace.ui.components.ScannedSummary
import theme.Blue500
import theme.N950
import theme.SCGTSTheme

@Composable
fun RackSelection(
    actionEnabled: Boolean,
    selectedRackLocation: RackLocation,
    assetsList: List<AssetCardUiModel>,
    rackLocations: List<RackLocation>,
    summaryList: List<TextEntry>,
    actionButtonText: String,
    titleText: String,
    onFinishedClick: () -> Unit,
    onCloseClick: () -> Unit,
    onLocationSelected: (rackLocation: RackLocation) -> Unit,
    onScanAssetsClicked: (() -> Unit)? = null,
    onAssetDeleteClicked: ((String) -> Unit)? = null,
) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                Column(
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { onCloseClick() }
                        ) {
                            Text(
                                text = stringResource(R.string.cancel),
                                color = N950,
                                style = MaterialTheme.typography.h6,
                            )
                        }
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.SemiBold,
                        )
                            TextButton(
                                enabled = actionEnabled,
                                onClick = { onFinishedClick() }
                            ) {
                                Text(
                                    text = actionButtonText,
                                    color = if (actionEnabled) Blue500 else Blue500.copy(alpha = 0.2f),
                                    style = MaterialTheme.typography.h6,
                                )
                            }
                    }
                    SCTraceDropdown(
                        label = R.string.transfer_location,
                        placeholder = stringResource(R.string.choose_location),
                        selectedItem = selectedRackLocation,
                        list = rackLocations,
                        fullScreen = true,
                        searchable = true,
                        onItemSelected = onLocationSelected as (Named) -> Unit
                    )
                }
                ScannedSummary(
                    assets = assetsList,
                    summaryList = summaryList,
                    isConsumption = false,
                    onAssetClicked = { },
                    onScanAssetsClicked = onScanAssetsClicked,
                    onDeleteClicked = onAssetDeleteClicked
                )
            }
        }
    }


@Preview
@Composable
private fun RackTransferSelectionPreview() {
    val assets = listOf(
        AssetCardUiModel(
            id = "",
            name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
            heatNumber = "847563",
            pipeNumber = "102",
            numTags = 3,
            tally = 30.3,
        )
    )
    val viewState = MutableLiveData(
        ViewState(
            assetsList = assets,
            summaryList = listOf(TextEntry(R.string.total_tally, "2 JT / 95.1 FT"))
        )
    )
    SCGTSTheme {
        EditRackTransfer(
            viewState = viewState,
            onCloseClick = {},
            onSaveClick = {},
            onLocationSelected = {}
        )
    }
}