package com.scgts.sctrace.capture

import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.base.model.CaptureMethod.*
import com.scgts.sctrace.root.components.R

fun CaptureMethod.iconResource() = when (this) {
    Camera -> R.drawable.ic_scgts_capture_camera
//    Laser -> R.drawable.ic_scgts_capture_laser
//    Rfid -> R.drawable.ic_scgts_capture_rfid
    Manual -> R.drawable.ic_scgts_capture_manual
    Unknown -> 0
    else -> 0
}
