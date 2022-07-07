package com.scgts.sctrace.base.model

import androidx.annotation.StringRes

data class ExpandableTextEntry(
    @StringRes val label: Int,
    val body: String,
    val expandable: Boolean = false,
)