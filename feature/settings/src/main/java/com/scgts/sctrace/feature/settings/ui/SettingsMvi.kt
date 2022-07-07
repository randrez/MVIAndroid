package com.scgts.sctrace.feature.settings.ui

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.settings.BuildConfig

const val VERSION =  "Version ".plus(BuildConfig.VERSION_NAME)

interface SettingsMvi {
    sealed class Intent : MviIntent {
        object OnBackPressed : Intent()
        object SelectPreference : Intent()
        object SelectSupport : Intent()
        object Logout : Intent()
        object NoOp : Intent()
        data class UserData(val name: String, val email: String) : Intent()
    }

    data class ViewState(
        override val loading: Boolean = false,
        override val error: Throwable? = null,
        val version: String = VERSION,
        val header: String = "",
        val email: String = "",
        val name: String = "",
        val settingsAction: SettingsAction = SettingsAction.DefaultCapture,
        val supportAction: SettingsAction = SettingsAction.GiveFeedback
    ) : MviViewState
}

