package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections.TaskDetailSection
import theme.Red

@Composable
fun ToLocationSection(
    toLocationName: String?,
    modifier: Modifier = Modifier
) {
    TaskDetailSection(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_location),
                contentDescription = stringResource(R.string.location_icon_description),
                tint = Red
            )
        },
        label = R.string.to,
        body = toLocationName,
        modifier = modifier
    )
}