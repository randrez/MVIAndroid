package com.scgts.sctrace.feature.landing.composable.tasks

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.feature.landing.R
import theme.Gray
import theme.N100
import theme.N500
import theme.SCGTSTheme

@Composable
fun FilterAndSortButton(
    onClick: () -> Unit,
    enabled: Boolean,
    filterAndSortCount: Int,
) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Gray,
            disabledBackgroundColor = Gray,
        ),
        contentPadding = PaddingValues(start = 6.dp, top = 8.dp, end = 12.dp, bottom = 8.dp),
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_buttons_part_default_icon_only_sort_filter),
            contentDescription = null,
            tint = if (enabled) N500 else N100,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = stringResource(R.string.filter_and_sort),
            color = if (enabled) N500 else N100,
            style = MaterialTheme.typography.subtitle1,
        )
        if (enabled && filterAndSortCount > 0) {
            Text(
                text = " ($filterAndSortCount)",
                color = if (enabled) N500 else N100,
                style = MaterialTheme.typography.subtitle1,
            )
        }
    }
}

@Preview
@Composable
private fun FilterAndSortButtonPreview() {
    SCGTSTheme {
        FilterAndSortButton(
            onClick = { },
            enabled = true,
            filterAndSortCount = 6,
        )
    }
}

@Preview
@Composable
private fun DisabledFilterAndSortButtonPreview() {
    SCGTSTheme {
        FilterAndSortButton(
            onClick = { },
            enabled = false,
            filterAndSortCount = 1,
        )
    }
}