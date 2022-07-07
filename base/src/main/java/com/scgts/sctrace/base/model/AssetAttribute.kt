package com.scgts.sctrace.base.model

enum class AssetAttribute(val uiName: String, val attributeName: String) {
    Manufacturer("Manufacturer", "millName"),
    MillWorkNumber("Mill work no.", "millWorkNumber"),
    HeatNumber("Heat no.", "heatNumber"),
    PipeNumber("Pipe no.", "pipeNumber"),
    ExMillDate("Ex mill date", "exMillDate")
}
