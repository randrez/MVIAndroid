package com.scgts.sctrace.task_summary.ui

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.task_summary.ui.TaskSummaryMvi.Intent.SessionTalliesAndJoints
import com.scgts.sctrace.task_summary.ui.TaskSummaryMvi.Intent.TalliesAndJoints

interface TaskSummaryMvi {
    sealed class Intent : MviIntent {
        //view intents
        object BackClicked : Intent()
        object SubmitClicked : Intent()
        object NavigateToTasksOnPostSubmit : Intent()
        object CaptureClicked : Intent()
        object CaptureRackTransferClicked : Intent()
        object ToggleSummaryExpanded : Intent()
        object SeeDetails : Intent()
        data class SelectAsset(val assetId: String) : Intent()
        data class DeselectAsset(val assetId: String) : Intent()
        data class DeleteCapturedAsset(val assetId: String) : Intent()
        data class EditCapturedAsset(val assetId: String) : Intent()
        data class AssetClicked(val assetId: String) : Intent()

        //data intents
        object Submitted : Intent()
        object OfflineSubmitted : Intent()
        data class TaskData(val task: Task) : Intent()
        data class SwipeToEditEnabled(val enabled: Boolean) : Intent()
        data class Assets(val assets: List<AssetCardUiModel>, val selectable: Boolean = false) :
            Intent()

        data class UnitTypeUpdate(val unitType: UnitType) : Intent()

        data class TalliesAndJoints(
            val total: Double,
            val totalConsumed: Double,
            val totalMakeUpLoss: Double,
            val totalRejected: Double,
            val totalJoints: Int,
            val consumedJoints: Int,
            val rejectedJoints: Int,
            val consumedRunningLength: Double,
            val runningLength: Double,
        ) : Intent()

        data class SessionTalliesAndJoints(
            val sessionTotal: Double,
            val sessionConsumed: Double,
            val sessionMakeUpLoss: Double,
            val sessionTotalJoints: Int,
            val sessionConsumedJoints: Int,
        ) : Intent()

        data class ExpandableSummaryData(val summaryListExpandable: List<ExpandableTextEntry>) :
            Intent()

        data class AssetsRackTransfer(val assetsRackTransfer: List<RackTransferModel>) : Intent()
    }

    data class ViewState(
        val task: Task? = null,
        val assets: List<AssetCardUiModel> = emptyList(),
        val selectableAssets: Boolean = false,
        val totalTalliesAndJoints: TalliesAndJoints = TalliesAndJoints(
            total = 0.0,
            totalConsumed = 0.0,
            totalMakeUpLoss = 0.0,
            totalRejected = 0.0,
            totalJoints = 0,
            consumedJoints = 0,
            rejectedJoints = 0,
            consumedRunningLength = 0.0,
            runningLength = 0.0
        ),
        val sessionTalliesAndJoints: SessionTalliesAndJoints = SessionTalliesAndJoints(
            sessionTotal = 0.0,
            sessionConsumed = 0.0,
            sessionMakeUpLoss = 0.0,
            sessionTotalJoints = 0,
            sessionConsumedJoints = 0
        ),
        val submitted: Boolean = false,
        val unitType: UnitType = UnitType.FEET,
        val isOfflineSubmitted: Boolean = false,
        val summaryExpanded: Boolean = false,
        val swipeToEditEnabled: Boolean = true,
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState
}
