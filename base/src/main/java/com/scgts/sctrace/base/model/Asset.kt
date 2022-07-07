package com.scgts.sctrace.base.model

data class Asset(
    override val id: String,
    val length: Double,
    val weight: Double, // in lbs per ft
    val millName: String,
    val rackLocationId: String?,
    val tags: List<String>,
    val heatNumber: String,
    val pipeNumber: String,
    val outerDiameter: Double,
    val grade: String,
    val range: String,
    val endFinish: String,
    val runningLength: Double,
    val millWorkNumber: String,
    val productId: String,
    val commodity: String,
    val expectedInOrder: Boolean = true,
    val consumed: Boolean? = null,
    val checkedForOutbound: Boolean? = null,
    val makeUpLossFt: Double,
    val conditionId: String?,
    val shipmentNumber: String?,
    val contractNumber: String,
    val projectId: String,
    val submitStatus: SubmitStatus = SubmitStatus.NOT_SUBMITTED
) : Identifiable {
    fun productDescription() = "$outerDiameter $weight $grade $endFinish $range $commodity"
}
