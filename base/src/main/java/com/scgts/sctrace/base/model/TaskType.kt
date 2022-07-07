package com.scgts.sctrace.base.model

//TODO: use uuid from the backend instead of display name
enum class TaskType(
    val id: String = "",
    val serverName: String,
    val displayName: String,
    val typeForFiltering: TaskTypeForFiltering,
) {
    BUILD_ORDER(
        serverName = "Build order",
        displayName = "Build order",
        typeForFiltering = TaskTypeForFiltering.BUILD_ORDER,
    ),
    DISPATCH(
        serverName = "Dispatch",
        displayName = "Dispatch",
        typeForFiltering = TaskTypeForFiltering.DISPATCH,
    ),
    DISPATCH_TO_YARD(
        serverName = "Dispatch to yard",
        displayName = "Dispatch to yard",
        typeForFiltering = TaskTypeForFiltering.DISPATCH,
    ),
    DISPATCH_TO_WELL(
        serverName = "Dispatch to well",
        displayName = "Dispatch to well",
        typeForFiltering = TaskTypeForFiltering.DISPATCH,
    ),
    INBOUND_TO_WELL(
        serverName = "Inbound to well",
        displayName = "Inbound to well",
        typeForFiltering = TaskTypeForFiltering.INBOUND_TO_WELL,
    ),
    INBOUND_FROM_MILL(
        serverName = "Inbound from mill",
        displayName = "Inbound from mill",
        typeForFiltering = TaskTypeForFiltering.INBOUND_FROM_MILL,
    ),
    CONSUME(
        serverName = "Consume",
        displayName = "Consume",
        typeForFiltering = TaskTypeForFiltering.CONSUME,
    ),
    INBOUND_FROM_WELL_SITE(
        serverName = "Inbound from well site",
        displayName = "Inbound from well site",
        typeForFiltering = TaskTypeForFiltering.INBOUND_FROM_WELL_SITE,
    ),
    RACK_TRANSFER(
        serverName = "Rack transfer",
        displayName = "Rack transfer",
        typeForFiltering = TaskTypeForFiltering.RACK_TRANSFER,
    ),

    // locally created quick action (ad hoc) tasks
    AD_HOC_QUICK_SCAN(
        id = "ad_hoc_quick_scan",
        serverName = "Ad hoc quick scan",
        displayName = "Ad hoc quick scan",
        typeForFiltering = TaskTypeForFiltering.AD_HOC,
    ),
    AD_HOC_INBOUND_TO_WELL(
        id = "ad_hoc_inbound_to_well",
        serverName = "Ad hoc inbound to well",
        displayName = "Ad hoc inbound to well",
        typeForFiltering = TaskTypeForFiltering.AD_HOC,
    ),
    AD_HOC_INBOUND_FROM_MILL(
        id = "ad_hoc_inbound_from_mill",
        serverName = "Ad hoc inbound from mill",
        displayName = "Ad hoc inbound from mill",
        typeForFiltering = TaskTypeForFiltering.AD_HOC,
    ),
    AD_HOC_INBOUND_FROM_WELL_SITE(
        id = "ad_hoc_inbound_from_well_site",
        serverName = "Ad hoc inbound from well site",
        displayName = "Ad hoc inbound from well site",
        typeForFiltering = TaskTypeForFiltering.AD_HOC,
    ),
    AD_HOC_DISPATCH_TO_YARD(
        id = "ad_hoc_dispatch_to_yard",
        serverName = "Ad hoc dispatch to yard",
        displayName = "Ad hoc dispatch to yard",
        typeForFiltering = TaskTypeForFiltering.AD_HOC,
    ),
    AD_HOC_DISPATCH_TO_WELL(
        id = "ad_hoc_dispatch_to_well",
        serverName = "Ad hoc dispatch to well",
        displayName = "Ad hoc dispatch to well",
        typeForFiltering = TaskTypeForFiltering.AD_HOC,
    ),
    AD_HOC_REJECT_SCAN(
        id = "ad_hoc_reject_scan",
        serverName = "Ad hoc reject scan",
        displayName = "Ad hoc reject scan",
        typeForFiltering = TaskTypeForFiltering.AD_HOC,
    ),
    AD_HOC_RACK_TRANSFER(
        id = "ad_hoc_rack_transfer",
        serverName = "Ad hoc rack transfer",
        displayName = "Ad hoc rack transfer",
        typeForFiltering = TaskTypeForFiltering.AD_HOC,
    ),
    NO_TYPE(
        serverName = "¯\\_(ツ)_/¯",
        displayName = "¯\\_(ツ)_/¯",
        typeForFiltering = TaskTypeForFiltering.AD_HOC,
    );

    companion object {
        private val map = values().associateBy(TaskType::serverName)
        operator fun get(displayName: String) = map[displayName] ?: NO_TYPE
    }
}
