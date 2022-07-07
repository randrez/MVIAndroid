package com.scgts.sctrace.ui.components.assetsList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.scgts.sctrace.base.model.AssetCardUiModel
import theme.SCGTSTheme

@Composable
fun AssetsList(
    assets: List<AssetCardUiModel>,
    showConsumptionStatus: Boolean,
    onAssetClicked: (String) -> Unit,
    onEditClicked: ((String) -> Unit)? = null,
    onDeleteClicked: ((String) -> Unit)? = null,
) {
    val (listSize, setListSize) = remember { mutableStateOf(assets.size) }
    val (expandedCardIndex, setExpandedCardIndex) = remember { mutableStateOf(-1) }
    if (listSize != assets.size) {
        setExpandedCardIndex(-1)
        setListSize(assets.size)
    }

    Column(Modifier.fillMaxWidth()) {
        AssetsListHeader(showConsumptionStatus = showConsumptionStatus)
        LazyColumn(Modifier.fillMaxWidth()) {
            itemsIndexed(assets) { index, asset ->
                AssetsListRow(
                    index = assets.size - index,
                    asset = asset,
                    onAssetClicked = onAssetClicked,
                    onEditClicked = onEditClicked,
                    onDeleteClicked = onDeleteClicked,
                    modifier = Modifier.height(IntrinsicSize.Max),
                    expanded = index == expandedCardIndex,
                    onCardCollapsed = { if (index == expandedCardIndex) setExpandedCardIndex(-1) },
                    onCardExpanded = { setExpandedCardIndex(index) },
                )
            }
        }
    }
}

@Preview
@Composable
fun AssetsListPreview() {
    SCGTSTheme {
        AssetsList(
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
            showConsumptionStatus = false,
            onAssetClicked = { }
        )
    }
}

@Preview
@Composable
fun AssetsListConsumptionPreview() {
    SCGTSTheme {
        AssetsList(
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
            showConsumptionStatus = true,
            onAssetClicked = { },
            onDeleteClicked = { },
            onEditClicked = { },
        )
    }
}