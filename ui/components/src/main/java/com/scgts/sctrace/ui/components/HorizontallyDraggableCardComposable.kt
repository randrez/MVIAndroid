package com.scgts.sctrace.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun HorizontallyDraggableCard(
    modifier: Modifier = Modifier,
    cardLeftOffset: Float = 0f,
    cardRightOffset: Float = 0f,
    isExpanded: Boolean = false,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    children: @Composable () -> Unit,
) {
    val (position, setPosition) = remember { mutableStateOf(0f) }
    val xOffset: Float by animateFloatAsState(if (!isExpanded) 0f else position)
    if (!isExpanded) setPosition(0f)
    Card(
        shape = RectangleShape,
        modifier = modifier
            .offset(xOffset.dp, 0.dp)
            .draggable(
                state = rememberDraggableState { delta ->
                    setPosition((position + delta).coerceIn(cardLeftOffset, cardRightOffset))
                },
                orientation = Orientation.Horizontal,
                onDragStarted = { onExpand() },
                onDragStopped = {
                    if ((cardLeftOffset / 2) < position && position < (cardRightOffset / 2)) {
                        onCollapse()
                        setPosition(0f)

                    } else if (position > (cardRightOffset / 2) && !isExpanded) {
                        setPosition(cardRightOffset)

                    } else if (position < (cardLeftOffset / 2) && !isExpanded) {
                        setPosition(cardLeftOffset)
                    }
                }
            )
    ) {
        children()
    }
}