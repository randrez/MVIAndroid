package com.scgts.sctrace.feature.landing.tasks

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.AdHocAction
import com.scgts.sctrace.base.model.Task
import com.scgts.sctrace.base.model.TaskCardUiModel

interface TasksMvi {
    sealed class Intent : MviIntent {
        //view intents
        object RefreshData : Intent()
        data class GoToUnsyncedSubmissions(val originName: String) : Intent()
        data class GoToSettings(val originName: String) : Intent()
        object FilterAndSortClicked : Intent()
        data class TaskClicked(val taskId: String) : Intent()
        data class OnTaskSelected(val taskId: String) : Intent()
        data class AdHocActionClicked(val adHocAction: AdHocAction) : Intent()

        //data intents
        data class TasksData(val tasks: List<TaskCardUiModel>) : Intent()
        data class SetFilterCount(val count: Int) : Intent()
        data class SetUnfilteredTasksCount(val count: Int) : Intent()
        data class SetPendingTaskCount(val count: Int) : Intent()
        data class AdHocActions(val adHocActions: List<AdHocAction>) : Intent()
        data class SetSelectedTask(val selectedTask: Task) : Intent()
        data class Error(val t: Throwable) : Intent()
        object NoOp : Intent()
    }

    data class ViewState(
        override val loading: Boolean = false,
        override val error: Throwable? = null,
        val tasks: List<TaskCardUiModel> = emptyList(),
        val isRefreshing: Boolean = false,
        val filterCount: Int = 0,
        val unfilteredTasksCount: Int = 0,
        val pendingTaskCount: Int = 0,
        val adHocActions: List<AdHocAction> = emptyList(),
        val selectedTask: Task? = null,
    ) : MviViewState
}

