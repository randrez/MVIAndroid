package com.scgts.sctrace.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.scgts.sctrace.root.components.R
import theme.N900

@Composable
fun HeaderSettings(
    headerTitle: String,
    headerSubtitle: String,
    onBackClick: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onBackClick)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = stringResource(id = R.string.back_button_icon_description),
                modifier = Modifier
                    .size(24.dp),
                tint = N900
            )
            Text(
                text = headerTitle,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            text = headerSubtitle,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(top = 30.dp),
            fontWeight = FontWeight.Bold,
        )
    }
}