package com.scgts.sctrace.capture.tag_conflict

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dialogs.BaseDialogFragment
import com.scgts.sctrace.capture.databinding.FragmentConflictHandlerBinding
import com.scgts.sctrace.capture.tag_conflict.ConflictHandlerMvi.Intent
import com.scgts.sctrace.capture.tag_conflict.ConflictHandlerMvi.Intent.*
import com.scgts.sctrace.capture.tag_conflict.ConflictHandlerMvi.ViewState
import com.scgts.sctrace.ui.components.AssetClickListener
import com.scgts.sctrace.ui.components.AssetsEpoxyController
import org.koin.android.viewmodel.ext.android.viewModel

class ConflictHandlerFragment : BaseDialogFragment<Intent, ViewState, ConflictHandlerViewModel>() {

    override val viewModel: ConflictHandlerViewModel by viewModel()
    private var _binding: FragmentConflictHandlerBinding? = null
    private val binding get() = _binding!!

    private val controller: AssetsEpoxyController by lazy {
        AssetsEpoxyController(
            showEmptyState = false,
            conflictHandling = true,
            clickListener = object : AssetClickListener {
                override fun assetClick(assetId: String) {
                    intents.onNext(AssetClicked(assetId))
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentConflictHandlerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false

        binding.conflictsAssets.setController(controller)

        binding.conflictsCancel.setOnClickListener { intents.onNext(CancelClicked) }

        binding.toggleButton.setOnClickListener { intents.onNext(ExpandClicked) }
    }

    override fun render(viewState: ViewState) {
        controller.setData(viewState.assets)

        changeViewHeight(viewState.expanded)
    }
}
