package com.scgts.sctrace.feature.settings.ui.select

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.CaptureMethod

interface SettingsSelectionMvi {
    sealed class Intent : MviIntent {
        object OnBackPressed : Intent()
        data class SelectCaptureMethod(val capture: CaptureMethod) : Intent()
        data class GetCaptureMethod(val captureMethod: CaptureMethod) : Intent()
    }

    data class ViewState(
        override val loading: Boolean = false,
        override val error: Throwable? = null,
        val captureMethod: CaptureMethod = CaptureMethod.Camera,
        val captureActions: List<CaptureMethod> = listOf(
            CaptureMethod.Camera,
            CaptureMethod.Manual
        ),
        val subtitle: String
    ) : MviViewState
}

