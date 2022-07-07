package com.scgts.sctrace.base.model

data class DeleteTagPayload(
    val assetId: String,
    val tag: String
)