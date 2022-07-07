package com.scgts.sctrace.feature.landing.composable.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.AdHocAction
import com.scgts.sctrace.feature.landing.R

@Composable
fun AdHocActionList(
    adHocActions: List<AdHocAction>,
    setQuickActionDialogVisibility: (Boolean) -> Unit,
    onAdHocActionClicked: (AdHocAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 12.dp
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Text(
                text = stringResource(R.string.task_ad_hoc_actions_text),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold
            )
        }
        itemsIndexed(adHocActions) { index, action ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        setQuickActionDialogVisibility(false)
                        onAdHocActionClicked(action)
                    }
            ) {
                Text(
                    text = action.displayName,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(vertical = contentPadding)
                )
            }
            if (index + 1 < adHocActions.size) Divider()
        }
    }
}