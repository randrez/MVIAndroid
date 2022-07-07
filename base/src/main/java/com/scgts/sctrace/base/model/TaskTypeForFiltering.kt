package com.scgts.sctrace.base.model

enum class TaskTypeForFiltering(val displayName: String) {
    AD_HOC("Ad hoc"),
    BUILD_ORDER("Build order"),
    CONSUME("Consume"),
    DISPATCH("Dispatch"),
    INBOUND_FROM_MILL("Inbound from mill"),
    INBOUND_FROM_WELL_SITE("Inbound from well site"),
    INBOUND_TO_WELL("Inbound to well"),
    RACK_TRANSFER("Rack transfer");
}