package com.scgts.sctrace.feature.landing.tablet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.composable.tablet.TaskAndTaskDetailsScreenComposable
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsMvi.Intent.ContinueClicked
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsViewModel
import com.scgts.sctrace.feature.landing.tasks.TasksMvi
import com.scgts.sctrace.feature.landing.tasks.TasksMvi.Intent.*
import com.scgts.sctrace.feature.landing.tasks.TasksViewModel
import com.scgts.sctrace.feature.tablet.BaseTabletToolBarScreen
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme
import util.sendErrorToDtrace

class TaskAndTaskDetailsScreenFragment : BaseTabletToolBarScreen<TasksViewModel>() {
    override val viewModel: TasksViewModel by viewModel()
    private val taskDetailViewModel: TaskDetailsViewModel by viewModel { parametersOf(null, null) }
    private val tasksIntents = PublishSubject.create<TasksMvi.Intent>()
    private val taskDetailsIntents = PublishSubject.create<TaskDetailsMvi.Intent>()

    private val _tasksViewState = MutableLiveData<TasksMvi.ViewState>()
    private val tasksViewState: LiveData<TasksMvi.ViewState>
        get() = _tasksViewState

    private val _taskDetailsViewState = MutableLiveData<TaskDetailsMvi.ViewState>()
    private val taskDetailsViewState: LiveData<TaskDetailsMvi.ViewState>
        get() = _taskDetailsViewState

    private val compositeDisposable = CompositeDisposable()
    private var taskDetailsDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(tasksIntents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::tasksRender) { error: Throwable -> sendError(error) }
            .addTo(compositeDisposable)
    }

    override fun onResume() {
        super.onResume()
        if (taskDetailViewModel.hasTaskAndOrderId()) {
            onTaskSelected(taskDetailViewModel.getTaskId(), taskDetailViewModel.getOrderId())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        taskDetailsDisposable?.dispose()
    }

    override fun getScreenTitle(): String = resources.getString(R.string.tasks)

    private fun tasksRender(tasksViewState: TasksMvi.ViewState) {
        _tasksViewState.postValue(tasksViewState)
        if (tasksViewState.error != null) {
            sendError(tasksViewState.error)
        }

        tasksViewState.selectedTask?.let { selectedTask ->
            onTaskSelected(selectedTask.id, selectedTask.orderId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    TaskAndTaskDetailsScreenComposable(
                        tasksViewState = tasksViewState,
                        taskDetailsViewState = taskDetailsViewState,
                        onFilterAndSortClicked = { tasksIntents.onNext(FilterAndSortClicked) },
                        onRefresh = { tasksIntents.onNext(RefreshData) },
                        onUnsyncedSubmissionClicked = { origin ->
                            tasksIntents.onNext(GoToUnsyncedSubmissions(origin))
                        },
                        onSettingsClicked = { origin -> tasksIntents.onNext(GoToSettings(origin)) },
                        onTaskClicked = { taskId -> tasksIntents.onNext(OnTaskSelected(taskId)) },
                        onStartClick = { taskDetailsIntents.onNext(ContinueClicked) },
                        onAdHocActionClicked = { adHocAction ->
                            tasksIntents.onNext(AdHocActionClicked(adHocAction))
                        }
                    )
                }
            }
        }
    }

    private fun onTaskSelected(taskId: String, orderId: String) {
        taskDetailsDisposable?.dispose()
        taskDetailsDisposable = taskDetailViewModel
            .setNewTaskAndOrderId(taskId, orderId)
            .bind(taskDetailsIntents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::taskDetailsRender) { error: Throwable -> sendError(error) }
    }

    private fun sendError(exception: Throwable?) {
        exception?.sendErrorToDtrace(this.javaClass.name)
    }

    private fun taskDetailsRender(viewState: TaskDetailsMvi.ViewState) {
        _taskDetailsViewState.postValue(viewState)
    }
}
