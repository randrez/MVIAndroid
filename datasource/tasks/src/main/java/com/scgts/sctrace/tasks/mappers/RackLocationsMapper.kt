package com.scgts.sctrace.tasks.mappers

import ProjectsQuery
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.database.model.RackLocationEntity

fun ProjectsQuery.Location.toEntity(facilityId: String): RackLocationEntity =
    RackLocationEntity(id = id, name = name, facilityId = facilityId, selectable = true)

fun RackLocationEntity.uiModel() = RackLocation(id = id, name = name)