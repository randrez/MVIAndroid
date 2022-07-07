package com.scgts.sctrace.feature.landing.composable.filter_and_sort

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.FilterAndSortCategory
import com.scgts.sctrace.base.model.FilterAndSortCategory.*
import com.scgts.sctrace.base.model.FilterAndSortOption
import com.scgts.sctrace.base.model.FilterAndSortOption.*
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.ui.components.TaskStatusTextButton
import theme.Blue500
import theme.N100
import theme.N900

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilterAndSortOptionList(
    selectedCategory: FilterAndSortCategory,
    onOptionClicked: (FilterAndSortCategory, FilterAndSortOption) -> Unit,
) {
    LazyColumn {
        when (selectedCategory) {
            is None -> {
            }
            is Sort -> items(selectedCategory.list) { option ->
                SortOption(option) { onOptionClicked(selectedCategory, option) }
            }
            is Project, is TaskType -> items(selectedCategory.options) { option ->
                FilterOption(
                    option = option as FilterOption,
                    numOfTasks = option.numOfTasks,
                    onOptionClicked = { onOptionClicked(selectedCategory, option) },
                )
            }
            is Status -> items(selectedCategory.list) { option ->
                FilterOption(
                    option = option,
                    numOfTasks = option.numOfTasks,
                    onOptionClicked = { onOptionClicked(selectedCategory, option) },
                ) {
                    TaskStatusTextButton(status = option.status, enabled = option.numOfTasks > 0)
                }
            }
            is FromLocation, is ToLocation -> {
                val facilitiesByType =
                    selectedCategory.options.groupBy { (it as LocationFilterOption).facilityType }
                facilitiesByType.forEach { (facilityType, facilities) ->
                    stickyHeader {
                        Text(
                            text = facilityType.displayName,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .background(Color.White)
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }
                    items(facilities) { facility ->
                        FilterOption(
                            option = facility as LocationFilterOption,
                            numOfTasks = facility.numOfTasks,
                            onOptionClicked = { onOptionClicked(selectedCategory, facility) }
                        )
                    }
                    item { Spacer(modifier = Modifier.size(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SortOption(
    option: SortOption,
    onOptionClicked: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onOptionClicked() }
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = option.name,
            style = MaterialTheme.typography.h6,
        )
        if (option.checked) Icon(
            painter = painterResource(R.drawable.ic_icon_check),
            contentDescription = null,
            tint = Blue500,
        )
    }
    Divider()
}

@Composable
private fun FilterOption(
    option: FilterAndSortOption,
    numOfTasks: Int,
    onOptionClicked: () -> Unit,
    child: (@Composable () -> Unit)? = null,
) {
    val enabled = numOfTasks > 0
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .then(if (enabled) Modifier.clickable { onOptionClicked() } else Modifier)
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        child?.let { child() } ?: FilterText(option.name, enabled)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterText(
                text = "($numOfTasks)",
                enabled = enabled
            )
            Checkbox(
                checked = option.checked,
                onCheckedChange = null,
                enabled = enabled,
                colors = CheckboxDefaults.colors(
                    checkedColor = Blue500,
                    checkmarkColor = Color.White,
                    disabledColor = N100,
                ),
            )
        }
    }
    Divider()
}

@Composable
private fun FilterText(
    text: String,
    enabled: Boolean,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        color = if (enabled) N900 else N100,
    )
}