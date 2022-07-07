package com.scgts.sctrace.base.model

data class RackLocation(
    override val id: String,
    override val name: String
): Identifiable, Named