package com.scgts.sctrace.feature.settings.ui

import androidx.annotation.DrawableRes
import com.scgts.sctrace.settings.R

sealed class SettingsAction(val name: String, @DrawableRes val icon: Int) {
    object DefaultCapture : SettingsAction("Default Capture Mode", R.drawable.ic_icon_capture)
    object Unknown : SettingsAction("", 0)
    object GiveFeedback : SettingsAction("Give feedback", R.drawable.ic_icon_chat_bubble)

    companion object {
        fun fromName(name: String) = when (name) {
            "Default Capture Mode" -> DefaultCapture
            "Give feedback" -> GiveFeedback
            else -> Unknown
        }
    }
}
