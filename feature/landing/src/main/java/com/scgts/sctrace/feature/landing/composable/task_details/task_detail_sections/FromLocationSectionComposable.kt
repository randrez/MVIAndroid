package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.scgts.sctrace.feature.landing.R
import theme.N500

@Composable
fun FromLocationSection(
    fromLocationName: String?,
    modifier: Modifier = Modifier
) {
    TaskDetailSection(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_pin),
                contentDescription = stringResource(R.string.location_pin_icon_description),
                tint = N500
            )
        },
        label = R.string.from,
        body = fromLocationName,
        modifier = modifier
    )
}