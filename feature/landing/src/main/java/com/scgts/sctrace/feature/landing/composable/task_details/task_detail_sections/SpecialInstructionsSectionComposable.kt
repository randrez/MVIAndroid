package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.feature.landing.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpecialInstructionsSection(
    specialInstructions: String?
) {
    var specialInstructionsExpanded by remember { mutableStateOf(false) }
    val chevronDirection by animateFloatAsState(targetValue = if (specialInstructionsExpanded) 180f else 0f)
    if (!specialInstructions.isNullOrEmpty()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { specialInstructionsExpanded = !specialInstructionsExpanded }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.special_instructions),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_down),
                        contentDescription = stringResource(R.string.chevron_icon_description),
                        modifier = Modifier.rotate(chevronDirection)
                    )
                }
            }
            AnimatedVisibility(
                visible = specialInstructionsExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = specialInstructions,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
        Divider()
    }
}