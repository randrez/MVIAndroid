package com.scgts.sctrace.base.model

enum class Reason(val uiName: String) {
    BoxThreadDamage("Box thread damage"),
    PinThreadDamage("Pin thread damage"),
    PinBoxThreadDamage("Pin and box thread damage"),
    SmashedBox("Smashed box"),
    SmashedPin("Smashed pin"),
    FailedDraft("Failed draft"),
    NotTraceable("Not traceable"),
    Other("Other")
}