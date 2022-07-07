package com.scgts.sctrace.capture

import androidx.annotation.StringRes
import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.CaptureMethod

interface CaptureMvi {
    sealed class Intent : MviIntent {
        // view intents
        object CaptureMethodButtonClicked : Intent()
        object Exit : Intent()
        data class CaptureMethodSelected(val captureMethod: CaptureMethod) : Intent()
        object NoOp : Intent()

        // data intents
        data class SetCaptureMethod(val captureMethod: CaptureMethod) : Intent()
        data class SetToolbar(
            val actions: List<CaptureMethod>,
            @StringRes val title: Int,
        ) : Intent()
    }

    data class ViewState(
        val captureMethods: List<CaptureMethod> = emptyList(),
        @StringRes val screenTitle: Int = R.string.capture_screen_title,
        val showCaptureMethodOptions: Boolean = false,
        val selectedCaptureMethod: CaptureMethod,
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState
}
