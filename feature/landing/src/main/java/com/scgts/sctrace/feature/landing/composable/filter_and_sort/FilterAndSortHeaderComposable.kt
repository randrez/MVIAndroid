package com.scgts.sctrace.feature.landing.composable.filter_and_sort

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.scgts.sctrace.base.model.FilterAndSortCategory
import com.scgts.sctrace.feature.landing.R
import theme.Blue500

@Composable
fun FilterAndSortHeader(
    selectedCategory: FilterAndSortCategory,
    clearEnabled: Boolean,
    isTablet: Boolean = false,
    onClearClicked: (FilterAndSortCategory) -> Unit,
    onBackClicked: (() -> Unit)? = null,
    onCloseClicked: (() -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (onCloseClicked != null) {
            IconButton(
                onClick = { onCloseClicked() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                )
            }
        } else if (onBackClicked != null) {
            IconButton(
                onClick = { onBackClicked() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_left),
                    contentDescription = null,
                )
            }
        }
        Text(
            text = selectedCategory.name,
            fontWeight = FontWeight.SemiBold,
            style = if (isTablet) MaterialTheme.typography.h4 else MaterialTheme.typography.h6,
        )
        TextButton(
            onClick = { onClearClicked(selectedCategory) },
            enabled = clearEnabled,
        ) {
            Text(
                text = stringResource(R.string.clear),
                color = if (clearEnabled) Blue500 else Blue500.copy(alpha = 0.2F),
                style = MaterialTheme.typography.h6,
            )
        }
    }
}