package com.scgts.sctrace.capture.manual.byattribute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.capture.composable.manual.CaptureByAttributes
import com.scgts.sctrace.capture.manual.FragmentRenderer
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.Intent
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.Intent.*
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.ViewState
import io.reactivex.rxjava3.subjects.PublishSubject
import theme.SCGTSTheme
import util.isTablet

internal class CaptureByAttributesFragment(
    private val intents: PublishSubject<Intent>,
) : Fragment(), FragmentRenderer {
    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    CaptureByAttributes(
                        viewState = viewState,
                        isTablet = isTablet(),
                        onFindAssetClicked = { intents.onNext(FindAsset()) },
                        onConsumeClicked = { intents.onNext(FindAsset(true)) },
                        onRejectClicked = { intents.onNext(FindAsset(false)) },
                        onAssetClicked = { assetId -> intents.onNext(AssetClicked(assetId)) },
                        onAttributeClicked = { attribute ->
                            intents.onNext(AttributeExpandClicked(attribute))
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun render(viewState: ViewState) {
        _viewState.postValue(viewState)
    }
}
