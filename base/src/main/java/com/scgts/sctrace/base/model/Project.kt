package com.scgts.sctrace.base.model

import org.threeten.bp.ZonedDateTime

data class Project(
    override val id: String,
    override val name: String,
    val unitOfMeasure: UnitType,
    val lastUpdated: ZonedDateTime?,
    val itimsProjectCode1:String?,
    val itimsProjectCode2:String?
) : Identifiable, Named
