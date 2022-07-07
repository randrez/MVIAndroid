package com.scgts.sctrace.ad_hoc_action.di

import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionFragmentArgs
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionInputCache
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionViewModel
import com.scgts.sctrace.base.model.AdHocAction
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object AdHocActionFeatureModule {

    private val adHocActionInputCacheModule = module {
        single { AdHocActionInputCache() }
    }

    private val viewModelModule = module {
        viewModel { (adHocAction: String) ->
            AdHocActionViewModel(adHocAction, get(), get(), get(), get())
        }
    }

    val modules: List<Module> = listOf(viewModelModule, adHocActionInputCacheModule)
}