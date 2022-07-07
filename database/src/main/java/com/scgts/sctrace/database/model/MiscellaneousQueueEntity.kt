package com.scgts.sctrace.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["payloadType", "payload"])
data class MiscellaneousQueueEntity(
    val payloadType: String,
    val payload: String
)
