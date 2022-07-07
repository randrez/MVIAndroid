package com.scgts.sctrace.base.model

enum class FacilityType(val displayName: String, val serverId: String) {
    MILL(displayName = "Mills", serverId = "a0714dd3-bb90-4e18-bdfa-df5f07b45eb1"),
    YARD(displayName = "Yards", serverId = "ba16a400-02c9-4c79-8d64-2cc3ae14a473"),
    RIG(displayName = "Rigs", serverId = "issa rig"),
    WELL(displayName = "Wells", serverId = "issa well"), // TODO: REAL SERVER ID FOR WELL AND RIG
}