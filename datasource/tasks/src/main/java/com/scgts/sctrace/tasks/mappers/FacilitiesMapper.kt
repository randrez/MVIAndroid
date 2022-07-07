package com.scgts.sctrace.tasks.mappers

import ProjectsQuery
import com.scgts.sctrace.base.model.Facility
import com.scgts.sctrace.base.model.FacilityType
import com.scgts.sctrace.database.model.FacilityEntity

fun ProjectsQuery.Mill.toEntity(): FacilityEntity =
    FacilityEntity(id = id, name = name, facilityType = FacilityType.MILL, selectable = true)

fun ProjectsQuery.Yard.toEntity(): FacilityEntity =
    FacilityEntity(id = id, name = name, facilityType = FacilityType.YARD, selectable = true)

fun ProjectsQuery.Well.toEntity(): FacilityEntity =
    FacilityEntity(id = id, name = name, facilityType = FacilityType.WELL, selectable = true)

fun ProjectsQuery.Rig.toEntity(): FacilityEntity =
    FacilityEntity(id = id, name = name, facilityType = FacilityType.RIG, selectable = true)

fun FacilityEntity.uiModel() = Facility(id = id, name = name)