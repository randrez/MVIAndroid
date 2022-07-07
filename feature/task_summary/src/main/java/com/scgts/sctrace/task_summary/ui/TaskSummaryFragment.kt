package com.scgts.sctrace.task_summary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.stickyheader.StickyHeaderLinearLayoutManager
import com.google.android.material.button.MaterialButton.ICON_GRAVITY_TEXT_START
import com.scgts.sctrace.feature.tablet.BaseTabletToolBarScreen
import com.scgts.sctrace.task_summary.R
import com.scgts.sctrace.task_summary.databinding.FragmentTaskSummaryBinding
import com.scgts.sctrace.task_summary.ui.TaskSummaryMvi.Intent
import com.scgts.sctrace.task_summary.ui.TaskSummaryMvi.Intent.*
import com.scgts.sctrace.ui.components.AssetClickListener
import com.scgts.sctrace.ui.components.AssetsEpoxyController
import com.scgts.sctrace.ui.components.SelectableAssetsEpoxyController
import com.scgts.sctrace.ui.components.SwipeableRow
import com.scgts.sctrace.ui.components.epoxyHelper.EpoxySwipe
import com.scgts.sctrace.ui.components.epoxyHelper.attachEpoxySwipe
import dialogs.OfflineSyncDialog
import dialogs.WarningDialogFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import util.isTablet
import util.sendErrorToDtrace

class TaskSummaryFragment : BaseTabletToolBarScreen<TaskSummaryViewModel>() {

    private val intents = PublishSubject.create<Intent>()

    private val args: TaskSummaryFragmentArgs by navArgs()

    override val viewModel: TaskSummaryViewModel by viewModel {
        parametersOf(args.taskId, args.orderId, isTablet())
    }

    private var _binding: FragmentTaskSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var epoxySwipe: EpoxySwipe<SwipeableRow>
    private lateinit var epoxySwipeSelect: EpoxySwipe<SwipeableRow>

    private val expandableTaskSummaryController by lazy {
        ExpandableTaskSummaryEpoxyController(
            toggleExpand = { intents.onNext(ToggleSummaryExpanded) },
            seeDetails = { intents.onNext(SeeDetails) },
            isTablet = isTablet()
        )
    }

    private val assetsController by lazy {
        AssetsEpoxyController(
            isTablet = isTablet(),
            clickListener = object : AssetClickListener {
                override fun assetClick(assetId: String) {
                    intents.onNext(AssetClicked(assetId))
                }
            }
        )
    }

    private val selectableAssetsController by lazy {
        SelectableAssetsEpoxyController(
            selectAsset = { intents.onNext(SelectAsset(it)) },
            deselectAsset = { intents.onNext(DeselectAsset(it)) },
            isTablet = isTablet(),
            clickListener = object : AssetClickListener {
                override fun assetClick(assetId: String) {
                    intents.onNext(AssetClicked(assetId))
                }
            }
        )
    }

    override fun getScreenTitle(): String = resources.getString(R.string.capture_screen_title)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        if (_binding != null) return binding.root
        _binding = FragmentTaskSummaryBinding.inflate(inflater, container, false)

        binding.assetsList.run {
            layoutManager = StickyHeaderLinearLayoutManager(context)
            adapter = assetsController.adapter
            epoxySwipe = attachEpoxySwipe(
                resources = resources,
                epoxyController = assetsController,
                recyclerView = this,
                leftSwipeClick = { intents.onNext(EditCapturedAsset(it.asset.id)) },
                rightSwipeClick = { model ->
                    DiscardConfirmationFragment {
                        intents.onNext(DeleteCapturedAsset(model.asset.id))
                    }.show(requireFragmentManager(), "DiscardConfirmationFragment")
                }
            )
        }

        binding.selectableAssetsList.run {
            layoutManager = StickyHeaderLinearLayoutManager(context)
            adapter = selectableAssetsController.adapter
            epoxySwipeSelect = attachEpoxySwipe(
                resources = resources,
                epoxyController = selectableAssetsController,
                recyclerView = this,
                leftSwipeClick = { intents.onNext(EditCapturedAsset(it.asset.id)) },
                rightSwipeClick = { model ->
                    DiscardConfirmationFragment {
                        intents.onNext(DeleteCapturedAsset(model.asset.id))
                    }.show(requireFragmentManager(), "DiscardConfirmationFragment")
                }
            )
        }

        binding.expandableTaskSummary.run {
            layoutManager = LinearLayoutManager(context)
            adapter = expandableTaskSummaryController.adapter
        }
        if (isTablet()) {
            toolbar = binding.taskToolbar?.root
            setBackArrow()
            hideIcons()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.summaryToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        with(binding) {
            summarySubmitButton.setOnClickListener {
                if (assetsController.containsUnexpectedProduct()) {
                    WarningDialogFragment(
                        warningChallengeRes = R.string.unexpected_product_challenge,
                        warningExplanationRes = R.string.unexpected_product_explanation,
                        showChecbox = false
                    )
                    {
                        intents.onNext(SubmitClicked)
                        progressHolder.isVisible = true
                    }.show(requireFragmentManager(), "WarningDialogFragment")
                } else {
                    intents.onNext(SubmitClicked)
                    progressHolder.isVisible = true
                }
            }
            summaryFab.setOnClickListener { intents.onNext(CaptureClicked) }
        }
    }

    private fun render(viewState: TaskSummaryMvi.ViewState) {
        if (!viewState.swipeToEditEnabled) {
            epoxySwipe.disableLeftSwipe()
            epoxySwipeSelect.disableLeftSwipe()
        }

        with(binding) {
            expandableTaskSummaryController.setData(viewState)
            viewState.task?.let { task ->
                summaryType.text = task.orderAndTask()
                summaryJobNumber.text = task.descriptionOrLocation()
            }

            summarySubmitButton.isEnabled = viewState.assets.find { it.checked == true } != null

            if (viewState.selectableAssets) {
                selectableAssetsController.setData(viewState.assets)
            } else {
                assetsController.setData(viewState.assets)
            }

            assetsList.isVisible = !viewState.selectableAssets
            selectableAssetsList.isVisible = viewState.selectableAssets

            if (viewState.submitted) {
                summarySubmitButton.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_icon_check)
                summarySubmitButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.green)
                summarySubmitButton.text = getString(R.string.submitted)
                summarySubmitButton.setTextColor(
                    ContextCompat.getColorStateList(requireContext(), R.color.n_900)
                )
                summarySubmitButton.iconGravity = ICON_GRAVITY_TEXT_START

                progressHolder.isVisible = false
                if (!viewState.isOfflineSubmitted) intents.onNext(NavigateToTasksOnPostSubmit)
            }
            if (viewState.isOfflineSubmitted) {
                if (childFragmentManager.findFragmentByTag(OFFLINE_SYNC_TAG) == null) {
                    OfflineSyncDialog { intents.onNext(NavigateToTasksOnPostSubmit) }
                        .show(childFragmentManager, OFFLINE_SYNC_TAG)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> intents.onNext(BackClicked)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val OFFLINE_SYNC_TAG = "offline_sync_tag"
    }
}
