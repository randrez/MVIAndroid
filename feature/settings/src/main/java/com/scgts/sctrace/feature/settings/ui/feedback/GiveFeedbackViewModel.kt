package com.scgts.sctrace.feature.settings.ui.feedback

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.Intent
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.Intent.*
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackMvi.ViewState
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.queue.QueueRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction

class GiveFeedbackViewModel(
    private val appNavigator: AppNavigator,
    private val feedbackInputCache: FeedbackInputCache,
    private val queueRepository: QueueRepository,
    private val networkChangeListener: NetworkChangeListener
) : ViewModel(), MviViewModel<Intent, ViewState> {

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith({ ViewState() }, reducer)
    }

    private val reducer =
        BiFunction<ViewState, Intent, ViewState> { prev, intent ->
            when (intent) {
                is FeedbackTypeSelected -> prev.copy(feedbackType = intent.feedbackType)
                is SeveritySelected -> prev.copy(severity = intent.severity)
                is InputDetails -> prev.copy(detailsValue = intent.detailsValue)
                is ValidateFormFeedback -> prev.copy(
                    enableSubmit = intent.isEnabled,
                    feedbackType = intent.feedbackType,
                    severity = intent.severity,
                    detailsValue = intent.inputDetails
                )
                is OfflineSubmitted -> prev.copy(isOfflineSubmitted = true)
                else -> prev
            }
        }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {

            dataIntent(feedbackInputCache.getObservable()) {
                it.map {
                    ValidateFormFeedback(
                        isEnabled = it.feedbackType != null && it.details.isNotBlank(),
                        feedbackType = it.feedbackType,
                        severity = it.severity,
                        inputDetails = it.details
                    )
                }
            }

            viewIntentCompletable<FeedbackTypeSelected> {
                it.flatMapCompletable {
                    feedbackInputCache.edit {
                        feedbackType = it.feedbackType
                        this
                    }
                }
            }

            viewIntentCompletable<SeveritySelected> {
                it.flatMapCompletable {
                    feedbackInputCache.edit {
                        severity = it.severity
                        this
                    }
                }
            }

            viewIntentCompletable<InputDetails> {
                it.flatMapCompletable {
                    feedbackInputCache.edit {
                        details = it.detailsValue
                        this
                    }
                }
            }

            viewIntentCompletable<OnCancelPressed> {
                it.flatMapCompletable {
                    clearFeedbackInputCache()
                        .andThen(appNavigator.popBackStack())
                }
            }

            viewIntentObservable<OnSubmitPressed> { onSubmitPressed ->
                onSubmitPressed.flatMap {
                    feedbackInputCache.get().flatMapCompletable {
                        queueRepository.addFeedbackToMiscQueue(
                            feedbackType = it.feedbackType?.name ?: "",
                            feedbackSeverity = it.severity?.name ?: "",
                            feedback = it.details
                        )
                    }.andThen(clearFeedbackInputCache())
                        .andThen(
                            if (networkChangeListener.isConnected())
                                appNavigator.popBackStack()
                                    .andThen(Observable.just(NoOp))
                            else Observable.just(OfflineSubmitted)
                        )
                }
            }

            viewIntentCompletable<NavigateToTasksOnPostSubmit> {
                it.flatMapCompletable {
                    appNavigator.popBackStack()
                }
            }
        }

    private fun clearFeedbackInputCache(): Completable {
        return feedbackInputCache.edit {
            feedbackType = null
            severity = null
            details = ""
            this
        }
    }
}