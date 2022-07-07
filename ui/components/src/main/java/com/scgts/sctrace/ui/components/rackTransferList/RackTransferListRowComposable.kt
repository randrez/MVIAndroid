package com.scgts.sctrace.task_summary.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.RackTransferModel
import com.scgts.sctrace.ui.components.EditRowAction
import com.scgts.sctrace.ui.components.HorizontallyDraggableCard
import com.scgts.sctrace.ui.components.assetsList.ACTION_ROW_WIDTH
import com.scgts.sctrace.ui.components.rackTransferList.RackTransferCard

@Composable
fun RackTransferListRow(
    rackTransfer: RackTransferModel,
    modifier: Modifier = Modifier,
    onRackTransferClicked: (RackTransferModel) -> Unit,
    onEditClicked: ((RackTransferModel) -> Unit),
    onCardExpanded: (() -> Unit)? = null,
    onCardCollapsed: (() -> Unit)? = null,
    expanded: Boolean = false,
) {
    Box(modifier) {
        EditRowAction(
            onClick = { onEditClicked(rackTransfer) },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(ACTION_ROW_WIDTH.dp)
        )
        HorizontallyDraggableCard(
            cardLeftOffset = -ACTION_ROW_WIDTH,
            modifier = Modifier.fillMaxHeight(),
            isExpanded = expanded,
            onCollapse = onCardCollapsed ?: { },
            onExpand = onCardExpanded ?: { },
        ) {
            RackTransferCard(
                rackTransferModel = rackTransfer,
                onRackTransferClicked = { onRackTransferClicked(it) })
        }
    }
}