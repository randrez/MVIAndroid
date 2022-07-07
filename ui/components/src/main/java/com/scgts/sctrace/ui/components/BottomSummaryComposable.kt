package com.scgts.sctrace.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.RackTransferModel
import com.scgts.sctrace.base.model.Task
import com.scgts.sctrace.base.model.TaskType
import com.scgts.sctrace.root.components.R
import theme.Green
import theme.N900

@Composable
fun BottomSummary(
    submitted: Boolean,
    assets: List<RackTransferModel>,
    task: Task?,
    onSubmitClick: () -> Unit,
    onCaptureClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        if (submitted) {
            Button(
                enabled = !assets.isNullOrEmpty(),
                onClick = onSubmitClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = Green),
                modifier = Modifier
                    .weight(1.8f)
                    .padding(end = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_check),
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = stringResource(id = R.string.submitted),
                    color = N900,
                    modifier = Modifier.padding(10.dp)
                )
            }
        } else {
            Button(
                enabled = !assets.isNullOrEmpty(),
                onClick = onSubmitClick,
                modifier = Modifier
                    .weight(1.8f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.submit),
                    color = Color.White,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
        FloatingActionButton(
            onClick = onCaptureClick,
            modifier = Modifier.wrapContentSize()
        ) {
            if (task?.type == TaskType.RACK_TRANSFER) {
                Image(
                    painter = painterResource(id = R.drawable.ic_buttons_floating_action_transfer),
                    modifier = Modifier
                        .wrapContentSize(),
                    contentDescription = "icon button"
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_scgts_fab),
                    modifier = Modifier
                        .wrapContentSize(),
                    contentDescription = "icon button"
                )
            }
        }
    }
}
