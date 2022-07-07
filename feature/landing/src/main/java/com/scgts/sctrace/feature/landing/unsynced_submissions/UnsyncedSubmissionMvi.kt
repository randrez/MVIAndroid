package com.scgts.sctrace.feature.landing.unsynced_submissions

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.UnsyncedSubmission

interface UnsyncedSubmissionMvi {
    sealed class Intent : MviIntent {
        data class SetUnsyncSubmissions(
            val unsyncedSubmissionList: List<UnsyncedSubmission>
        ) : Intent()

        object OnBackPressed : Intent()
    }

    data class ViewState(
        val unsyncedSubmissionList: List<UnsyncedSubmission> = mutableListOf(),
        override val error: Throwable? = null,
        override val loading: Boolean = false
    ) : MviViewState
}