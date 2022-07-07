package com.scgts.sctrace.root

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.DistributeRepository
import com.scgts.sctrace.UpdateStatus
import com.scgts.sctrace.base.auth.AuthManager
import com.scgts.sctrace.base.model.AuthState
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination.Login
import com.scgts.sctrace.framework.navigation.NavDestination.Tasks
import com.scgts.sctrace.login.LoginRepository
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.queue.QueueRepository
import com.scgts.sctrace.root.RootMvi.Intent
import com.scgts.sctrace.root.RootMvi.Intent.*
import com.scgts.sctrace.tasks.ProjectsRepository
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import util.TopToastManager
import util.sendErrorToDtrace
import java.util.concurrent.TimeUnit

class RootViewModel(
    private val navigator: AppNavigator,
    private val authManager: AuthManager,
    private val networkChangeListener: NetworkChangeListener,
    private val tasksRepository: TasksRepository,
    private val queueRepository: QueueRepository,
    private val projectsRepository: ProjectsRepository,
    private val distributeRepository: DistributeRepository,
    private val topToastManager: TopToastManager,
    private val loginRepository: LoginRepository
) : ViewModel(), MviViewModel<Intent, RootMvi.ViewState> {

    override fun bind(intents: Observable<Intent>): Observable<RootMvi.ViewState> {
        return bindIntents(intents).scanWith(
            { RootMvi.ViewState() },
            { prev, intent ->
                when (intent) {
                    is Submitted -> prev.copy(toastMessage = intent.message)
                    else -> prev
                }
            }
        )
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {
        dataIntent(loginRepository.registerCallback())

        dataIntent(distributeRepository.getStatus()) {
            it.filter { updateStatus ->
                updateStatus == UpdateStatus.NO_UPDATE
                        || updateStatus == UpdateStatus.DEBUG_BUILD
                        || !networkChangeListener.isConnected()
            }.flatMap {
                authManager.authStateObs()
            }.flatMap { authState ->
                when (authState) {
                    AuthState.LOGGED_IN -> navigator.clearStack()
                        .andThen(navigator.navigate(Tasks))
                        .andThen(
                            networkChangeListener.isConnectedObs().flatMap { connected ->
                                if (connected) projectsRepository.syncRemote()
                                    .toObservable<Intent>()
                                else Observable.just(NoOp)
                            }
                        )
                    AuthState.LOGGED_OUT -> navigator.clearStack()
                        .andThen(navigator.navigate(Login))
                        .toObservable()
                }
            }
        }

        val submitObs = Observable.combineLatest(
            networkChangeListener.isConnectedObs(),
            queueRepository.hasMiscellaneousQueue(),
            tasksRepository.hasQueue(),
            authManager.authStateObs(),
            { isConnected, hasMiscQueue, hasQueue, authState ->
                Triple(isConnected && authState == AuthState.LOGGED_IN, hasMiscQueue, hasQueue)
            }
        ).debounce(500, TimeUnit.MILLISECONDS)

        dataIntent(submitObs) {
            it.flatMap { (onlineAndLoggedIn, hasMiscQueue, hasQueue) ->
                if (onlineAndLoggedIn) {
                    if (hasMiscQueue) {
                        queueRepository.submitMiscellaneousQueue()
                            .doOnError { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
                    } else {
                        Completable.complete()
                    }.andThen(
                        if (hasQueue) tasksRepository.submitQueue()
                            .doOnError { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
                        else Completable.complete()
                    ).toObservable()
                } else {
                    Observable.just<Intent>(NoOp)
                }.onErrorReturnItem(SyncError)
            }
        }

        dataIntent(tasksRepository.getPendingTaskIdsObs().distinctUntilChanged()) {
            it.flatMapCompletable { taskIds ->
                tasksRepository.updateTasksStatusToPending(taskIds)
            }.toObservable()
        }

        dataIntent(topToastManager.listenForToast()) {
            it.flatMap { message ->
                Observable.just(Submitted(""))
                    .delay(2, TimeUnit.SECONDS)
                    .startWith(Observable.just(Submitted(message)))
            }
        }
    }
}
