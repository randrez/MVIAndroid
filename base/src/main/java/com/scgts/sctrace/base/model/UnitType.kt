package com.scgts.sctrace.base.model

enum class UnitType(val abbreviation: String) {
    FEET("FT"),
    METER("M"),
    UNKNOWN("??");

    companion object {
        private val map = values().associateBy(UnitType::abbreviation)
        operator fun get(unitType: String) = map[unitType.uppercase()] ?: UNKNOWN
    }
}