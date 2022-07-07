package com.scgts.sctrace.base.util

import com.scgts.sctrace.base.model.Length
import com.scgts.sctrace.base.model.UnitType

fun formatTally(
    expectedLength: Double,
    numJoints: Int,
    unitType: UnitType,
    decimalPlaces: Int = 4
): String {
    val length = Length(expectedLength, unitType)
    return "$numJoints JT / ${length.getFormattedLengthString(decimalPlaces)}"
}

fun formatScannedLengthTally(
    scannedLength: Double,
    totalLengthValue: Double,
    unitType: UnitType,
    decimalPlaces: Int = 4
): String {
    val totalLength = Length(totalLengthValue, unitType)
    return String.format("%.${decimalPlaces}f", scannedLength) +
            " / ${totalLength.getFormattedLengthString(decimalPlaces)}"
}