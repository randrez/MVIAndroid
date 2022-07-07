package com.scgts.sctrace.tasks.mappers

import com.google.gson.Gson
import com.scgts.sctrace.database.model.MiscellaneousQueueEntity
import com.scgts.sctrace.base.model.DeleteTagPayload
import com.scgts.sctrace.base.model.MiscellaneousQueue
import com.scgts.sctrace.base.model.PayloadType
import com.scgts.sctrace.base.model.UserFeedbackPayload

fun MiscellaneousQueueEntity.uiModel(): com.scgts.sctrace.base.model.MiscellaneousQueue {
    val gson = Gson()
    return when (payloadType) {
        com.scgts.sctrace.base.model.PayloadType.DELETE_TAG.serverName -> {
            com.scgts.sctrace.base.model.MiscellaneousQueue(
                payloadType = com.scgts.sctrace.base.model.PayloadType.DELETE_TAG,
                payload = gson.fromJson(payload,
                    com.scgts.sctrace.base.model.DeleteTagPayload::class.java)
            )
        }
        com.scgts.sctrace.base.model.PayloadType.USER_FEEDBACK.serverName -> {
            com.scgts.sctrace.base.model.MiscellaneousQueue(
                payloadType = com.scgts.sctrace.base.model.PayloadType.USER_FEEDBACK,
                payload = gson.fromJson(payload,
                    com.scgts.sctrace.base.model.UserFeedbackPayload::class.java)
            )
        }
        else -> com.scgts.sctrace.base.model.MiscellaneousQueue(
            payloadType = com.scgts.sctrace.base.model.PayloadType.UNKNOWN,
            payload = ""
        )
    }
}

fun com.scgts.sctrace.base.model.MiscellaneousQueue.toEntity(): MiscellaneousQueueEntity {
    val gson = Gson()
    return MiscellaneousQueueEntity(
        payloadType = payloadType.serverName,
        payload = gson.toJson(payload)
    )
}