package com.scgts.sctrace.base.model

enum class TypeWarnings(val message: String) {
    NO_WARNING(
        message = ""
    ),
    WARNING_PRODUCT(
        message = "This asset’s product description is not expected in this order. Do you want to confirm or discard asset from order?"
    ),
    WARNING_CONTRACT(
        message = "This asset’s contract number is not expected in this order. Do you want to confirm or discard asset from order?"
    ),
    WARNING_SHIPMENT(
        message = "This asset’s shipment number is not expected in this order. Do you want to confirm or discard asset from order?"
    ),
    WARNING_CONDITION(
        message = "This asset’s condition is not expected in this order. Do you want to confirm or discard asset from order?"
    ),
    WARNING_RACK_LOCATION(
        message = "This asset’s rack location is not expected in this order. Do you want to confirm or discard asset from order?"
    ),
    WARNING_CONDITION_RACK_LOCATION(
        message = "This asset’s condition and rack location is not expected in this order. Do you want to confirm or discard asset from order?"
    )
}