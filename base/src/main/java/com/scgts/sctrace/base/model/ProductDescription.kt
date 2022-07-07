package com.scgts.sctrace.base.model

data class ProductDescription(
    val productId: String,
    val outerDiameter: Double,
    val weight: Double,
    val grade: String,
    val endFinish: String,
    val range: String,
    val commodity: String,
) {
    fun formattedDescription() = "$outerDiameter $weight $grade $endFinish $range $commodity"
}