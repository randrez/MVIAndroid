package com.scgts.sctrace.capture.manual.bytag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.capture.composable.manual.CaptureByTagScreen
import com.scgts.sctrace.capture.manual.FragmentRenderer
import com.scgts.sctrace.capture.manual.ManualCaptureMvi
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.Intent
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.Intent.AssetClicked
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.Intent.FindAssetTag
import io.reactivex.rxjava3.subjects.PublishSubject
import theme.SCGTSTheme
import util.isTablet

internal class CaptureByTagFragment(
    private val intents: PublishSubject<Intent>,
) : Fragment(), FragmentRenderer {
    private val _viewState = MutableLiveData<ManualCaptureMvi.ViewState>()
    private val viewState: LiveData<ManualCaptureMvi.ViewState>
        get() = _viewState

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    CaptureByTagScreen(
                        viewState = viewState,
                        isTablet = isTablet(),
                        onFindAssetClicked = { tag -> intents.onNext(FindAssetTag(tag)) },
                        onConsumeClicked = { tag -> intents.onNext(FindAssetTag(tag, true)) },
                        onRejectClicked = { tag -> intents.onNext(FindAssetTag(tag, false)) },
                        onAssetClicked = { assetId -> intents.onNext(AssetClicked(assetId)) }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun render(viewState: ManualCaptureMvi.ViewState) {
        _viewState.postValue(viewState)
    }
}
