package com.scgts.sctrace.feature.settings.ui.select

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionMvi.Intent
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionMvi.Intent.*
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionMvi.ViewState
import com.scgts.sctrace.framework.navigation.AppNavigator
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction

class SettingsSelectionViewModel(
    private val settingsType: String,
    private val appNavigator: AppNavigator,
    private val settingsManager: SettingsManager
) : ViewModel(), MviViewModel<Intent, ViewState> {
    private val reducer =
        BiFunction<ViewState, Intent, ViewState> { prev, intent ->
            when (intent) {
                is GetCaptureMethod -> prev.copy(captureMethod = intent.captureMethod)
                else -> prev
            }
        }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents).scanWith({
            ViewState(subtitle = settingsType)
        }, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {
            dataIntent(settingsManager.captureMethod()) {
                it.map {
                    GetCaptureMethod(it)
                }
            }

            viewIntentCompletable<OnBackPressed> {
                it.flatMapCompletable { appNavigator.popBackStack() }
            }
            viewIntentCompletable<SelectCaptureMethod> {
                it.flatMapCompletable {
                    settingsManager.setDefaultCaptureMethod(it.capture)
                        .andThen(appNavigator.popBackStack())
                }
            }
        }
}
