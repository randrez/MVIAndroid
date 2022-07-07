package com.scgts.sctrace.feature.settings.ui.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.feature.settings.ui.SettingsMvi
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionMvi.Intent
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionMvi.Intent.*
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionMvi.ViewState
import com.scgts.sctrace.feature.settings.ui.select.composable.SettingsSelection
import com.scgts.sctrace.framework.view.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import theme.SCGTSTheme
import util.sendErrorToDtrace

class SettingsSelectionFragment : BaseFragment<SettingsSelectionViewModel>() {
    private val intents = PublishSubject.create<Intent>()
    private val args: SettingsSelectionFragmentArgs by navArgs()
    override val viewModel: SettingsSelectionViewModel by viewModel {
        parametersOf(args.settingsType)
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
                    SettingsSelection(
                        viewState = viewState,
                        onSelectCaptureMethod = { intents.onNext(SelectCaptureMethod(it)) },
                        onBackClick = { intents.onNext(OnBackPressed) })
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

