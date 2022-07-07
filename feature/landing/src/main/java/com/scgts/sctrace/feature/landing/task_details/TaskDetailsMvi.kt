package com.scgts.sctrace.feature.landing.task_details

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.TaskStatus.NOT_STARTED

interface TaskDetailsMvi {
    sealed class Intent : MviIntent {
        //view intents
        object BackClicked : Intent()
        object ContinueClicked : Intent()

        //data intents
        data class TaskDetailsData(
            val task: Task,
            val tallies: Tallies,
            val productInfoList: List<AssetProductInformationCardUiModel>
        ) : Intent()

        data class TaskLocationsData(
            val fromLocation: Facility?,
            val toLocation: Facility?
        ) : Intent()

        data class LastUpdatedAt(val updatedAt: String?) : Intent()
        data class ShowNotSubmittedTraceEventsWarning(val show: Boolean) : Intent()
    }

    data class ViewState(
        override val loading: Boolean = true,
        override val error: Throwable? = null,
        val taskType: TaskType = TaskType.NO_TYPE,
        val orderType: OrderType = OrderType.NO_TYPE,
        val orderAndTaskType: String = "",
        val taskDescription: String = "",
        val taskStatus: TaskStatus = NOT_STARTED,
        val wellSection: String? = null,
        val fromLocation: Facility? = null,
        val toLocation: Facility? = null,
        val deliveryDate: String? = null,
        val percentCompletion: Float = 0f,
        val lastUpdatedAt: String? = null,
        val totalTally: String? = null,
        val totalConsumed: String? = null,
        val expectedTally: String? = null,
        val specialInstructions: String? = null,
        val assetProductDescription: List<AssetProductInformationCardUiModel> = emptyList(),
        val showNotSubmittedTraceEventsWarning: Boolean = false
    ) : MviViewState
}
