package com.scgts.sctrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scgts.sctrace.base.model.TaskStatus
import com.scgts.sctrace.base.model.TaskStatus.*
import com.scgts.sctrace.root.components.R
import theme.*
import java.util.*

@Composable
fun TaskStatusTextButton(status: TaskStatus, enabled: Boolean = true) {
    val (text, color) = when (status) {
        NOT_STARTED -> Pair(R.string.not_started, N060)
        IN_PROGRESS -> Pair(R.string.in_progress, T100)
        PENDING -> Pair(R.string.pending, Yellow)
        IN_REVIEW -> Pair(R.string.in_review, P200)
        else -> Pair(R.string.completed, Color.Green)
    }
    Text(
        text = stringResource(text).uppercase(Locale.getDefault()),
        color = if (enabled) N900 else N900.copy(alpha = 0.2f),
        fontSize = 10.sp,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.body2,
        modifier = Modifier
            .background(
                color = if (enabled) color else color.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            )
            .width(80.dp)
            .padding(vertical = 4.dp)
    )
}