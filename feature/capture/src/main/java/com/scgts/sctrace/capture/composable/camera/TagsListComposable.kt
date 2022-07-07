package com.scgts.sctrace.capture.composable.camera

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.assets.confirmation.R
import com.scgts.sctrace.ui.components.DeleteRowAction
import com.scgts.sctrace.ui.components.HorizontallyDraggableCard

const val DELETE_ACTION_WIDTH = 100f

@Composable
fun TagsList(
    tags: List<String>,
    onDeleteClicked: ((String) -> Unit)? = null,
) {
    val (listSize, setListSize) = remember { mutableStateOf(tags.size) }
    val (expandedCardIndex, setExpandedCardIndex) = remember { mutableStateOf(-1) }
    if (listSize != tags.size) {
        setExpandedCardIndex(-1)
        setListSize(tags.size)
    }

    Column(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.associated_tags_num, tags.size),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 12.dp)
        )
        Divider()
        LazyColumn(Modifier.fillMaxHeight()) {
            itemsIndexed(tags) { index, tag ->
                Box(Modifier.height(IntrinsicSize.Max)) {
                    if (onDeleteClicked != null) {
                        DeleteRowAction(
                            onClick = { onDeleteClicked(tag) },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(DELETE_ACTION_WIDTH.dp)
                        )
                    }
                    HorizontallyDraggableCard(
                        cardRightOffset = if (onDeleteClicked != null) DELETE_ACTION_WIDTH else 0f,
                        modifier = Modifier.fillMaxWidth(),
                        isExpanded = index == expandedCardIndex,
                        onExpand = { setExpandedCardIndex(index) },
                        onCollapse = { if (index == expandedCardIndex) setExpandedCardIndex(-1) },
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        Divider()
                    }
                }
            }
        }
    }
}