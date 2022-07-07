package com.scgts.sctrace.base.model

enum class SubmitStatus {
    NOT_SUBMITTED,
    PENDING,
    SUBMITTED;

    val dbName = "${ordinal}_${name}"
}