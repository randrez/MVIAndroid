package com.scgts.sctrace.rack_transfer.transferselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.framework.view.BaseFragment
import com.scgts.sctrace.rack_transfer.composable.RackTransferSelection
import com.scgts.sctrace.rack_transfer.transferselection.RackTransferSelectionMvi.Intent
import com.scgts.sctrace.rack_transfer.transferselection.RackTransferSelectionMvi.Intent.*
import com.scgts.sctrace.rack_transfer.transferselection.RackTransferSelectionMvi.ViewState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme
import util.sendErrorToDtrace

class RackTransferSelectionFragment : BaseFragment<RackTransferSelectionViewModel>() {
    private val intents = PublishSubject.create<Intent>()
    private val args: RackTransferSelectionFragmentArgs by navArgs()
    override val viewModel: RackTransferSelectionViewModel by viewModel {
        parametersOf(args.taskId)
    }

    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

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
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    RackTransferSelection(
                        viewState = viewState,
                        onScanClick = { intents.onNext(ScanClicked) },
                        onCreateClick = { intents.onNext(CreateClicked) },
                        onCloseClick = { intents.onNext(CloseClicked) },
                        onLocationSelected = { rack -> intents.onNext(
                            RackLocationSelected(
                                rack
                            )
                        ) },
                        onAssetDeleteClicked = {
                            intents.onNext(Intent.OnAssetDeleteClicked(it))
                        }
                    )
                }
            }
        }
    }

    private fun render(viewState: ViewState) {
        _viewState.postValue(viewState)
    }
}