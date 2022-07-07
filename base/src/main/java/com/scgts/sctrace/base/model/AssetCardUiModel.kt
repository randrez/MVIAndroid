package com.scgts.sctrace.base.model

data class AssetCardUiModel(
    val id: String,
    val name: String,
    val heatNumber: String,
    val pipeNumber: String,
    val numTags: Int,
    val tally: Double,
    val expectedInOrder: Boolean = true,
    val consumed: Boolean? = null,
    val checked: Boolean? = null,
    val millWorkNum: String? = null,
    val submitStatus: SubmitStatus = SubmitStatus.NOT_SUBMITTED
)