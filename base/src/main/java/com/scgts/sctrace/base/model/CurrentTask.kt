package com.scgts.sctrace.base.model

data class CurrentTask(
    val id: String?,
    val projectId: String,
    val quickReject: Boolean = false
)