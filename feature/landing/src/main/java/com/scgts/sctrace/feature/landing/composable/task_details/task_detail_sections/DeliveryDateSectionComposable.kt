package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.scgts.sctrace.feature.landing.R

@Composable
fun DeliveryDateSection(
    deliveryDate: String?,
    modifier: Modifier = Modifier
) {
    TaskDetailSection(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = stringResource(R.string.calendar_icon_description),
            )
        },
        label = R.string.delivery_date,
        body = deliveryDate ?: "",
        modifier = modifier
    )
}
