package com.scgts.sctrace.feature.landing.task_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.feature.landing.composable.task_details.TaskDetailsScreen
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.Intent.BackClicked
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.Intent.ContinueClicked
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.ViewState
import com.scgts.sctrace.framework.view.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme
import util.sendErrorToDtrace

class TaskDetailsFragment : BaseFragment<TaskDetailsViewModel>() {
    private val intents = PublishSubject.create<TaskDetailsMvi.Intent>()
    private val args: TaskDetailsFragmentArgs by navArgs()
    override val viewModel: TaskDetailsViewModel by viewModel {
        parametersOf(args.taskId, args.orderId)
    }

    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render) { it.sendErrorToDtrace(this.javaClass.name) }
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
                    TaskDetailsScreen(
                        viewState = viewState,
                        onStartClick = { intents.onNext(ContinueClicked) },
                        onBackClick = { intents.onNext(BackClicked) }
                    )
                }
            }
        }
    }

    private fun render(viewState: ViewState) {
        _viewState.postValue(viewState)
    }
}
