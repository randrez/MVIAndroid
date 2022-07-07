package com.scgts.sctrace.base.model

import com.scgts.sctrace.base.model.UnitType.FEET

data class Length(
    val value: Double,
    val unitType: UnitType,
) {
    constructor(
        length: Double,
        unitType: String,
    ) : this(length, UnitType[unitType])

    fun getFormattedLengthString(decimalPlaces: Int = 4): String {
        return String.format("%.${decimalPlaces}f", value) + " " + unitType.abbreviation
    }

    companion object {
        fun emptyLength(unitType: UnitType = FEET): Length {
            return Length(0.0, unitType)
        }
    }
}