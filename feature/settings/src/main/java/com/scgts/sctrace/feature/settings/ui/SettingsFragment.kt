package com.scgts.sctrace.feature.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.feature.settings.ui.SettingsMvi.Intent
import com.scgts.sctrace.feature.settings.ui.SettingsMvi.Intent.Logout
import com.scgts.sctrace.feature.settings.ui.SettingsMvi.Intent.OnBackPressed
import com.scgts.sctrace.feature.settings.ui.SettingsMvi.ViewState
import com.scgts.sctrace.feature.settings.ui.composable.Settings
import com.scgts.sctrace.framework.view.BaseFragment
import com.scgts.sctrace.settings.BuildConfig
import com.scgts.sctrace.settings.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme
import util.sendErrorToDtrace

class SettingsFragment : BaseFragment<SettingsViewModel>() {
    private val intents = PublishSubject.create<Intent>()
    private val args: SettingsFragmentArgs by navArgs()
    override val viewModel: SettingsViewModel by viewModel{
        parametersOf(args.originName)
    }
    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    Settings(
                        viewState = viewState,
                        selectedPreference = { intents.onNext(Intent.SelectPreference) },
                        selectedSupport = { intents.onNext(Intent.SelectSupport) },
                        onBackClick = { intents.onNext(OnBackPressed) },
                        onLogout = { intents.onNext(Logout) }
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render) { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
            .autoDisposeOnDestroy()
    }

    private fun render(viewState: ViewState) {
        _viewState.postValue(viewState)
    }
}
