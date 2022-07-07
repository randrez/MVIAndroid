package com.scgts.sctrace.ui.components.assetsList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.ui.components.DeleteRowAction
import com.scgts.sctrace.ui.components.EditRowAction
import com.scgts.sctrace.ui.components.HorizontallyDraggableCard

const val ACTION_ROW_WIDTH = 100f

@Composable
fun AssetsListRow(
    index: Int,
    asset: AssetCardUiModel,
    modifier: Modifier = Modifier,
    onAssetClicked: (String) -> Unit,
    onEditClicked: ((String) -> Unit)? = null,
    onDeleteClicked: ((String) -> Unit)? = null,
    onCardExpanded: (() -> Unit)? = null,
    onCardCollapsed: (() -> Unit)? = null,
    expanded: Boolean = false,
) {
    Box(modifier) {
        if (onDeleteClicked != null) {
            DeleteRowAction(
                onClick = { onDeleteClicked(asset.id) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(ACTION_ROW_WIDTH.dp)
            )
        }
        if (onEditClicked != null) {
            EditRowAction(
                onClick = { onEditClicked(asset.id) },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(ACTION_ROW_WIDTH.dp)
            )
        }
        HorizontallyDraggableCard(
            cardLeftOffset = if (onEditClicked != null) -ACTION_ROW_WIDTH else 0f,
            cardRightOffset = if (onDeleteClicked != null) ACTION_ROW_WIDTH else 0f,
            modifier = Modifier.fillMaxWidth(),
            isExpanded = expanded,
            onCollapse = onCardCollapsed ?: { },
            onExpand = onCardExpanded ?: { },
        ) {
            AssetCard(
                index = index.toString(),
                asset = asset,
                onAssetClicked = onAssetClicked,
            )
        }
    }
}