package com.scgts.sctrace.capture.tablet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scgts.sctrace.base.model.DialogType.*
import com.scgts.sctrace.base.model.DialogType.Companion.ASSET_FROM_WRONG_PROJECT_TAG
import com.scgts.sctrace.base.model.DialogType.Companion.ASSET_NOT_FOUND_TAG
import com.scgts.sctrace.base.util.removeProjectString
import com.scgts.sctrace.base.util.toFormattedString
import com.scgts.sctrace.capture.R
import com.scgts.sctrace.capture.composable.camera.TabletCaptureCameraScreen
import com.scgts.sctrace.capture.databinding.FragmentTabletCaptureCameraBinding
import com.scgts.sctrace.capture.scan.CaptureCameraFragment
import com.scgts.sctrace.capture.scan.CaptureCameraMvi
import com.scgts.sctrace.capture.scan.CaptureCameraMvi.Intent
import com.scgts.sctrace.capture.scan.CaptureCameraMvi.Intent.DismissDialog
import dialogs.AssetFromWrongProjectDialog
import dialogs.AssetNotFoundDialog
import theme.SCGTSTheme
import util.CaptureMode
import util.ScanState

internal class TabletCaptureCameraFragment : CaptureCameraFragment() {
    private lateinit var binding: FragmentTabletCaptureCameraBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentTabletCaptureCameraBinding.inflate(inflater, container, false).let {
        binding = it
        dataCaptureView = DataCaptureView.newInstance(requireContext(), null)
        binding.cameraPreview.addView(dataCaptureView)
        binding.captureCameraConsume.setOnClickListener {
            intents.onNext(Intent.ConsumeClicked)
        }
        binding.captureCameraReject.setOnClickListener {
            intents.onNext(Intent.RejectClicked)
        }
        binding.tabletCameraComposeView.setContent {
            SCGTSTheme {
                TabletCaptureCameraScreen(
                    viewState = viewState,
                    onCaptureButtonClicked = { intents.onNext(Intent.CaptureButtonClicked) },
                    onAutoScanToggle = { intents.onNext(Intent.ToggleAutoScan) }
                )
            }
        }
        it.root
    }

    override fun render(viewState: CaptureCameraMvi.ViewState) {
        _viewState.postValue(viewState)

        if (viewState.autoScanNotificationMessage != null) {
            setNextAutoScanNotificationMessage(null)
        }

        when (viewState.scanState) {
            ScanState.STANDBY -> resumeFrameSource()
            ScanState.SCANNING -> enableBarcodeCapture()
            ScanState.PAUSED -> pauseFrameSource()
            ScanState.OFF -> turnFrameSourceOff()
        }
        with(binding) {
            scanIssueMessage.isVisible = viewState.dialogType is ConsumptionDuplicate

            toastAssetAdded.root.isVisible = viewState.showAssetAddedToast
            val captureMode = viewState.captureMode
            val quickRejectMode =
                captureMode is CaptureMode.Consumption.Reject && captureMode.quickReject
            consumptionViewsVisibility.isVisible =
                captureMode is CaptureMode.Consumption && !quickRejectMode
            captureCameraConsume.isActivated = captureMode is CaptureMode.Consumption.Consume
            captureCameraReject.isActivated = captureMode is CaptureMode.Consumption.Reject

            captureCameraQuickReject.isVisible = quickRejectMode

            when (viewState.dialogType) {
                is AssetNotFound ->
                    if (childFragmentManager.findFragmentByTag(ASSET_NOT_FOUND_TAG) == null) {
                        AssetNotFoundDialog(
                            message = getString(R.string.asset_not_found_manual),
                            okayClickListener = { intents.onNext(DismissDialog) }
                        ).show(childFragmentManager, ASSET_NOT_FOUND_TAG)
                    }
                is AssetFromWrongProject ->
                    if (childFragmentManager.findFragmentByTag(ASSET_FROM_WRONG_PROJECT_TAG) == null) {
                        val descriptionFormat =
                            getString(R.string.asset_from_wrong_project_description)
                        val descriptionMessage = String.format(
                            descriptionFormat,
                            viewState.dialogType.otherProjectNames.map { it.removeProjectString() }
                                .toFormattedString(),
                            viewState.dialogType.currentProjectName.removeProjectString()
                        )
                        AssetFromWrongProjectDialog(
                            message = descriptionMessage,
                            dismissClickListener = { intents.onNext(DismissDialog) }
                        ).show(childFragmentManager, ASSET_FROM_WRONG_PROJECT_TAG)
                    }
                is ConsumptionDuplicate -> scanIssueDescription.text = getString(
                    if (viewState.dialogType.consumed) R.string.already_consumed_description
                    else R.string.already_rejected_description
                )
                else -> {
                }
            }
        }
    }
}