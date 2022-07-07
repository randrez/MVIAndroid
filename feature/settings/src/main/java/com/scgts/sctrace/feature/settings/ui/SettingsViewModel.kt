package com.scgts.sctrace.feature.settings.ui

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.feature.settings.ui.SettingsMvi.*
import com.scgts.sctrace.feature.settings.ui.SettingsMvi.Intent.*
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination
import com.scgts.sctrace.framework.navigation.ScreenAnimation.SLIDE_IN_FROM_RIGHT
import com.scgts.sctrace.login.LoginRepository
import com.scgts.sctrace.user.UserRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier

class SettingsViewModel(
    private val header: String,
    private val appNavigator: AppNavigator,
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val initialState = Supplier { ViewState(header = header) }

    private val reducer =
        BiFunction<ViewState, Intent, ViewState> { prev, intent ->
            when (intent) {
                is UserData -> prev.copy(name = intent.name, email = intent.email)
                else -> prev
            }
        }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {

            dataIntent(userRepository.getUser()) {
                it.map { user ->
                    UserData(user.name.orEmpty(), user.email.orEmpty())
                }
            }

            viewIntentCompletable<OnBackPressed> {
                it.flatMapCompletable { appNavigator.popBackStack() }
            }

            viewIntentCompletable<SelectPreference> {
                it.flatMapCompletable {
                    appNavigator.navigate(
                        NavDestination.NavDestinationArgs.SettingsSelection(SettingsAction.DefaultCapture.name),
                        SLIDE_IN_FROM_RIGHT
                    )
                }
            }

            viewIntentCompletable<SelectSupport> {
                it.flatMapCompletable {
                    appNavigator.navigate(NavDestination.GiveFeedback, SLIDE_IN_FROM_RIGHT)
                }
            }

            viewIntentCompletable<Logout> {
                it.flatMapCompletable {
                    loginRepository.logout()
                }
            }
        }
}
