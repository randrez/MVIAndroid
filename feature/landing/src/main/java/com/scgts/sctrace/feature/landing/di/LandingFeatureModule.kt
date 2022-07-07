package com.scgts.sctrace.feature.landing.di

import com.scgts.sctrace.feature.landing.filter_and_sort.FilterAndSortViewModel
import com.scgts.sctrace.feature.landing.task_details.TaskDetailsViewModel
import com.scgts.sctrace.feature.landing.tasks.TasksViewModel
import com.scgts.sctrace.feature.landing.unsynced_submissions.UnsyncSubmissionViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object LandingFeatureModule {
    private val viewModelModule = module {
        viewModel {
            TasksViewModel(get(), get(), get(), get(), get(), get(), get())
        }
        viewModel { (taskId: String?, orderId: String?) ->
            TaskDetailsViewModel(taskId, orderId, get(), get())
        }
        viewModel {
            FilterAndSortViewModel(get(), get(), get(), get())
        }
        viewModel {
            UnsyncSubmissionViewModel(get(), get(), get())
        }
    }

    val modules: List<Module> = listOf(viewModelModule)
}
