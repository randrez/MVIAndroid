package com.scgts.sctrace.root

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState

interface RootMvi {
    sealed class Intent : MviIntent {
        object OnResume : Intent()
        object NoOp: Intent()
        data class Submitted(val message: String): Intent()
        object SubmitError: Intent()
        object SyncError: Intent()
        object DebugBuild: Intent()
    }

    data class ViewState(
        val syncError: Boolean = false,
        val submitError: Boolean = false,
        val toastMessage: String = "",
        override val error: Throwable? = null,
        override val loading: Boolean = false
    ) : MviViewState
}

