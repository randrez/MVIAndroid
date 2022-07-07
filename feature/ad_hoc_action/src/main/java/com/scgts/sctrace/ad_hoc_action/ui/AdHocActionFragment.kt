package com.scgts.sctrace.ad_hoc_action.ui

import android.os.Bundle
import android.view.*
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.ad_hoc_action.composable.AdHocActionScreen
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionMvi.Intent
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionMvi.Intent.*
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionMvi.ViewState
import com.scgts.sctrace.base.model.DispatchType
import com.scgts.sctrace.base.model.DispatchType.*
import com.scgts.sctrace.framework.view.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme

class AdHocActionFragment : BaseFragment<AdHocActionViewModel>() {

    private val args: AdHocActionFragmentArgs by navArgs()
    override val viewModel: AdHocActionViewModel by viewModel { parametersOf(args.action) }
    private val intents = PublishSubject.create<Intent>()
    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render)
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
                    AdHocActionScreen(
                        viewState = viewState,
                        onCloseClick = { intents.onNext(XClicked) },
                        onStartClick = { intents.onNext(StartClicked) },
                        onClickDatePicker = {
                            DatePickerDialogFragment { newDate ->
                                intents.onNext(DateSet(newDate))
                            }.show(requireFragmentManager(), "DatePickerDialogFragment")
                        },
                        onSelectDispatchType = { dispatchType ->
                            if (dispatchType == DISPATCH_TO_YARD) {
                                intents.onNext(DispatchReturnSelected)
                            } else {
                                intents.onNext(DispatchTransferSelected)
                            }
                        },
                        onSelectDropDownProject = { intents.onNext(OnSelectOptionDropDown(it)) },
                        onSelectDropDownFacility = { intents.onNext(OnSelectOptionDropDown(it)) },
                        onSelectDropDownLocation = { intents.onNext(OnSelectOptionDropDown(it)) }
                    )
                }
            }
        }
    }

    private fun render(viewState: ViewState) {
        _viewState.postValue(viewState)
    }
}
