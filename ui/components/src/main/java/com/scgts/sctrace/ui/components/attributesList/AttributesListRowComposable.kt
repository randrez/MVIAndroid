package com.scgts.sctrace.ui.components.attributesList

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.AssetAttribute
import com.scgts.sctrace.root.components.R
import theme.N900
import theme.SCGTSTheme


@Composable
fun AttributesListRow(
    attribute: AssetAttribute,
    selection: String?,
    onAttributeClicked: (AssetAttribute) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAttributeClicked(attribute) }
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = attribute.uiName,
                style = MaterialTheme.typography.body2,
                color = N900.copy(alpha = 0.6f)
            )
            Text(
                text = selection ?: "",
                style = MaterialTheme.typography.body1
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = null,
            modifier = Modifier.align(CenterVertically)
        )
    }
}

@Preview
@Composable
fun PreviewRow() {
    SCGTSTheme {
        AttributesListRow(attribute = AssetAttribute.PipeNumber,
            selection = "",
            onAttributeClicked = {})
    }
}