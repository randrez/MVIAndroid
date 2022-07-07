package com.scgts.sctrace.feature.landing.composable.tablet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scgts.sctrace.base.model.AdHocAction
import com.scgts.sctrace.feature.landing.composable.tasks.AdHocActionList

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TabletAdHocActionDialog(
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
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .clickable { setQuickActionDialogVisibility(false) }
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color.White,
                modifier = Modifier
                    .width(275.dp)
                    .padding(bottom = 60.dp)
            ) {
                AdHocActionList(
                    adHocActions = adHocActions,
                    setQuickActionDialogVisibility = setQuickActionDialogVisibility,
                    onAdHocActionClicked = onAdHocActionClicked,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 30.dp,
                        bottom = 8.dp
                    )
                )
            }
        }
    }
}