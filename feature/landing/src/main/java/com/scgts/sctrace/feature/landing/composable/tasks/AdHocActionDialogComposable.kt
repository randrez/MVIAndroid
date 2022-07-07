package com.scgts.sctrace.feature.landing.composable.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scgts.sctrace.base.model.AdHocAction

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AdHocActionDialog(
    adHocActions: List<AdHocAction>,
    setQuickActionDialogVisibility: (Boolean) -> Unit,
    onAdHocActionClicked: (AdHocAction) -> Unit
) {
    Dialog(
        onDismissRequest = { setQuickActionDialogVisibility(false) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .clickable { setQuickActionDialogVisibility(false) }
        ) {
            Card(
                shape = RectangleShape,
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                AdHocActionList(
                    adHocActions = adHocActions,
                    contentPadding = 14.dp,
                    setQuickActionDialogVisibility = setQuickActionDialogVisibility,
                    onAdHocActionClicked = onAdHocActionClicked,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 20.dp,
                        bottom = 60.dp
                    )
                )
            }
        }
    }
}