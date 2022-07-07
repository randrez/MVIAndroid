package com.scgts.sctrace.assets.confirmation

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.scgts.sctrace.assets.confirmation.AssetConfirmationMvi.Intent
import com.scgts.sctrace.assets.confirmation.AssetConfirmationMvi.Intent.*
import com.scgts.sctrace.assets.confirmation.AssetConfirmationMvi.ViewState
import com.scgts.sctrace.assets.confirmation.SearchableBottomSheetData.SearchableConditionData
import com.scgts.sctrace.assets.confirmation.SearchableBottomSheetData.SearchableLocationData
import com.scgts.sctrace.assets.confirmation.databinding.FragmentAssetConfirmationBinding
import com.scgts.sctrace.assets.detail.AssetDetailController
import com.scgts.sctrace.base.model.TypeWarnings
import dialogs.BaseDialogFragment
import dialogs.OfflineSyncDialog
import dialogs.WarningDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import util.isTablet

class AssetConfirmationFragment :
    BaseDialogFragment<Intent, ViewState, AssetConfirmationViewModel>() {
    private val args: AssetConfirmationFragmentArgs by navArgs()
    override val viewModel by viewModel<AssetConfirmationViewModel> { parametersOf(args) }
    private val assetDetailController = AssetDetailController()
    private var _binding: FragmentAssetConfirmationBinding? = null
    private val binding get() = _binding!!

    private val conditionSearchableBottomSheet by lazy {
        SearchableBottomSheet {
            if (it is SearchableConditionData) {
                intents.onNext(ConditionSelected(it.condition))
            }
        }
    }

    private val locationSearchableBottomSheet by lazy {
        SearchableBottomSheet {
            if (it is SearchableLocationData) {
                intents.onNext(LocationSelected(it.location))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAssetConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false
        setUpViews()
    }

    private fun setUpViews() {
        with(binding) {
            millWorkNoLayout.iconImage.setImageResource(R.drawable.ic_podium_circle)
            millWorkNoLayout.dataTitle.text = getString(R.string.mill_work_no)

            pipeNoLayout.iconImage.setImageResource(R.drawable.ic_pipe_circle)
            pipeNoLayout.dataTitle.text = getString(R.string.pipe_no)

            heatNoLayout.iconImage.setImageResource(R.drawable.ic_flame_circle)
            heatNoLayout.dataTitle.text = getString(R.string.heat_no)

            tagIdLayout.iconImage.setImageResource(R.drawable.ic_tag_circle)
            tagIdLayout.dataTitle.text = getString(R.string.tag_id)

            conditionSection.sectionTitle.text = getString(R.string.condition)

            lengthSection.sectionTitle.text = getString(R.string.length)
            lengthSection.chevron.isVisible = false

            locationSection.sectionTitle.text = getString(R.string.rack_location)

            tagSection.sectionTitle.text = getString(R.string.no_of_tags)
            tagSection.chevron.setImageResource(R.drawable.ic_chevron_right)

            setClickListeners()

            assetDetailRecyclerView.setController(assetDetailController)
        }
    }

    private fun FragmentAssetConfirmationBinding.setClickListeners() {
        discardButton.setOnClickListener { intents.onNext(DiscardClick) }
        confirmButton.setOnClickListener {
            if (binding.tagSection.sectionInfo.text.toString().toIntOrNull() ?: 0 == 0 &&
                !viewModel.shouldIgnoreTagWarnings()
            ) {
                WarningDialogFragment(
                    R.string.asset_no_tag_challenge,
                    R.string.asset_no_tag_explanation
                )
                { ignoreTagWarningsSelected ->
                    if (ignoreTagWarningsSelected) viewModel.ignoreTagWarnings()
                    intents.onNext(Save)
                }.show(requireFragmentManager(), "WarningDialogFragment")
            } else {
                intents.onNext(Save)
            }

        }
        tagSection.sectionLayout.setOnClickListener { intents.onNext(TagClick) }
        lengthSection.sectionLayout.setOnClickListener { intents.onNext(LengthClick) }
        toggleButton.setOnClickListener { intents.onNext(ToggleClick) }

        conditionSection.root.setOnClickListener {
            conditionSearchableBottomSheet.show(
                requireFragmentManager(),
                "conditionSearchableBottomSheet"
            )
        }
        locationSection.root.setOnClickListener {
            locationSearchableBottomSheet.show(
                requireFragmentManager(),
                "locationSearchableBottomSheet"
            )
        }
    }

    override fun render(viewState: ViewState) {
        with(binding) {
            if (viewState.hasSubmitted && viewState.isAdHoc) {
                val checkIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_icon_check)?.apply {
                        colorFilter = PorterDuffColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.n_900),
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                confirmButton.icon = checkIcon
                confirmButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.green)
                confirmButton.setText(R.string.submitted)
                confirmButton.setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.n_900
                    )
                )
                confirmButton.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            }
            heatNoLayout.dataText.text = viewState.heatNumber
            pipeNoLayout.dataText.text = viewState.pipeNumber
            millWorkNoLayout.dataText.text = viewState.millWorkNo
            conditionSection.sectionInfo.text = viewState.selectedCondition?.name
            lengthSection.sectionInfo.text = viewState.laserLength?.getFormattedLengthString()
            tagIdLayout.root.isVisible = viewState.isAdHoc && !viewState.isExpanded
            if (viewState.isAdHoc && viewState.scannedTag != null) {
                assetDetailLayouts.setMaxElementsWrap(2)
                tagIdLayout.dataText.text = viewState.scannedTag
            }

            locationSection.sectionInfo.text = viewState.selectedLocation?.name
            tagSection.sectionInfo.text = viewState.numTags.toString()
            conditionSection.root.isEnabled = viewState.userRole.isYardOperator
            locationSection.root.isEnabled = viewState.userRole.isYardOperator
            tagSection.sectionLayout.isEnabled = viewState.userRole.isYardOperator

            if (viewState.numTags == 0) {
                tagSection.sectionTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error
                    )
                )
                tagSection.sectionInfo.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.error)
            } else {
                tagSection.sectionTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.n_900
                    )
                )
                tagSection.sectionInfo.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.grey_light)
            }

            // TODO: Asset ID to be used to get data in the mean time this
            assetName.text = viewState.name

            if (viewState.dismiss) dismiss()

            changeViewHeight(viewState.isExpanded)
            assetDetailRecyclerView.isVisible = viewState.isExpanded
            topBar.isVisible = viewState.isExpanded
            millWorkNoLayout.iconLayout.isVisible = !viewState.isExpanded
            pipeNoLayout.iconLayout.isVisible = !viewState.isExpanded
            heatNoLayout.iconLayout.isVisible = !viewState.isExpanded
            inputGroup.isVisible = !viewState.isExpanded && !validateShowWarning(viewState)
            confirmButton.isVisible = !viewState.isExpanded
            discardButton.isVisible = !viewState.isExpanded

            assetWarning.isVisible =
                !viewState.isExpanded && validateShowWarning(viewState)

            if (assetWarning.isVisible) {
                assetWarning.text = viewState.typeOrderWarning.message
            }

            if (!viewState.hasSubmitted) {
                if (viewState.newAsset) {
                    assetDetails.text = resources.getString(R.string.new_asset)
                    discardButton.text = resources.getString(R.string.discard)
                    confirmButton.text = if (viewState.isAdHoc) resources.getString(R.string.submit)
                    else resources.getString(R.string.confirm)
                } else {
                    assetDetails.text = resources.getString(R.string.edit_asset)
                    discardButton.text = getString(R.string.cancel)
                    confirmButton.text = getString(R.string.save)
                }
            }
            assetDetailController.setData(viewState.assetDetailList)
            conditionSearchableBottomSheet.setData(
                viewState.conditions.map { SearchableConditionData(it) }
            )
            locationSearchableBottomSheet.setData(
                viewState.rackLocations.map { SearchableLocationData(it) }
            )
            if (viewState.isOfflineSubmitted) {
                OfflineSyncDialog {
                    intents.onNext(OfflineAcknowledged)
                }.show(childFragmentManager, "")
            }

            val confirmButtonEnabled: Boolean =
                if (viewState.taskType != null) viewState.selectedLocation != null && viewState.selectedCondition != null else viewState.selectedLocation != null
            confirmButton.isEnabled = confirmButtonEnabled || validateShowWarning(viewState)

            expandInConfirmation(binding)
        }
    }

    private fun expandInConfirmation(
        binding: FragmentAssetConfirmationBinding,
    ) {
        if (binding.inputGroup.isVisible && !isTablet()) {
            dialog?.let {
                val bottomSheet = it.findViewById<View>(R.id.design_bottom_sheet)
                bottomSheet.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.peekHeight = resources.displayMetrics.heightPixels
            }
            view?.requestLayout()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun validateShowWarning(viewState: ViewState): Boolean {
        return viewState.typeOrderWarning != TypeWarnings.NO_WARNING
    }
}
