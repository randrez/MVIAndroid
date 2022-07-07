package com.scgts.sctrace.feature.login.ui

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.feature.login.ui.LoginMvi.Intent
import com.scgts.sctrace.feature.login.ui.LoginMvi.Intent.Login
import com.scgts.sctrace.feature.login.ui.LoginMvi.ViewState
import com.scgts.sctrace.login.LoginRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val initialState = Supplier { ViewState() }
    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {

        viewIntentCompletable<Login> {
            it.flatMapCompletable { loginRepository.login() }
        }
    }
}
