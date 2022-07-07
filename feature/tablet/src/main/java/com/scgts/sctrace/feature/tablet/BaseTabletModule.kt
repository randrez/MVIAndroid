package com.scgts.sctrace.feature.tablet

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object BaseTabletModule {
    private val viewModelModule = module {
        viewModel {
            BaseTabletViewModel(get(), get())
        }
    }

    val modules: List<Module> = listOf(viewModelModule)
}