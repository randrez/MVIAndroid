package com.scgts.sctrace.capture.scan

import androidx.annotation.StringRes
import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.base.model.DialogType
import com.scgts.sctrace.base.model.TextEntry
import util.CaptureMode
import util.ScanState

interface CaptureCameraMvi {
    sealed class Intent : MviIntent {
        // view intents
        data class BarCodeScanned(
            val tag: String,
            val selectedConflictAssetId: String? = null,
        ) : Intent()

        object DismissDialog : Intent()
        object ExitFlow : Intent()
        data class EditAssetClick(val assetId: String) : Intent()
        data class DeleteCapturedAsset(val assetId: String) : Intent()
        object ConsumeClicked : Intent()
        object RejectClicked : Intent()
        data class AssetClicked(val assetId: String) : Intent()
        data class AddTag(val tag: String) : Intent()
        data class DeleteTag(val tag: String) : Intent()
        object DoneClicked : Intent()
        object CaptureButtonClicked : Intent()
        object ToggleAutoScan : Intent()
        data class SetAutoScanNotificationMessage(@StringRes val message: Int?) : Intent()
        object OnCameraPermissionGranted : Intent()
        object OnPause : Intent()

        // data intents
        data class SetScanState(val scanState: ScanState) : Intent()
        data class SetAutoScan(val autoScanIsEnabled: Boolean) : Intent()
        data class CapturedAssets(val assets: List<AssetCardUiModel>) : Intent()
        data class SetCaptureMode(val captureMode: CaptureMode) : Intent()
        data class SwipeToEditEnabled(val enabled: Boolean) : Intent()
        data class SetShowAssetSummary(val show: Boolean) : Intent()
        data class UpdateAssetAddedToast(val show: Boolean) : Intent()
        data class SetDialog(val dialogType: DialogType?) : Intent()
        data class UpdateTagList(val tags: List<String>) : Intent()
        data class ScannedSummaryData(val summaryList: List<TextEntry>) : Intent()
        object NoOp : Intent()
    }

    data class ViewState(
        val autoScanEnabled: Boolean = false,
        @StringRes val autoScanNotificationMessage: Int? = null,
        val scanState: ScanState = ScanState.STANDBY,
        val dialogType: DialogType? = null,
        val assets: List<AssetCardUiModel> = emptyList(),
        val summaryList: List<TextEntry> = emptyList(),
        val captureMode: CaptureMode = CaptureMode.Assets,
        val swipeToEditEnabled: Boolean = true,
        val showAssetSummary: Boolean = true,
        val showAssetAddedToast: Boolean = false,
        val tags: List<String> = emptyList(),
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState
}