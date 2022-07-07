package com.scgts.sctrace.feature.settings.ui.feedback

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.Intent
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.Intent.*
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.ViewState
import com.scgts.sctrace.feature.settings.ui.feedback.composable.GiveFeedback
import com.scgts.sctrace.framework.view.BaseFragment
import dialogs.OfflineSyncDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import theme.SCGTSTheme
import util.sendErrorToDtrace

class GiveFeedbackFragment : BaseFragment<GiveFeedbackViewModel>() {

    override val viewModel: GiveFeedbackViewModel by viewModel()
    private val intents = PublishSubject.create<Intent>()
    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
                    GiveFeedback(
                        viewState = viewState,
                        onFeedbackDetail = {
                            intents.onNext(InputDetails(detailsValue = it))
                        },
                        onSubmitFeedback = {
                            intents.onNext(OnSubmitPressed)
                        },
                        onSelectFeedbackType = { intents.onNext(FeedbackTypeSelected(it)) },
                        onSelectSeverity = { intents.onNext(SeveritySelected(it)) },
                        onCancel = { intents.onNext(OnCancelPressed) }
                    )
                }
            }
        }
    }

    private fun render(viewState: ViewState) {
        _viewState.postValue(viewState)
        if (viewState.isOfflineSubmitted) {
            if (childFragmentManager.findFragmentByTag(OFFLINE_SYNC_TAG) == null) {
                OfflineSyncDialog { intents.onNext(NavigateToTasksOnPostSubmit) }
                    .show(childFragmentManager, OFFLINE_SYNC_TAG)
            }
        }
    }

    companion object {
        const val OFFLINE_SYNC_TAG = "offline_sync_tag"
    }
}