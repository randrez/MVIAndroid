package com.scgts.sctrace.tasks.mappers

import ProjectsQuery
import com.scgts.sctrace.database.model.ConditionEntity
import com.scgts.sctrace.base.model.Condition

fun ProjectsQuery.Condition_code.toEntity() =
    ConditionEntity(id = id, name = name, conditionCode = condition_code)

fun ConditionEntity.toUiModel() = Condition(id, name)