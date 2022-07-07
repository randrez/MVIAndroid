package com.scgts.sctrace.task_summary.di

import com.scgts.sctrace.task_summary.ui.TaskSummaryViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object TaskSummaryFeatureModule {
    private val viewModelModule = module {
        viewModel {
            (taskId: String, orderId: String, isTablet: Boolean) -> TaskSummaryViewModel(taskId, orderId, get(), get(), get(), isTablet, get())
        }
    }

    val modules: List<Module> = listOf(viewModelModule)
}
