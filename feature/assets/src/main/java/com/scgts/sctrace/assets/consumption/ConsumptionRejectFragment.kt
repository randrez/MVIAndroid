package com.scgts.sctrace.assets.consumption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.jakewharton.rxbinding4.widget.textChanges
import dialogs.BaseDialogFragment
import com.scgts.sctrace.assets.confirmation.R
import com.scgts.sctrace.assets.confirmation.SearchableBottomSheet
import com.scgts.sctrace.assets.confirmation.SearchableBottomSheetData.SearchableReasonData
import com.scgts.sctrace.assets.confirmation.databinding.FragmentConsumptionRejectBinding
import com.scgts.sctrace.assets.consumption.ConsumptionIntent.Intent
import com.scgts.sctrace.assets.consumption.ConsumptionIntent.Intent.ReasonSelected
import com.scgts.sctrace.assets.consumption.ConsumptionIntent.ViewState
import com.scgts.sctrace.base.model.Reason
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType.ConsumedToRejected
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType.RejectedToConsumed
import io.reactivex.rxjava3.kotlin.addTo
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

class ConsumptionRejectFragment :
    BaseDialogFragment<Intent, ViewState, ConsumptionViewModel>() {

    private val args: ConsumptionRejectFragmentArgs by navArgs()
    override val viewModel: ConsumptionViewModel by viewModel { parametersOf(args) }

    private var _binding: FragmentConsumptionRejectBinding? = null
    private val binding get() = _binding!!

    private val reasonSearchableBottomSheet by lazy {
        SearchableBottomSheet {
            intents.onNext(
                ReasonSelected((it as SearchableReasonData).reason)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsumptionRejectBinding.inflate(inflater, container, false)
        binding.rejectDiscardButton.setOnClickListener { intents.onNext(Intent.DiscardClick) }
        binding.rejectRejectButton.setOnClickListener { intents.onNext(Intent.RejectClick) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false
        setUpViews()
    }

    private fun setUpViews() {
        with(binding) {
            reasonSection.sectionTitle.text = requireContext().getString(R.string.reason)
            commentSection.sectionTitle.text = requireContext().getString(R.string.comment)

            reasonSection.root.setOnClickListener {
                reasonSearchableBottomSheet.show(
                    requireFragmentManager(),
                    "reasonSearchableBottomSheet"
                )
            }

            commentSection.sectionInfo.textChanges()
                .debounce(250, TimeUnit.MILLISECONDS)
                .subscribe {
                    intents.onNext(
                        Intent.CommentUpdated(it.toString())
                    )
                }.addTo(disposables)
        }

        reasonSearchableBottomSheet.setData(Reason.values().map { SearchableReasonData(it) })

    }

    override fun render(viewState: ViewState) {
        with(binding) {
            if (viewState.statusChange != null) {
                with(rejectWarningIcon) {
                    setBackgroundResource(R.drawable.circle_bg_green)
                    setImageResource(R.drawable.ic_swap)
                }
            }

            rejectTitle.text = getString(
                when (viewState.statusChange) {
                    RejectedToConsumed -> {
                        R.string.reject_to_consume_title
                    }
                    ConsumedToRejected -> {
                        R.string.consume_to_reject_title
                    }
                    else -> R.string.consumption_reject_title
                },
                viewState.pipeNo
            )

            val isRejecting =
                viewState.statusChange == null || viewState.statusChange == ConsumedToRejected
            reasonSection.root.isVisible = isRejecting
            reasonSection.sectionInfo.text = viewState.reason
            commentSection.root.isVisible = isRejecting

            if (viewState.statusChange == RejectedToConsumed) {
                rejectRejectButton.setBackgroundColor(R.color.green)
                rejectRejectButton.text = getString(R.string.yes_consume)
            }
        }
    }
}
