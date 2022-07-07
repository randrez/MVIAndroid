package com.scgts.sctrace.user.mappers

import com.scgts.sctrace.base.model.User
import com.scgts.sctrace.database.model.UserEntity

fun UserEntity.toUiModel() = User(
    id = id,
    name = name,
    email = email
)
