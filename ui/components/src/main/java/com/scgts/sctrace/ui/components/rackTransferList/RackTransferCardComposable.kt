package com.scgts.sctrace.ui.components.rackTransferList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.RackTransferModel
import com.scgts.sctrace.root.components.R
import theme.N900
import theme.SCGTSTypography

@Composable
fun RackTransferCard(
    rackTransferModel: RackTransferModel,
    onRackTransferClicked: (RackTransferModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onRackTransferClicked(rackTransferModel) })
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = rackTransferModel.rackLocationName,
                    fontWeight = FontWeight.Medium,
                    style = SCGTSTypography.body1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = rackTransferModel.totalJointsAndLength,
                    style = SCGTSTypography.body1,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = rackTransferModel.productDescription,
                    style = MaterialTheme.typography.body2,
                    color = N900.copy(alpha = 0.6f)
                )
                if (rackTransferModel.millWorkNum.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.mill_work_no) + rackTransferModel.millWorkNum,
                        style = MaterialTheme.typography.body2,
                        color = N900.copy(alpha = 0.6f)
                    )
                }
            }
        }
        Divider()
    }
}