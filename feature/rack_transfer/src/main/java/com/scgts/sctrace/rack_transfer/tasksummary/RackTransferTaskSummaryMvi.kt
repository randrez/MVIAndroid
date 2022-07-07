package com.scgts.sctrace.rack_transfer.tasksummary

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.ExpandableTextEntry
import com.scgts.sctrace.base.model.RackTransferModel
import com.scgts.sctrace.base.model.Task

interface RackTransferTaskSummaryMvi {
    sealed class Intent : MviIntent {

        //view intents
        object BackClicked : Intent()
        object SubmitClicked : Intent()
        object NavigateToTasksOnPostSubmit : Intent()
        object CaptureClicked : Intent()
        object SeeDetails : Intent()
        data class EditClicked(
            val rackId: String,
            val millWorkNum: String,
            val productDescription: String,
        ) : Intent()
        data class RackTransferClicked(
            val rackId: String,
            val millWorkNum: String,
            val productDescription: String,
        ) : Intent()

        //data intents
        object Submitted : Intent()
        object OfflineSubmitted : Intent()
        data class TaskData(val task: Task) : Intent()
        data class ExpandableSummaryData(val summaryListExpandable: List<ExpandableTextEntry>) :
            Intent()
        data class RackTransferData(val rackTransfers: List<RackTransferModel>) : Intent()
    }

    data class ViewState(
        val task: Task? = null,
        val rackTransfers: List<RackTransferModel> = emptyList(),
        val selectableAssets: Boolean = false,
        val submitted: Boolean = false,
        val isOfflineSubmitted: Boolean = false,
        val summaryExpanded: Boolean = false,
        val summaryListExpandable: List<ExpandableTextEntry> = emptyList(),
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState

}