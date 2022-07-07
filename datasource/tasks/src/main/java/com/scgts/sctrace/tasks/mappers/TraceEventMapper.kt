package com.scgts.sctrace.tasks.mappers

import com.scgts.sctrace.database.model.TraceEventEntity
import com.scgts.sctrace.base.model.TraceEvent

fun TraceEventEntity.toUiModel(): TraceEvent = TraceEvent(
    taskId = taskId,
    assetId = assetId,
    capturedAt = scannedAt,
    conditionId = conditionId,
    rackLocationId = rackLocationId,
    consumed = consumed,
    laserLength = laserLength,
    userId = userId,
    userName = userName
)