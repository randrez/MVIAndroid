package com.scgts.sctrace.tasks.di

import com.scgts.sctrace.network.retrofit.AssetService
import com.scgts.sctrace.network.retrofit.RetrofitClient
import com.scgts.sctrace.tasks.ProjectsRepository
import com.scgts.sctrace.tasks.TasksRepository
import com.scgts.sctrace.tasks.TasksRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

object TasksDataModule {
    private val apiModules = module {
        single { get<RetrofitClient>().getService(AssetService::class.java) }
    }

    private val repositoryModules = module {
        single<TasksRepository> {
            TasksRepositoryImpl(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }

        single<ProjectsRepository> {
            TasksRepositoryImpl(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

    val modules: List<Module> = listOf(repositoryModules, apiModules)
}
