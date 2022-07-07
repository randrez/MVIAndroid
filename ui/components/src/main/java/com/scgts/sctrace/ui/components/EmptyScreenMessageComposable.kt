package com.scgts.sctrace.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.root.components.R
import theme.SCGTSTheme

@Composable
fun EmptyScreenMessage(
    @StringRes title: Int? = null,
    @StringRes message: Int? = null,
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .width(200.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo_sct),
                contentDescription = stringResource(R.string.logo_sct),
            )
            if (title != null || message != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    title?.let {
                        Text(
                            text = stringResource(it),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    message?.let {
                        Text(
                            text = stringResource(it),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    SCGTSTheme {
        EmptyScreenMessage(
            title = R.string.start_transferring,
            message = R.string.message_new_transfer
        )
    }
}