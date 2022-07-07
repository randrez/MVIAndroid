package com.scgts.sctrace.base.model

sealed class CaptureMethod(val name: String) {
    object Camera : CaptureMethod("Camera")
//    object Laser : CaptureMethod("Laser")
//    object Rfid : CaptureMethod("RFID")
    object Manual : CaptureMethod("Manual")
    object Unknown : CaptureMethod("")

    companion object {
        fun fromName(name: String) = when (name) {
            "Camera" -> Camera
//            "Laser" -> Laser
//            "RFID" -> Rfid
            "Manual" -> Manual
            else -> Unknown
        }
    }
}
