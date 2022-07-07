package com.scgts.sctrace.rack_transfer.rackdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.rack_transfer.composable.RackDetailsComposable
import com.scgts.sctrace.rack_transfer.rackdetails.RackDetailsMvi.Intent
import com.scgts.sctrace.rack_transfer.rackdetails.RackDetailsMvi.ViewState
import dialogs.BaseDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme

class RackDetailsFragment : BaseDialogFragment<Intent, ViewState, RackDetailsViewModel>() {

    private val args: RackDetailsFragmentArgs by navArgs()
    override val viewModel: RackDetailsViewModel by viewModel {
        parametersOf(args.taskId, args.rackId, args.millWorkNum, args.productDescription)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    RackDetailsComposable(
                        viewState = viewState,
                        onBackClicked = { intents.onNext(Intent.BackClicked) },
                        onAssetClicked = { intents.onNext(Intent.AssetClicked(it)) }
                    )
                }
            }
        }
    }

    override fun render(viewState: ViewState) {
        super.render(viewState)
        changeViewHeight(true)
    }
}