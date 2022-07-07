package com.scgts.sctrace.base.util

import com.scgts.sctrace.base.model.Asset
import com.scgts.sctrace.base.model.AssetCardUiModel

fun List<Asset>.toUiModels(isChecked: Boolean? = null): List<AssetCardUiModel> {
    return this.map { it.uiModel(isChecked) }
}

fun Asset.uiModel(isChecked: Boolean? = null) = AssetCardUiModel(
    id = id,
    name = productDescription(),
    heatNumber = heatNumber,
    pipeNumber = pipeNumber,
    numTags = tags.size,
    tally = length,
    expectedInOrder = expectedInOrder,
    consumed = consumed,
    checked = isChecked ?: checkedForOutbound,
    millWorkNum = millWorkNumber,
    submitStatus = submitStatus
)