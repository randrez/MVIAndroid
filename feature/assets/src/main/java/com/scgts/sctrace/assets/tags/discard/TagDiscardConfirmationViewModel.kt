package com.scgts.sctrace.assets.tags.discard

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.assets.tags.AssetTagCache
import com.scgts.sctrace.assets.tags.discard.TagDiscardConfirmationMvi.Intent
import com.scgts.sctrace.assets.tags.discard.TagDiscardConfirmationMvi.Intent.*
import com.scgts.sctrace.assets.tags.discard.TagDiscardConfirmationMvi.ViewState
import com.scgts.sctrace.queue.QueueRepository
import io.reactivex.rxjava3.core.Observable

class TagDiscardConfirmationViewModel(
    private val assetId: String,
    private val tag: String,
    private val assetTagCache: AssetTagCache,
    private val queueRepository: QueueRepository,
) : ViewModel(), MviViewModel<Intent, ViewState> {
    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(
                { ViewState() },
                { prev, intent ->
                    when (intent) {
                        CancelDiscardClick, Dismiss -> prev.copy(dismiss = true)
                        else -> prev
                    }
                }
            )
    }

    private fun bindIntents(intents: Observable<Intent>) =
        intentsBuild(intents) {
            viewIntentObservable<ConfirmDiscardClick> {
                it.flatMap {
                    assetTagCache.edit {
                        remove(tag)
                        this
                    }
                        .andThen(queueRepository.addDeleteTagToMiscQueue(assetId, tag))
                        .andThen(Observable.just(Dismiss))
                }
            }

            viewIntentPassThroughs(CancelDiscardClick::class)
        }
}