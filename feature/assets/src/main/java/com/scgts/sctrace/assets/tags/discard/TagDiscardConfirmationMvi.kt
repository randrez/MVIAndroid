package com.scgts.sctrace.assets.tags.discard

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState

interface TagDiscardConfirmationMvi {
    sealed class Intent: MviIntent {
        object ConfirmDiscardClick: Intent()
        object CancelDiscardClick: Intent()
        object Dismiss: Intent()
    }

    data class ViewState(
        val dismiss: Boolean = false,
        override val error: Throwable? = null,
        override val loading: Boolean = false
    ): MviViewState
}