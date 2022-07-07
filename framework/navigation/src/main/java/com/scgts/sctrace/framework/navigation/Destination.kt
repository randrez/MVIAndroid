package com.scgts.sctrace.framework.navigation

import android.net.Uri
import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionFragmentDirections
import com.scgts.sctrace.assets.confirmation.AssetConfirmationFragmentDirections
import com.scgts.sctrace.assets.consumption.ConsumptionRejectFragmentDirections
import com.scgts.sctrace.assets.detail.AssetDetailFragmentDirections
import com.scgts.sctrace.assets.tags.discard.TagDiscardConfirmationFragmentDirections
import com.scgts.sctrace.base.model.TypeWarnings
import com.scgts.sctrace.base.model.TypeWarnings.NO_WARNING
import com.scgts.sctrace.capture.CaptureFragmentDirections
import com.scgts.sctrace.capture.tablet.TabletCaptureFragmentDirections
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsFragmentDirections
import com.scgts.sctrace.feature.landing.unsynced_submissions.UnsyncedSubmissionsFragmentDirections
import com.scgts.sctrace.feature.settings.ui.SettingsFragmentDirections
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionFragmentDirections
import com.scgts.sctrace.rack_transfer.edittransfer.EditRackTransferFragmentDirections
import com.scgts.sctrace.rack_transfer.rackdetails.RackDetailsFragmentDirections
import com.scgts.sctrace.rack_transfer.tasksummary.RackTransferTaskSummaryFragmentDirections
import com.scgts.sctrace.rack_transfer.transferselection.RackTransferSelectionFragmentDirections
import com.scgts.sctrace.see_details.ui.SeeDetailsFragmentDirections
import com.scgts.sctrace.task_summary.ui.TaskSummaryFragmentDirections
import com.scgts.sctrace.base.model.AdHocAction as AdHocActionModel

sealed class NavDestination(val id: Int) {
    object Login : NavDestination(R.id.loginFragment)
    object Tasks : NavDestination(R.id.tasksFragment)
    object TabletTasks : NavDestination(R.id.tasksAndTaskDetailsScreenFragment)
    object EditAssetLength : NavDestination(R.id.editAssetLengthFragment)
    object GiveFeedback : NavDestination(R.id.giveFeedbackFragment)
    object ConflictHandler : NavDestination(R.id.conflictHandlerFragment)
    object FilterAndSort : NavDestination(R.id.filterAndSortFragment)

    sealed class NavDestinationArgs(val navDirections: NavDirections) : NavDestination(0) {

        class AdHocAction(val action: String) :
            NavDestinationArgs(AdHocActionFragmentDirections.startAdHocAction(action = action))

        class AdHocActionDialog(action: String) :
            NavDestinationArgs(AdHocActionFragmentDirections.startAdHocActionDialog(action = action))

        class TaskDetails(taskId: String, orderId: String) : NavDestinationArgs(
            TaskDetailsFragmentDirections.startTaskDetails(
                taskId = taskId,
                orderId = orderId
            )
        )

        class RackDetails(
            taskId: String,
            rackId: String,
            millWorkNum: String,
            productDescription: String,
        ) : NavDestinationArgs(
            RackDetailsFragmentDirections.startRackDetails(
                taskId = taskId,
                rackId = rackId,
                millWorkNum = millWorkNum,
                productDescription = productDescription
            )
        )

        class SeeDetails(taskId: String, orderId: String) : NavDestinationArgs(
            SeeDetailsFragmentDirections.startSeeDetails(
                taskId = taskId,
                orderId = orderId
            )
        )

        class ShowAssetDetails(assetId: String, taskId: String?) : NavDestinationArgs(
            AssetDetailFragmentDirections.startAssetDetail(
                assetId = assetId,
                taskId = taskId
            )
        )

        class TaskSummary(taskId: String, orderId: String) : NavDestinationArgs(
            TaskSummaryFragmentDirections.startTaskSummary(
                taskId = taskId,
                orderId = orderId
            )
        )

        class RackTransferTaskSummary(taskId: String, orderId: String) : NavDestinationArgs(
            RackTransferTaskSummaryFragmentDirections.startRackTransferTaskSummary(
                taskId = taskId,
                orderId = orderId
            )
        )

        data class RackTransferSelection(val projectId: String, val taskId: String) :
            NavDestinationArgs(
                RackTransferSelectionFragmentDirections.startRackTransferSelection(
                    taskId = taskId,
                    projectId = projectId
                )
            )

        data class EditRackTransfer(
            val taskId: String,
            val rackId: String,
            val millWorkNum: String,
            val productDescription: String,
        ) : NavDestinationArgs(
            EditRackTransferFragmentDirections.startEditRackTransfer(
                taskId = taskId,
                rackId = rackId,
                millWorkNum = millWorkNum,
                productDescription = productDescription
            )
        )

        data class Capture(val projectId: String, val taskId: String?) :
            NavDestinationArgs(CaptureFragmentDirections.startCapture(projectId, taskId))

        data class TabletCapture(val projectId: String, val taskId: String?) :
            NavDestinationArgs(
                TabletCaptureFragmentDirections.startTabletCapture(
                    projectId,
                    taskId
                )
            )

        data class DeleteAsset(val assetId: String, val taskId: String) :
            NavDestinationArgs(AssetConfirmationFragmentDirections.discardAsset(assetId, taskId))

        data class AssetDetails(val data: AssetDataForNavigation) : NavDestinationArgs(
            AssetConfirmationFragmentDirections.assetDetails(
                assetId = data.assetId,
                taskId = data.taskId,
                new = data.newAsset,
                scannedTag = data.scannedTag,
                unexpectedWarning = data.unexpectedWarning,
                originPage = data.originPage
            )
        )

        //only use for navigation
        data class AssetDataForNavigation(
            val assetId: String,
            val taskId: String? = null,
            val newAsset: Boolean = true,
            val scannedTag: String? = null,
            val unexpectedWarning: TypeWarnings = NO_WARNING,
            @IdRes val originPage: Int,
        )

        data class DeleteTag(val assetId: String, val tag: String) :
            NavDestinationArgs(TagDiscardConfirmationFragmentDirections.discardTag(assetId, tag))

        data class SettingsSelection(val settingsType: String) : NavDestinationArgs(
            SettingsSelectionFragmentDirections.startSettingsSelection(settingsType = settingsType)
        )

        data class UnsyncedSubmissions(val originName: String) : NavDestinationArgs(
            UnsyncedSubmissionsFragmentDirections.startUnsyncSubmissions(originName)
        )

        data class Settings(val originName: String) :
            NavDestinationArgs(SettingsFragmentDirections.startSettings(originName))

        data class RejectAsset(
            val assetId: String,
            val taskId: String,
            val quickReject: Boolean,
            val statusChange: ConsumptionSwitchType? = null,
        ) : NavDestinationArgs(
            ConsumptionRejectFragmentDirections.rejectAsset(
                taskId = taskId,
                assetId = assetId,
                consumptionStatusChange = statusChange?.name,
                quickReject = quickReject
            )
        ) {
            enum class ConsumptionSwitchType {
                ConsumedToRejected, RejectedToConsumed
            }
        }
    }
}

sealed class WebDestination(val uri: Uri) {
    object ForgotPassword :
        WebDestination("https://scgts.oktapreview.com/signin/forgot-password".toUri())
}

internal fun String.toUri(): Uri = Uri.parse(this)
