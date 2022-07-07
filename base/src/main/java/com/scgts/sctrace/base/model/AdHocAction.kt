package com.scgts.sctrace.base.model

import com.scgts.sctrace.base.model.TaskType.*

sealed class AdHocAction(val displayName: String, val taskId: String) {

    object Dispatch : AdHocAction(
        displayName = "Dispatch",
        taskId = AD_HOC_DISPATCH_TO_WELL.id
    )

    object InboundFromWellSite : AdHocAction(
        displayName = "Inbound from well site",
        taskId = AD_HOC_INBOUND_FROM_WELL_SITE.id
    )

    object InboundFromMill : AdHocAction(
        displayName = "Inbound from mill",
        taskId = AD_HOC_INBOUND_FROM_MILL.id
    )

    object InboundToWell : AdHocAction(
        displayName = "Inbound to well",
        taskId = AD_HOC_INBOUND_TO_WELL.id
    )

    object QuickScan : AdHocAction(
        displayName = "Quick scan",
        taskId = AD_HOC_QUICK_SCAN.id
    )

    object RejectScan : AdHocAction(
        displayName = "Reject scan",
        taskId = AD_HOC_REJECT_SCAN.id
    )

    object RackTransfer : AdHocAction(
        displayName = "Rack transfer",
        taskId = AD_HOC_RACK_TRANSFER.id
    )
}
