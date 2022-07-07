package com.scgts.sctrace.feature.login.ui

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState

interface LoginMvi {
    sealed class Intent : MviIntent {
        //view intents
        object Login : Intent()

        //data intents
    }

    data class ViewState(
        override val error: Throwable? = null,
        override val loading: Boolean = false
    ) : MviViewState
}