package com.scgts.sctrace.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.Named
import com.scgts.sctrace.root.components.R
import theme.Blue500
import theme.N500
import theme.N900

@Composable
fun SCTraceDropdown(
    @StringRes label: Int,
    placeholder: String? = null,
    selectedItem: Named? = null,
    list: List<Named> = emptyList(),
    fullScreen: Boolean = false,
    searchable: Boolean = false,
    onItemSelected: (item: Named) -> Unit,
    enabled:Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var filteredList = filterList(list, searchText)

    fun collapseDropdownMenu() {
        expanded = false
        searchText = ""
    }
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) {
                    expanded = !expanded
                    if (!expanded) searchText = ""
                }
        ) {
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.body1,
                color = N900.copy(alpha = 0.6f),
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedItem?.name ?: placeholder ?: "",
                    style = MaterialTheme.typography.h6,
                    color = if(enabled) N900 else N900.copy(alpha = 0.6f)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_icon_chevron_down_dark),
                    contentDescription = stringResource(R.string.dropdown_chevron_icon_description),
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { collapseDropdownMenu() },
            modifier = if (fullScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()
        ) {
            if (fullScreen) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { collapseDropdownMenu() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.close_icon_description),
                        )
                    }
                    Text(
                        text = stringResource(label),
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if (searchable) {
                TextField(
                    value = searchText,
                    onValueChange = { text ->
                        searchText = text
                        filteredList = filterList(list, text)
                    },
                    placeholder = {
                        Text(stringResource(R.string.search) + " " + stringResource(label))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_icon_search),
                            contentDescription = stringResource(R.string.search_icon_description),
                            tint = N500,
                        )
                    },
                    singleLine = true,
                    maxLines = 1,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 36.dp)
                )
            }
            filteredList.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        collapseDropdownMenu()
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.h6,
                            color = N500,
                        )
                        if (item == selectedItem) Icon(
                            painter = painterResource(R.drawable.ic_icon_check),
                            contentDescription = stringResource(R.string.checkmark_icon_description),
                            tint = Blue500
                        )
                    }
                }
                Divider()
            }
        }
    }
}

private fun filterList(list: List<Named>, filter: String): List<Named> {
    return list.filter { it.name.contains(filter, true) }
}