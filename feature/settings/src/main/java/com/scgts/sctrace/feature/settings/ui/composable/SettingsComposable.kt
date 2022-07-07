package com.scgts.sctrace.feature.settings.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.feature.settings.ui.SettingsAction
import com.scgts.sctrace.feature.settings.ui.SettingsMvi.*
import com.scgts.sctrace.settings.R
import com.scgts.sctrace.ui.components.HeaderSettings
import theme.Gray
import theme.N900
import theme.SCGTSTheme
import theme.Slate

@Composable
fun Settings(
    viewState: LiveData<ViewState>,
    selectedPreference: (SettingsAction) -> Unit,
    selectedSupport: (SettingsAction) -> Unit,
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {
    viewState.observeAsState().value?.let { state ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            HeaderSettings(
                headerTitle = state.header,
                onBackClick = onBackClick,
                headerSubtitle = stringResource(id = R.string.account)
            )
            Text(
                text = state.name,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            Text(
                text = state.email,
                style = MaterialTheme.typography.body1,
                color = Slate
            )
            SettingActionWithTitle(
                stringResource(id = R.string.preferences),
                settingAction = state.settingsAction,
                modifier = Modifier.padding(top = 29.dp),
                onSelectSetting = selectedPreference
            )
            SettingActionWithTitle(
                stringResource(id = R.string.support),
                settingAction = state.supportAction,
                modifier = Modifier.padding(top = 20.dp),
                onSelectSetting = selectedSupport
            )
            Button(
                onClick = onLogout,
                contentPadding = PaddingValues(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.logout),
                    color = Color.White
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            ) {
                Text(
                    text = state.version,
                    style = MaterialTheme.typography.caption,
                    color = Slate,
                    fontWeight = FontWeight.Medium
                )
            }

        }
    }
}

@Composable
fun SettingActionWithTitle(
    title: String,
    settingAction: SettingsAction,
    modifier: Modifier,
    onSelectSetting: (SettingsAction) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
            color = Slate
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Gray)
                    .clickable(onClick = { onSelectSetting(settingAction) })
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = settingAction.icon),
                            modifier = Modifier
                                .size(24.dp),
                            contentDescription = settingAction.name,
                            tint = N900
                        )
                        Text(
                            text = settingAction.name,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(start = 10.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_icon_chevron_right),
                        modifier = Modifier
                            .size(24.dp),
                        contentDescription = settingAction.name
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettings() {
    val viewState = MutableLiveData(
        ViewState(
            email = stringResource(id = R.string.email),
            name = stringResource(id = R.string.name),
            header = stringResource(id = R.string.tasks)
        )
    )
    SCGTSTheme {
        Settings(
            viewState = viewState,
            onBackClick = {},
            onLogout = {},
            selectedPreference = {},
            selectedSupport = {}
        )
    }
}