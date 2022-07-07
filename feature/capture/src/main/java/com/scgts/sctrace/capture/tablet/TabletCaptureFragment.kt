package com.scgts.sctrace.capture.tablet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.base.model.TaskType.AD_HOC_REJECT_SCAN
import com.scgts.sctrace.capture.CaptureEpoxyController
import com.scgts.sctrace.capture.CaptureMvi
import com.scgts.sctrace.capture.CaptureMvi.Intent.CaptureMethodSelected
import com.scgts.sctrace.capture.CaptureViewModel
import com.scgts.sctrace.capture.R
import com.scgts.sctrace.capture.composable.camera.CaptureContentScreen
import com.scgts.sctrace.capture.databinding.FragmentTabletCaptureBinding
import com.scgts.sctrace.capture.di.CaptureFlowModules
import com.scgts.sctrace.capture.scan.CaptureCameraMvi
import com.scgts.sctrace.capture.scan.CaptureCameraMvi.Intent.*
import com.scgts.sctrace.capture.scan.CaptureCameraViewModel
import com.scgts.sctrace.feature.tablet.BaseTabletToolBarScreen
import com.scgts.sctrace.ui.components.IconTextRowModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import theme.SCGTSTheme
import util.dpToPixels
import util.sendErrorToDtrace

class TabletCaptureFragment : BaseTabletToolBarScreen<CaptureViewModel>() {
    override val viewModel: CaptureViewModel by viewModel()
    private val captureIntents = PublishSubject.create<CaptureMvi.Intent>()
    private val args: TabletCaptureFragmentArgs by navArgs()
    private val captureCameraViewModel: CaptureCameraViewModel by viewModel()
    private val captureCameraIntents = PublishSubject.create<CaptureCameraMvi.Intent>()
    private val _captureCameraViewState = MutableLiveData<CaptureCameraMvi.ViewState>()
    private val captureCameraViewState: LiveData<CaptureCameraMvi.ViewState>
        get() = _captureCameraViewState
    private val koinModules by lazy {
        val quickReject = args.taskId == AD_HOC_REJECT_SCAN.id
        CaptureFlowModules(args.projectId, args.taskId, quickReject).modules
    }
    private lateinit var popupWindow: PopupWindow
    private lateinit var binding: FragmentTabletCaptureBinding

    private val captureModeController by lazy {
        CaptureEpoxyController(
            object : IconTextRowModel.IconTextRowClickListener {
                override fun onClick(label: String) {
                    popupWindow.dismiss()
                    captureIntents.onNext(
                        when (CaptureMethod.fromName(label)) {
                            is CaptureMethod.Camera -> CaptureMethodSelected(CaptureMethod.Camera)
                            is CaptureMethod.Manual -> CaptureMethodSelected(CaptureMethod.Manual)
                            else -> CaptureMvi.Intent.NoOp
                        }
                    )
                }
            }
        )
    }

    override fun getScreenTitle(): String = resources.getString(R.string.capture_screen_title)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadKoinModules(koinModules)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(captureIntents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::captureRender) { error: Throwable -> sendError(error) }
            .autoDisposeOnDestroy()
        captureCameraViewModel.bind(captureCameraIntents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::captureCameraRender) { error: Throwable -> sendError(error) }
            .autoDisposeOnDestroy()
    }

    private fun sendError(throwable: Throwable) {
        throwable.sendErrorToDtrace(this.javaClass.name)
    }

    private fun captureRender(viewState: CaptureMvi.ViewState) {
        captureModeController.setData(viewState.captureMethods)
        when (viewState.selectedCaptureMethod) {
            CaptureMethod.Camera -> {
                binding.captureCameraFragment.isVisible = true
                binding.captureManualFragment.isVisible = false
                binding.captureModeSelector.setImageResource(R.drawable.ic_buttons_fab_camera)
            }
            CaptureMethod.Manual -> {
                binding.captureCameraFragment.isVisible = false
                binding.captureManualFragment.isVisible = true
                binding.captureModeSelector.setImageResource(R.drawable.ic_buttons_fab_camera) // TODO: change this to manual mode icon
            }
            else -> {
            }
        }
    }

    private fun captureCameraRender(viewState: CaptureCameraMvi.ViewState) {
        _captureCameraViewState.postValue(viewState)

        val params = binding.guideline.layoutParams as ConstraintLayout.LayoutParams
        params.guidePercent = if (viewState.showAssetSummary) .45f else 1f
        binding.guideline.layoutParams = params

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentTabletCaptureBinding.inflate(inflater, container, false).let {
        binding = it
        toolbar = binding.toolbar.root
        hideIcons()
        setBackArrow()
        binding.captureSidePanel.setContent {
            SCGTSTheme {
                CaptureContentScreen(
                    viewState = captureCameraViewState,
                    isTablet = true,
                    onAddTagSaveClicked = { tag -> captureCameraIntents.onNext(AddTag(tag)) },
                    onAddTagDeleteClicked = { tag -> captureCameraIntents.onNext(DeleteTag(tag)) },
                    onAddTagDoneClicked = { captureCameraIntents.onNext(DoneClicked) },
                    onAssetClicked = { assetId ->
                        captureCameraIntents.onNext(AssetClicked(assetId))
                    },
                    onEditClicked = { assetId ->
                        captureCameraIntents.onNext(EditAssetClick(assetId))
                    },
                    onDeleteClicked = { assetId ->
                        captureCameraIntents.onNext(DeleteCapturedAsset(assetId))
                    }
                )
            }
        }
        it.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPopupWindow()
        with(binding.captureModeSelector) {
            setOnClickListener {
                popupWindow.also {
                    it.showAsDropDown(
                        this,
                        dpToPixels(POPUP_X_OFFSET),
                        dpToPixels(POPUP_Y_OFFSET)
                    )
                    it.update()
                }
            }
        }
    }

    private fun setupPopupWindow() {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_window, binding.root, false)
        popupView.findViewById<RecyclerView>(R.id.popup_list).also {
            it.layoutManager = LinearLayoutManager(it.context)
            it.adapter = captureModeController.adapter
        }
        popupView.findViewById<TextView>(R.id.popup_title).setText(R.string.scan_mode_screen_title)
        val width = dpToPixels(POPUP_WIDTH)
        val height = dpToPixels(POPUP_HEIGHT)
        val focusable = true
        popupWindow = PopupWindow(popupView, width, height, focusable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (koinModules.isNotEmpty()) {
            unloadKoinModules(koinModules)
        }
    }

    companion object {
        const val POPUP_X_OFFSET = 70
        const val POPUP_Y_OFFSET = 130
        const val POPUP_HEIGHT = 230
        const val POPUP_WIDTH = 230
    }
}
