package com.scgts.sctrace.capture.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jakewharton.rxbinding4.widget.queryTextChanges
import com.scgts.sctrace.base.model.DialogType.*
import com.scgts.sctrace.base.model.DialogType.Companion.ASSET_FROM_WRONG_PROJECT_TAG
import com.scgts.sctrace.base.model.DialogType.Companion.ASSET_NOT_FOUND_TAG
import com.scgts.sctrace.base.model.DialogType.Companion.CONSUMPTION_DUPLICATE_TAG
import com.scgts.sctrace.base.util.toFormattedString
import com.scgts.sctrace.capture.R
import com.scgts.sctrace.capture.databinding.FragmentManualCaptureBinding
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.Intent.*
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.ViewState
import com.scgts.sctrace.capture.manual.byattribute.AttributeOptionRowModel
import com.scgts.sctrace.capture.manual.byattribute.CaptureByAttributesFragment
import com.scgts.sctrace.capture.manual.byattribute.SelectAttributeController
import com.scgts.sctrace.capture.manual.bytag.CaptureByTagFragment
import com.scgts.sctrace.framework.view.BaseFragment
import com.scgts.sctrace.base.model.AssetAttribute
import dialogs.AssetFromWrongProjectDialog
import dialogs.AssetNotFoundDialog
import dialogs.ConsumptionDuplicateDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import com.scgts.sctrace.base.util.removeProjectString
import util.sendErrorToDtrace
import java.util.concurrent.TimeUnit

internal class ManualCaptureFragment : BaseFragment<ManualCaptureViewModel>() {

    override val viewModel: ManualCaptureViewModel by viewModel()

    private val intents = PublishSubject.create<ManualCaptureMvi.Intent>()

    private var _binding: FragmentManualCaptureBinding? = null
    private val binding get() = _binding!!

    private val disposables = CompositeDisposable()

    private val selectAttributesController by lazy {
        SelectAttributeController(
            object : AttributeOptionRowModel.AttributeSelectListener {
                override fun onClick(attribute: AssetAttribute, selectedOption: String) {
                    intents.onNext(AttributeEdited(attribute, selectedOption))
                }
            }
        )
    }

    private val bottomSheet by lazy {
        BottomSheetBehavior.from(binding.manualCaptureBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentManualCaptureBinding.inflate(inflater, container, false).apply {
            captureAttributesSelectionList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = selectAttributesController.adapter
            }
        }

        binding.manualViewpager.adapter = ManualPagerAdapter(this).apply {
            addFragment(CaptureByAttributesFragment(intents))
            addFragment(CaptureByTagFragment(intents))
        }
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
        TabLayoutMediator(binding.manualTabLayout, binding.manualViewpager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.manual_capture_tab_attribute)
                1 -> tab.text = getString(R.string.manual_capture_tab_tag)
                else -> tab.text = "undefined"
            }
        }.attach()

        binding.captureAttributesClose.setOnClickListener {
            intents.onNext(CloseSelector)
        }

        binding.manualTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                intents.onNext(TabSelected)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.captureAttributesSearch.queryTextChanges()
            .debounce(200, TimeUnit.MILLISECONDS)
            .map { intents.onNext(SearchForAttribute(it.toString())) }
            .onErrorReturnItem(Unit)
            .subscribe()
            .addTo(disposables)
    }

    private fun render(viewState: ViewState) {
        viewState.optionsForSelectedAttribute?.let { selectorData ->
            if (viewState.searchQuery == null) binding.captureAttributesSearch.setQuery(null, false)

            val data = if (viewState.searchQuery != null && viewState.searchQuery.isNotBlank()) {
                val searchList = selectorData.selections.filter {
                    it.startsWith(viewState.searchQuery)
                }
                selectorData.copy(selections = searchList)
            } else selectorData

            selectAttributesController.setData(data)

            binding.captureAttributesTitle.text = selectorData.attribute.uiName
            binding.captureAttributesSearch.queryHint = "Search ${selectorData.attribute.uiName}"
        }

        bottomSheet.state = if (viewState.showAttributeSelector) {
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_COLLAPSED
        }

        (binding.manualViewpager.adapter as ManualPagerAdapter).render(viewState)

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
            is ConsumptionDuplicate ->
                if (childFragmentManager.findFragmentByTag(CONSUMPTION_DUPLICATE_TAG) == null) {
                    val descriptionMessage = getString(
                        if (viewState.dialogType.consumed) R.string.already_consumed_description
                        else R.string.already_rejected_description
                    )
                    ConsumptionDuplicateDialog(
                        message = descriptionMessage,
                        okayClickListener = { intents.onNext(DismissDialog) }
                    ).show(childFragmentManager, CONSUMPTION_DUPLICATE_TAG)
                }
            else -> {
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        disposables.clear()
        super.onDestroy()
    }
}

interface FragmentRenderer {
    fun render(viewState: ViewState)
}
