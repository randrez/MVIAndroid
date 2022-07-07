package com.scgts.sctrace.tasks.mappers

import ProjectsQuery
import com.scgts.sctrace.base.model.Project
import com.scgts.sctrace.base.model.UnitType
import com.scgts.sctrace.database.model.ProjectEntity
import com.scgts.sctrace.database.model.ProjectPartialEntity

fun ProjectsQuery.GetMobileUserProject.toEntity() = ProjectPartialEntity(
    id = id,
    name = name,
    unitOfMeasure = uom_type,
    itimsProjectCode1 = itims_project_code_1,
    itimsProjectCode2 = itims_project_code_2
)

fun ProjectPartialEntity.toEntity() = ProjectEntity(
    id = id,
    name = name,
    unitOfMeasure = unitOfMeasure,
    itimsProjectCode1 = itimsProjectCode1,
    itimsProjectCode2 = itimsProjectCode2
)

fun ProjectEntity.toUiModel(): Project = Project(
    id = id,
    name = name,
    unitOfMeasure = UnitType[unitOfMeasure],
    lastUpdated = lastUpdated,
    itimsProjectCode1 = itimsProjectCode1,
    itimsProjectCode2 = itimsProjectCode2
)