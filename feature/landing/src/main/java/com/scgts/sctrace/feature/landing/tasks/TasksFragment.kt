package com.scgts.sctrace.feature.landing.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.feature.landing.composable.tasks.TasksScreen
import com.scgts.sctrace.feature.landing.tasks.TasksMvi.Intent
import com.scgts.sctrace.feature.landing.tasks.TasksMvi.Intent.*
import com.scgts.sctrace.feature.landing.tasks.TasksMvi.ViewState
import com.scgts.sctrace.framework.view.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import theme.SCGTSTheme
import util.sendErrorToDtrace

class TasksFragment : BaseFragment<TasksViewModel>() {
    private val intents = PublishSubject.create<Intent>()

    override val viewModel: TasksViewModel by viewModel()

    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(intents.hide())
            .subscribeOn(Schedulers.io())
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
                    TasksScreen(
                        viewState = viewState,
                        onFilterAndSortClicked = { intents.onNext(FilterAndSortClicked) },
                        onRefresh = { intents.onNext(RefreshData) },
                        onUnsyncedSubmissionClicked = { origin ->
                            intents.onNext(GoToUnsyncedSubmissions(origin))
                        },
                        onSettingsClicked = { origin -> intents.onNext(GoToSettings(origin)) },
                        onTaskClicked = { taskId -> intents.onNext(TaskClicked(taskId)) },
                        onAdHocActionClicked = { adHocAction ->
                            intents.onNext(AdHocActionClicked(adHocAction))
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
