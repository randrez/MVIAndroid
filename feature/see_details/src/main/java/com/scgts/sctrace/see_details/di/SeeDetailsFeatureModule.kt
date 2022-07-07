package com.scgts.sctrace.see_details.di

import com.scgts.sctrace.see_details.ui.SeeDetailsFragmentArgs
import com.scgts.sctrace.see_details.ui.SeeDetailsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object SeeDetailsFeatureModule {

    private val viewModelModule = module {
        viewModel {
            (args: SeeDetailsFragmentArgs) -> SeeDetailsViewModel(args.taskId, args.orderId, get(), get(), get())
        }
    }

    val modules: List<Module> = listOf(viewModelModule)
}
