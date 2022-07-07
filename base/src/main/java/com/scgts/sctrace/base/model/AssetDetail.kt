package com.scgts.sctrace.base.model

import androidx.annotation.StringRes

data class AssetDetail(
    @StringRes val label: Int,
    val value: String
) {
    constructor(@StringRes label: Int, value: Number) : this(label, value.toString())
}