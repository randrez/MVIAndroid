package com.scgts.sctrace.user.mappers

import RolesQuery
import com.scgts.sctrace.base.model.UserRole
import com.scgts.sctrace.database.model.UserProjectRoleEntity

fun RolesQuery.Project_role.toEntity(): UserProjectRoleEntity {
    val roleIds = roles.map { role -> role.id }
    return UserProjectRoleEntity(
        projectId = id,
        isDrillingEngineer = roleIds.contains(DRILLING_ENGINEER_ROLE_ID),
        isYardOperator = roleIds.contains(YARD_OPERATOR_ROLE_ID),
        isAuditor = roleIds.contains(AUDITOR_ROLE_ID),
    )
}

fun UserProjectRoleEntity.toUiModel() = UserRole(
    isDrillingEngineer = isDrillingEngineer,
    isYardOperator = isYardOperator,
    isAuditor = isAuditor,
)

const val YARD_OPERATOR_ROLE_ID = "7767b41e-c818-4fbc-9f9f-ce4a290ce9f2"
const val DRILLING_ENGINEER_ROLE_ID = "b62cc303-46e1-4e63-af46-5be88644e2f6"
const val AUDITOR_ROLE_ID = "c948ccb4-7c80-44af-bb7d-326f16c7e58a"
