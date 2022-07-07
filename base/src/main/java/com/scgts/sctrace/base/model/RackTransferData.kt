package com.scgts.sctrace.base.model

data class RackTransferData(
    val millWorkNumber: String,
    val rackId: String,
    val rackName: String,
    val productId: String,
    val outerDiameter: Double,
    val weight: Double,
    val grade: String,
    val endFinish: String,
    val range: String,
    val commodity: String,
    val expectedLength: Double,
    val joints: Int
) {
    fun productDescription() = "$outerDiameter $weight $grade $endFinish $range $commodity"
}
