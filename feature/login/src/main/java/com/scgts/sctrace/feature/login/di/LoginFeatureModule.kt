package com.scgts.sctrace.feature.login.di

import com.scgts.sctrace.feature.login.ui.LoginViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object LoginFeatureModule {

    private val viewModelModule = module {
        viewModel { LoginViewModel(get()) }
    }

    val modules: List<Module> = listOf(viewModelModule)
}

