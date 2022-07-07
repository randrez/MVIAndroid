package com.scgts.sctrace.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserProjectRoleEntity(
    @PrimaryKey val projectId: String,
    val isDrillingEngineer: Boolean,
    val isYardOperator: Boolean,
    val isAuditor: Boolean,
)