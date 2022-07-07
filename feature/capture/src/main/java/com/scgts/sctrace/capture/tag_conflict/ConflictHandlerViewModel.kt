package com.scgts.sctrace.capture.tag_conflict

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.util.toUiModels
import com.scgts.sctrace.capture.tag_conflict.ConflictHandlerMvi.Intent
import com.scgts.sctrace.capture.tag_conflict.ConflictHandlerMvi.Intent.*
import com.scgts.sctrace.capture.tag_conflict.ConflictHandlerMvi.ViewState
import com.scgts.sctrace.framework.navigation.AppNavigator
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import util.ScanStateManager
import util.TopToastManager

class ConflictHandlerViewModel(
    private val assetsIdsCache: ConflictIdsCache,
    private val scanStateManager: ScanStateManager,
    private val topToastManager: TopToastManager,
    private val navigator: AppNavigator,
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is Assets -> prev.copy(assets = intent.assets)
            is ExpandClicked -> prev.copy(expanded = !prev.expanded)
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith({ ViewState() }, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {
        dataIntent(assetsIdsCache.get().toObservable()) {
            it.map { assets -> Assets(assets.toUiModels()) }
        }

        viewIntentCompletable<AssetClicked> {
            it.flatMapCompletable { intent ->
                assetsIdsCache.get().flatMapCompletable { assets ->
                    navigator.popBackStack()
                        .andThen(assetsIdsCache.assetSelected(assets.find { asset -> asset.id == intent.assetId }!!))
                        .andThen(topToastManager.setShowAssetAddedToastFlag(true))
                }
            }
        }

        viewIntentCompletable<CancelClicked> {
            it.flatMapCompletable {
                scanStateManager.setScanning().andThen(navigator.popBackStack())
            }
        }

        viewIntentPassThroughs(ExpandClicked::class)
    }
}