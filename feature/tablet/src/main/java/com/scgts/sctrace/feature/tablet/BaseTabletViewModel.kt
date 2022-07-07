package com.scgts.sctrace.feature.tablet

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.feature.tablet.BaseTabletMvi.Intent
import com.scgts.sctrace.feature.tablet.BaseTabletMvi.ViewState
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination
import com.scgts.sctrace.framework.navigation.ScreenAnimation
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier

class BaseTabletViewModel(
    private val navigator: AppNavigator,
    private val tasksRepository: TasksRepository
) : ViewModel(), MviViewModel<Intent, ViewState> {
    private val initialState = Supplier { ViewState() }
    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is Intent.SetUnsubmittedTaskCount -> prev.copy(unsubmittedTaskCount = intent.count)
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
            .distinctUntilChanged()
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {
            dataIntent(tasksRepository.getPendingTraceEvents()) {
                it.map { traceEvents ->
                    Intent.SetUnsubmittedTaskCount(
                        traceEvents.groupBy { evevnt -> evevnt.taskId }.keys.size
                    )
                }
            }

            viewIntentCompletable<Intent.GoToUnsyncedSubmissions> {
                it.flatMapCompletable {
                    navigator.navigate(
                        NavDestination.NavDestinationArgs.UnsyncedSubmissions(it.originName),
                        ScreenAnimation.SLIDE_IN_FROM_RIGHT
                    )
                }
            }
            viewIntentCompletable<Intent.GoToSettings> {
                it.flatMapCompletable {
                    navigator.navigate(
                        NavDestination.NavDestinationArgs.Settings(it.originName),
                        ScreenAnimation.SLIDE_IN_FROM_RIGHT
                    )
                }
            }
            viewIntentCompletable<Intent.GoBack> {
                it.flatMapCompletable {
                    navigator.popBackStack()
                }
            }
        }
}