package com.scgts.sctrace.base.model

sealed class SettingsKey(val key: String) {
    object Capture : SettingsKey("capture")
    object UnitOfMeasurement : SettingsKey("unit")
}