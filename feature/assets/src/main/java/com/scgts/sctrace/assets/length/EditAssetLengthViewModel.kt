package com.scgts.sctrace.assets.length

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.assets.confirmation.AssetInputCache
import com.scgts.sctrace.assets.length.EditAssetLengthMvi.Intent
import com.scgts.sctrace.assets.length.EditAssetLengthMvi.Intent.*
import com.scgts.sctrace.assets.length.EditAssetLengthMvi.ViewState
import com.scgts.sctrace.base.model.Length
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.Length.Companion.emptyLength
import com.scgts.sctrace.framework.navigation.AppNavigator
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier

class EditAssetLengthViewModel(
    private val navigator: AppNavigator,
    private val assetInputCache: AssetInputCache,
    private val settingsManager: SettingsManager
) : ViewModel(), MviViewModel<Intent, ViewState> {
    private val initialState = Supplier {
        ViewState(unitType = settingsManager.unitType().blockingFirst())
    }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is IsEditing -> prev.copy(isEditing = intent.isEditing)
            is SetLength -> prev.copy(
                length = intent.length,
                isEditing = false
            )
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {
        dataIntent(assetInputCache.getObservable()) {
            it.flatMap { assetInput ->
                Observable.just(SetLength(assetInput.length ?: emptyLength()))
            }
        }

        viewIntentCompletable<SaveLength> {
            it.flatMapCompletable { intent ->
                assetInputCache.edit {
                    this.length = Length(intent.length, settingsManager.unitType().blockingFirst())
                    this
                }
            }
        }

        viewIntentCompletable<Done> {
            it.flatMapCompletable {
                navigator.popBackStack()
            }
        }

        viewIntentPassThroughs(IsEditing::class)
    }
}
