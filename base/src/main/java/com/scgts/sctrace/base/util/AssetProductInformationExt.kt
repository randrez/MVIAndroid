package com.scgts.sctrace.base.util

import com.scgts.sctrace.base.model.AssetProductInformation
import com.scgts.sctrace.base.model.AssetProductInformationCardUiModel
import com.scgts.sctrace.base.model.UnitType

fun List<AssetProductInformation>.toUiModels(unitType: UnitType): List<AssetProductInformationCardUiModel> {
    return this.map { it.uiModel(unitType) }
}

fun AssetProductInformation.uiModel(unitType: UnitType) = AssetProductInformationCardUiModel(
    productDescription = productDescription,
    contractNumber = contractNumber,
    shipmentNumber = shipmentNumber,
    conditionName = conditionName,
    rackLocationName = rackLocationName,
    percentCompletion = if (expectedJoints == 0) 0f else
        (capturedJoints / expectedJoints.toFloat()).coerceAtMost(1f),
    totalTally = formatTally(capturedTally, capturedJoints, unitType, 2),
    expectedTally = formatTally(expectedTally, expectedJoints, unitType, 2)
)