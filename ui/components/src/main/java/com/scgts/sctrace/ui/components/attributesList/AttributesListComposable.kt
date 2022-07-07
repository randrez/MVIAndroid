package com.scgts.sctrace.ui.components.attributesList

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.scgts.sctrace.base.model.AssetAttribute
import theme.SCGTSTheme

@Composable
fun AttributesList(
    attributes: List<Pair<AssetAttribute, String?>>,
    onAttributeClicked: (AssetAttribute) -> Unit,
    footer: @Composable (() -> Unit)? = null,
) {
    LazyColumn(Modifier.fillMaxWidth()) {
        items(attributes) { (attribute, selection) ->
            AttributesListRow(
                attribute = attribute,
                selection = selection,
                onAttributeClicked = onAttributeClicked
            )
        }
        if (footer != null) item { footer() }
    }
}

@Preview
@Composable
fun PreviewList() {
    val attributes = arrayListOf(
        Pair(AssetAttribute.HeatNumber, "test"),
        Pair(AssetAttribute.Manufacturer, "test")
    )
    SCGTSTheme {
        AttributesList(attributes = attributes, onAttributeClicked = {})
    }
}