package com.scgts.sctrace.rack_transfer.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.base.model.TextEntry
import com.scgts.sctrace.rack_transfer.R
import com.scgts.sctrace.rack_transfer.transferselection.RackTransferSelectionMvi.ViewState
import theme.SCGTSTheme

@Composable
fun RackTransferSelection(
    viewState: LiveData<ViewState>,
    onScanClick: () -> Unit,
    onCreateClick: () -> Unit,
    onCloseClick: () -> Unit,
    onLocationSelected: (rackLocation: RackLocation) -> Unit,
    onAssetDeleteClicked: ((String) -> Unit)? = null,
) {
    viewState.observeAsState().value?.let { state ->
        RackSelection(
            actionEnabled = state.createEnabled,
            selectedRackLocation = state.selectedRackLocation,
            assetsList = state.assetsList,
            rackLocations = state.rackLocations,
            summaryList = state.summaryList,
            actionButtonText = stringResource(id = R.string.create),
            titleText = stringResource(R.string.new_transfer),
            onFinishedClick = onCreateClick,
            onCloseClick = onCloseClick,
            onLocationSelected = onLocationSelected,
            onScanAssetsClicked = onScanClick,
            onAssetDeleteClicked = onAssetDeleteClicked
        )
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
        RackTransferSelection(
            viewState = viewState,
            onCloseClick = {},
            onScanClick = {},
            onCreateClick = {},
            onLocationSelected = {}
        )
    }
}