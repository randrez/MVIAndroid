package com.scgts.sctrace.capture

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.base.model.CaptureMethod.Camera
import com.scgts.sctrace.base.model.CaptureMethod.Manual
import com.scgts.sctrace.base.model.TaskType.AD_HOC_REJECT_SCAN
import com.scgts.sctrace.capture.CaptureMvi.Intent
import com.scgts.sctrace.capture.CaptureMvi.Intent.*
import com.scgts.sctrace.capture.CaptureMvi.ViewState
import com.scgts.sctrace.capture.databinding.FragmentCaptureBinding
import com.scgts.sctrace.capture.di.CaptureFlowModules
import com.scgts.sctrace.framework.view.BaseFragment
import com.scgts.sctrace.ui.components.IconTextRowModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import util.sendErrorToDtrace

class CaptureFragment : BaseFragment<CaptureViewModel>() {

    private var _binding: FragmentCaptureBinding? = null
    private val binding get() = _binding!!

    private val intents = PublishSubject.create<Intent>()

    private val args: CaptureFragmentArgs by navArgs()

    override val viewModel: CaptureViewModel by viewModel()

    private val koinModules by lazy {
        val quickReject = args.taskId == AD_HOC_REJECT_SCAN.id
        CaptureFlowModules(args.projectId, args.taskId, quickReject).modules
    }

    private val controller by lazy {
        CaptureEpoxyController(
            object : IconTextRowModel.IconTextRowClickListener {
                override fun onClick(label: String) {
                    intents.onNext(
                        when (CaptureMethod.fromName(label)) {
                            is Camera -> CaptureMethodSelected(Camera)
                            is Manual -> CaptureMethodSelected(Manual)
                            else -> NoOp
                        }
                    )
                    bottomSheet().state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        )
    }

    private fun bottomSheet() = BottomSheetBehavior.from(binding.captureBottomSheet)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadKoinModules(koinModules)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCaptureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render) { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
            .autoDisposeOnDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.captureActionsList.run {
            layoutManager = LinearLayoutManager(context)
            adapter = controller.adapter
        }
        initBottomSheet(intents)
        initToolbar(
            intents,
            view.findViewById(R.id.toolbar_close),
            view.findViewById(R.id.capture_mode)
        )
    }

    private fun render(viewState: ViewState) {
        controller.setData(viewState.captureMethods)
        view?.findViewById<TextView>(R.id.toolbar_title)?.setText(viewState.screenTitle)
        view?.findViewById<ImageButton>(R.id.toolbar_close)?.isVisible =
            viewState.screenTitle == R.string.capture_screen_title

        if (viewState.showCaptureMethodOptions) {
            bottomSheet().state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheet().state = BottomSheetBehavior.STATE_COLLAPSED
        }

        when (viewState.selectedCaptureMethod) {
            Camera -> {
                binding.captureCameraFragment.isVisible = true
                binding.captureManualFragment.isVisible = false
                view?.findViewById<ImageView>(R.id.capture_mode)
                    ?.setImageResource(R.drawable.ic_scgts_toolbar_camera)
            }
            Manual -> {
                binding.captureCameraFragment.isVisible = false
                binding.captureManualFragment.isVisible = true
                view?.findViewById<ImageView>(R.id.capture_mode)
                    ?.setImageResource(R.drawable.ic_scgts_toolbar_manual)
            }
            else -> {
            }
        }
    }

    private fun initBottomSheet(intents: PublishSubject<Intent>) {
        bottomSheet().apply {
            binding.captureBackdrop.setOnClickListener {
                if (state == BottomSheetBehavior.STATE_EXPANDED) {
                    intents.onNext(CaptureMethodButtonClicked)
                }
            }

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    binding.captureBackdrop.isVisible = BottomSheetBehavior.STATE_COLLAPSED != state
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.captureBackdrop
                        .animate()
                        .alpha(0 + slideOffset)
                        .setDuration(0)
                        .start()
                }
            })
        }
    }

    private fun initToolbar(
        intents: PublishSubject<Intent>,
        closeButton: ImageView,
        captureModeButton: ImageView,
    ) {
        captureModeButton.setOnClickListener { intents.onNext(CaptureMethodButtonClicked) }
        closeButton.setOnClickListener { intents.onNext(Exit) }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
        if (koinModules.isNotEmpty()) {
            unloadKoinModules(koinModules)
        }
    }
}
