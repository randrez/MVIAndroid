package com.scgts.sctrace.capture.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scgts.sctrace.base.model.DialogType.*
import com.scgts.sctrace.base.model.DialogType.Companion.ASSET_FROM_WRONG_PROJECT_TAG
import com.scgts.sctrace.base.model.DialogType.Companion.ASSET_NOT_FOUND_TAG
import com.scgts.sctrace.base.util.removeProjectString
import com.scgts.sctrace.base.util.toFormattedString
import com.scgts.sctrace.capture.R
import com.scgts.sctrace.capture.composable.camera.CaptureCameraScreen
import com.scgts.sctrace.capture.composable.camera.CaptureContentScreen
import com.scgts.sctrace.capture.databinding.FragmentCaptureCameraBinding
import com.scgts.sctrace.capture.scan.CaptureCameraMvi.Intent
import com.scgts.sctrace.capture.scan.CaptureCameraMvi.Intent.*
import com.scgts.sctrace.framework.view.BaseCaptureCameraFragment
import dialogs.AssetFromWrongProjectDialog
import dialogs.AssetNotFoundDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import theme.SCGTSTheme
import util.CaptureMode.Consumption
import util.ScanState
import util.sendErrorToDtrace
import java.util.*
import kotlin.concurrent.schedule

internal open class CaptureCameraFragment : BaseCaptureCameraFragment<CaptureCameraViewModel>() {

    protected val intents = PublishSubject.create<Intent>()
    override val viewModel: CaptureCameraViewModel by viewModel()

    protected val _viewState = MutableLiveData<CaptureCameraMvi.ViewState>()
    protected val viewState: LiveData<CaptureCameraMvi.ViewState>
        get() = _viewState

    private var _binding: FragmentCaptureCameraBinding? = null
    private val binding get() = _binding!!

    protected var autoScanNotificationTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render) { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
            .autoDisposeOnDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCaptureCameraBinding.inflate(inflater, container, false)
        dataCaptureView = DataCaptureView.newInstance(requireContext(), null)
        binding.cameraPreview.addView(dataCaptureView)

        binding.captureCameraConsume.setOnClickListener { intents.onNext(ConsumeClicked) }
        binding.captureCameraReject.setOnClickListener { intents.onNext(RejectClicked) }
        binding.captureBottomSheetComposeView.setContent {
            SCGTSTheme {
                CaptureContentScreen(
                    viewState = viewState,
                    isTablet = false,
                    onAddTagSaveClicked = { tag -> intents.onNext(AddTag(tag)) },
                    onAddTagDeleteClicked = { tag -> intents.onNext(DeleteTag(tag)) },
                    onAddTagDoneClicked = { intents.onNext(DoneClicked) },
                    onAssetClicked = { assetId -> intents.onNext(AssetClicked(assetId)) },
                    onEditClicked = { assetId -> intents.onNext(EditAssetClick(assetId)) },
                    onDeleteClicked = { assetId -> intents.onNext(DeleteCapturedAsset(assetId)) },
                )
            }
        }
        binding.cameraComposeView.setContent {
            SCGTSTheme {
                CaptureCameraScreen(
                    viewState = viewState,
                    onCaptureButtonClicked = { intents.onNext(CaptureButtonClicked) },
                    onAutoScanToggle = { intents.onNext(ToggleAutoScan) }
                )
            }
        }
        return binding.root
    }

    override fun onBarcodeParsed(barcode: String) {
        intents.onNext(BarCodeScanned(barcode))
    }

    override fun onCameraPermissionGranted() {
        intents.onNext(OnCameraPermissionGranted)
    }

    open fun render(viewState: CaptureCameraMvi.ViewState) {
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

            val captureMode = viewState.captureMode
            val quickRejectMode = captureMode is Consumption.Reject && captureMode.quickReject

            toastAssetAdded.root.isVisible = viewState.showAssetAddedToast
            captureCameraBottomSheet.isVisible = viewState.showAssetSummary

            consumptionViewsVisibility.isVisible =
                !quickRejectMode && captureMode is Consumption
            captureCameraConsume.isActivated = captureMode is Consumption.Consume
            captureCameraReject.isActivated = captureMode is Consumption.Reject

            captureCameraQuickReject.isVisible = quickRejectMode

            val scale = resources.displayMetrics.density
            val paddingBottom = if (viewState.showAssetSummary) (300 * scale + 0.5f).toInt() else 0
            cameraComposeView.setPadding(0, 0, 0, paddingBottom)

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

    protected fun setNextAutoScanNotificationMessage(@StringRes text: Int?) {
        autoScanNotificationTimer?.cancel()
        autoScanNotificationTimer = Timer()
        autoScanNotificationTimer?.schedule(2000) {
            intents.onNext(SetAutoScanNotificationMessage(text))
        }
    }

    override fun onPause() {
        super.onPause()
        intents.onNext(OnPause)
    }

    override fun onExit() = intents.onNext(ExitFlow)

    override fun onDestroy() {
        _binding = null
        autoScanNotificationTimer?.cancel()
        autoScanNotificationTimer?.purge()
        autoScanNotificationTimer = null
        super.onDestroy()
    }
}
