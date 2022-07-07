package com.scgts.sctrace.feature.tablet

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState

interface BaseTabletMvi {
    sealed class Intent : MviIntent {
        data class SetUnsubmittedTaskCount(val count: Int) : Intent()
        data class GoToUnsyncedSubmissions(val originName: String) : Intent()
        data class GoToSettings(val originName: String) : Intent()
        object GoBack : Intent()
    }
    data class ViewState(
        override val loading: Boolean = false,
        override val error: Throwable? = null,
        val unsubmittedTaskCount: Int = 0,
    ) : MviViewState
}