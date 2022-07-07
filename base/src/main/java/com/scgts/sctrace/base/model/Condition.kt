package com.scgts.sctrace.base.model

data class Condition(
    override val id: String,
    override val name: String,
): Identifiable, Named