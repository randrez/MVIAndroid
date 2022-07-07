package com.scgts.sctrace.base.model

enum class PayloadType(val serverName: String) {
    DELETE_TAG("deleteTag"),
    USER_FEEDBACK("userFeedback"),
    UNKNOWN("unknown")
}