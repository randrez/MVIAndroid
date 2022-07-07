package com.scgts.sctrace.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String
)
