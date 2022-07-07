package com.scgts.sctrace.feature.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.feature.login.composable.LoginScreen
import com.scgts.sctrace.feature.login.ui.LoginMvi.Intent
import com.scgts.sctrace.feature.login.ui.LoginMvi.Intent.Login
import com.scgts.sctrace.feature.login.ui.LoginMvi.ViewState
import com.scgts.sctrace.framework.view.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import theme.SCGTSTheme
import util.sendErrorToDtrace

class LoginFragment : BaseFragment<LoginViewModel>() {
    private val intents = PublishSubject.create<Intent>()
    override val viewModel: LoginViewModel by viewModel()
    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render) { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
            .autoDisposeOnDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    LoginScreen(
                        viewState = viewState,
                        onLoginClick = { intents.onNext(Login) }
                    )
                }
            }
        }
    }

    private fun render(viewState: ViewState) {
        _viewState.postValue(viewState)
    }
}
