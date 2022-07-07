package com.scgts.sctrace.assets.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.navArgs
import dialogs.BaseDialogFragment
import com.scgts.sctrace.assets.detail.AssetDetailMvi.Intent
import com.scgts.sctrace.assets.detail.AssetDetailMvi.ViewState
import com.scgts.sctrace.assets.detail.composable.AssetDetailComposable
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme

class AssetDetailFragment :
    BaseDialogFragment<Intent, ViewState, AssetDetailViewModel>() {

    private val args: AssetDetailFragmentArgs by navArgs()
    override val viewModel by viewModel<AssetDetailViewModel> { parametersOf(args) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    AssetDetailComposable(
                        viewState = viewState,
                        onBackClick = { intents.onNext(Intent.BackClick) },
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