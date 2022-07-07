package com.scgts.sctrace.feature.login.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.feature.login.R
import theme.N100
import theme.N500

@Composable
fun OktaLoginButton(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { onLoginClick() },
        border = BorderStroke(color = N500, width = 1.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
        ),
        contentPadding = PaddingValues(20.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo_okta),
                contentDescription = stringResource(R.string.okta_logo),
            )
            Divider(
                color = N100,
                modifier = Modifier
                    .fillMaxHeight()
                    .size(1.dp)
            )
            Text(
                text = stringResource(R.string.okta_login),
                color = N500,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}