package com.scgts.sctrace.rack_transfer.edittransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.framework.view.BaseFragment
import com.scgts.sctrace.rack_transfer.composable.EditRackTransfer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme
import util.sendErrorToDtrace

class EditRackTransferFragment: BaseFragment<EditRackTransferViewModel>() {

    private val intents = PublishSubject.create<EditRackTransferMvi.Intent>()
    private val args: EditRackTransferFragmentArgs by navArgs()
    override val viewModel: EditRackTransferViewModel by viewModel {
        parametersOf(args.taskId, args.rackId, args.millWorkNum, args.productDescription)
    }

    private val _viewState = MutableLiveData<EditRackTransferMvi.ViewState>()
    private val viewState: LiveData<EditRackTransferMvi.ViewState> get() = _viewState

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
                    EditRackTransfer(
                        viewState = viewState,
                        onSaveClick = {
                            intents.onNext(EditRackTransferMvi.Intent.SaveClicked) },
                        onCloseClick = {
                            intents.onNext(EditRackTransferMvi.Intent.CloseClicked) },
                        onLocationSelected = {
                            intents.onNext(EditRackTransferMvi.Intent.RackLocationSelected(it))
                        },
                        onAssetDeleteClicked = {
                            intents.onNext(EditRackTransferMvi.Intent.OnAssetDeleteClicked(it))
                        }
                    )
                }
            }
        }
    }

    fun render(viewState: EditRackTransferMvi.ViewState) {
        _viewState.postValue(viewState)
    }
}