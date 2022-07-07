package com.scgts.sctrace.ui.components

import com.airbnb.epoxy.TypedEpoxyController
import com.scgts.sctrace.base.model.UnitType
import com.scgts.sctrace.base.model.AssetProductInformation
import com.scgts.sctrace.base.util.formatScannedLengthTally
import com.scgts.sctrace.base.util.formatTally

class OrderDetailsEpoxyController(val toggleExpand: (ExpandableRow) -> Unit) :
    TypedEpoxyController<OrderDetailsControllerInput>() {
    var unitType: UnitType = UnitType.FEET

    override fun buildModels(viewState: OrderDetailsControllerInput) {
        with(viewState) {

            if (hideInstructions) {
                expandableSectionRow {
                    id("Special instructions")
                    name("Special instructions")
                    expanded(instructionsExpanded)
                    clickListener { toggleExpand(ExpandableRow.INSTRUCTIONS) }
                }
            }

            if (instructionsExpanded) {
                specialInstructionsRow {
                    id("specialInstructions")
                    instructions(specialInstructions)
                }
            }

            expandableSectionRow {
                id("Asset list")
                name("Asset list")
                expanded(assetsExpanded)
                clickListener { toggleExpand(ExpandableRow.ASSETS) }
            }

            if (assetsExpanded) {
                detailsAssetHeaderRow {
                    id("header row")
                }

                info.forEachIndexed { index, assetProductInfo ->
                    var lengthFormat = formatScannedLengthTally(
                        assetProductInfo.capturedTally,
                        assetProductInfo.expectedTally,
                        unitType
                    )
                    var jointFormat =
                        "${assetProductInfo.capturedJoints} / ${assetProductInfo.expectedJoints} JT"

                    if (isDispatchOrBuildOrder) {
                        lengthFormat = formatTally(
                            assetProductInfo.capturedTally,
                            assetProductInfo.capturedJoints,
                            unitType
                        )
                        jointFormat = formatTally(
                            assetProductInfo.expectedTally,
                            assetProductInfo.expectedJoints,
                            unitType
                        )
                    }

                    val percentCompleteByProduct =
                        ((assetProductInfo.capturedJoints.toDouble() / assetProductInfo.expectedJoints) * 100).toInt()
                            .coerceAtMost(100)

                    if (isDispatchOrBuildOrder) detailsAssetOutboundRow {
                        id("$assetProductInfo $index")
                        number(index + 1)
                        name(assetProductInfo.productDescription)
                        joint(jointFormat)
                        length(lengthFormat)
                        contractNumber(assetProductInfo.contractNumber ?: "")
                        shipmentNumber(assetProductInfo.shipmentNumber ?: "")
                        conditionName(assetProductInfo.conditionName ?: "")
                        rackLocationName(assetProductInfo.rackLocationName ?: "")
                        percentCompleteRow(percentCompleteByProduct)
                    } else detailsAssetRow {
                        id("$assetProductInfo $index")
                        number(index + 1)
                        name(assetProductInfo.productDescription)
                        joint(jointFormat)
                        length(lengthFormat)
                    }
                }
            }
        }
    }
}

data class OrderDetailsControllerInput(
    val specialInstructions: String? = null,
    val info: List<AssetProductInformation>,
    val instructionsExpanded: Boolean = false,
    val assetsExpanded: Boolean = false,
    val hideInstructions: Boolean = false,
    val isDispatchOrBuildOrder: Boolean = false,
)

enum class ExpandableRow {
    INSTRUCTIONS,
    ASSETS
}
