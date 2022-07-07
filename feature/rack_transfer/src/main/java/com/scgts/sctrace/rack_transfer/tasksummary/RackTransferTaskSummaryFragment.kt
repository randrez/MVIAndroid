package com.scgts.sctrace.rack_transfer.tasksummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.feature.tablet.BaseTabletToolBarScreen
import com.scgts.sctrace.rack_transfer.R
import com.scgts.sctrace.rack_transfer.composable.RackTransferTabletTaskSummary
import com.scgts.sctrace.rack_transfer.composable.RackTransferTaskSummary
import com.scgts.sctrace.rack_transfer.tasksummary.RackTransferTaskSummaryMvi.Intent
import com.scgts.sctrace.rack_transfer.tasksummary.RackTransferTaskSummaryMvi.Intent.*
import com.scgts.sctrace.rack_transfer.tasksummary.RackTransferTaskSummaryMvi.ViewState
import dialogs.OfflineSyncDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme
import util.isTablet
import util.sendErrorToDtrace

class RackTransferTaskSummaryFragment :
    BaseTabletToolBarScreen<RackTransferTaskSummaryViewModel>() {

    private val intents = PublishSubject.create<Intent>()

    private val args: RackTransferTaskSummaryFragmentArgs by navArgs()

    override val viewModel: RackTransferTaskSummaryViewModel by viewModel {
        parametersOf(args.taskId, args.orderId, isTablet())
    }
    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    val state = viewState.observeAsState().value
                    if (state != null && state.isOfflineSubmitted) {
                        if (childFragmentManager.findFragmentByTag(OFFLINE_SYNC_TAG) == null) {
                            OfflineSyncDialog { intents.onNext(NavigateToTasksOnPostSubmit) }
                                .show(
                                    childFragmentManager,
                                    OFFLINE_SYNC_TAG
                                )
                        }
                    }
                    if (isTablet()) {
                        RackTransferTabletTaskSummary(
                            viewState = viewState,
                            onBackClick = { intents.onNext(BackClicked) },
                            onSeeDetails = { intents.onNext(SeeDetails) },
                            onRackTransferClicked = { rackTransfer ->
                                intents.onNext(
                                    RackTransferClicked(
                                        rackId = rackTransfer.rackLocationId,
                                        millWorkNum = rackTransfer.millWorkNum,
                                        productDescription = rackTransfer.productDescription
                                    )
                                )
                            },
                            onSubmitClick = { intents.onNext(SubmitClicked) },
                            onCaptureClick = { intents.onNext(CaptureClicked) },
                            onEditClicked = { rackTransfer ->
                                intents.onNext(
                                    EditClicked(
                                        rackId = rackTransfer.rackLocationId,
                                        millWorkNum = rackTransfer.millWorkNum,
                                        productDescription = rackTransfer.productDescription
                                    )
                                )
                            }
                        )
                    } else {
                        RackTransferTaskSummary(
                            viewState = viewState,
                            onBackClick = { intents.onNext(BackClicked) },
                            onSeeDetails = { intents.onNext(SeeDetails) },
                            onRackTransferClicked = { rackTransfer ->
                                intents.onNext(
                                    RackTransferClicked(
                                        rackId = rackTransfer.rackLocationId,
                                        millWorkNum = rackTransfer.millWorkNum,
                                        productDescription = rackTransfer.productDescription
                                    )
                                )
                            },
                            onSubmitClick = { intents.onNext(SubmitClicked) },
                            onCaptureClick = { intents.onNext(CaptureClicked) },
                            onEditClicked = { rackTransfer ->
                                intents.onNext(
                                    EditClicked(
                                        rackId = rackTransfer.rackLocationId,
                                        millWorkNum = rackTransfer.millWorkNum,
                                        productDescription = rackTransfer.productDescription
                                    )
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    private fun render(viewState: ViewState?) {
        _viewState.postValue(viewState)
    }

    companion object {
        const val OFFLINE_SYNC_TAG = "offline_sync_tag"
    }
}