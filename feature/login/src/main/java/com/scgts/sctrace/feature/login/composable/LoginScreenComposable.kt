package com.scgts.sctrace.feature.login.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.feature.login.R
import com.scgts.sctrace.feature.login.ui.LoginMvi.ViewState
import theme.N500
import theme.SCGTSTheme

@Composable
fun LoginScreen(
    viewState: LiveData<ViewState>,
    onLoginClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo_sct),
                    contentDescription = stringResource(R.string.logo_sct),
                )
                Text(
                    text = stringResource(R.string.login),
                    color = N500,
                    style = MaterialTheme.typography.h6,
                )
                OktaLoginButton(
                    onLoginClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    SCGTSTheme {
        LoginScreen(
            viewState = MutableLiveData(ViewState()),
            onLoginClick = { }
        )
    }
}