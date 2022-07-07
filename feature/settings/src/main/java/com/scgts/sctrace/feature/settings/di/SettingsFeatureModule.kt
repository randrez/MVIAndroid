package com.scgts.sctrace.feature.settings.di

import com.scgts.sctrace.feature.settings.ui.SettingsViewModel
import com.scgts.sctrace.feature.settings.ui.feedback.FeedbackInputCache
import com.scgts.sctrace.feature.settings.ui.feedback.GiveFeedbackViewModel
import com.scgts.sctrace.feature.settings.ui.select.SettingsSelectionViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object SettingsFeatureModule {
    private val feedbackInputCacheModule = module {
        single { FeedbackInputCache() }
    }
    private val viewModelModule = module {
        viewModel { (header: String) ->
            SettingsViewModel(header, get(), get(), get())
        }
        viewModel { (settingsType: String) ->
            SettingsSelectionViewModel(settingsType, get(), get())
        }
        viewModel { GiveFeedbackViewModel(get(), get(), get(), get()) }
    }

    val modules: List<Module> = listOf(viewModelModule, feedbackInputCacheModule)
}
