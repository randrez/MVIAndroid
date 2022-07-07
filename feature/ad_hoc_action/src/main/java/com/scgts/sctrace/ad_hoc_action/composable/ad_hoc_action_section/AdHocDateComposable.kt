package com.scgts.sctrace.ad_hoc_action.composable.ad_hoc_action_section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.root.components.R.*
import theme.N900

@Composable
fun AdHocDate(selectedDate: String, onClickDatePicker: () -> Unit, title: String) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            color = N900.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClickDatePicker)
        ) {
            Text(
                text = selectedDate,
                style = MaterialTheme.typography.h6,
            )
            Icon(
                painter = painterResource(drawable.ic_icon_chevron_down_dark),
                contentDescription = stringResource(string.dropdown_chevron_icon_description),
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}