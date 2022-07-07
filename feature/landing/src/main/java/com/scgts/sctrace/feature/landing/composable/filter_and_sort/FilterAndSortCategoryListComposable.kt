package com.scgts.sctrace.feature.landing.composable.filter_and_sort

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.FilterAndSortCategory
import com.scgts.sctrace.feature.landing.R
import theme.Blue500
import theme.N500

@Composable
fun FilterAndSortCategoryList(
    categories: List<FilterAndSortCategory>,
    selectedCategory: FilterAndSortCategory,
    onCategoryClicked: (FilterAndSortCategory) -> Unit,
) {
    LazyColumn {
        items(categories) { category ->
            FilterAndSortCategoryOption(
                category = category,
                highlighted = category::class == selectedCategory::class,
                selectedOptions = category.selectedOptions(),
                onCategoryClicked = { onCategoryClicked(category) },
            )
        }
    }
}

@Composable
private fun FilterAndSortCategoryOption(
    category: FilterAndSortCategory,
    highlighted: Boolean,
    selectedOptions: List<String>,
    onCategoryClicked: () -> Unit,
) {
    Card(
        shape = RectangleShape,
        elevation = 0.dp,
        modifier = Modifier
            .then(if (highlighted) Modifier.border(width = 2.dp, color = Blue500) else Modifier)
            .clickable { onCategoryClicked() }
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.h6,
            )
            Text(
                text = selectedOptions.joinToString(),
                style = MaterialTheme.typography.h6,
                color = N500,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = null,
                modifier = Modifier.rotate(180F)
            )
        }
    }
    Divider(Modifier.fillMaxWidth())
}