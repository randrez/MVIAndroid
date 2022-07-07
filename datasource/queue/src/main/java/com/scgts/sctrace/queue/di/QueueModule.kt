package com.scgts.sctrace.queue.di

import com.scgts.sctrace.queue.QueueRepository
import com.scgts.sctrace.queue.QueueRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

object QueueModule {
    private val repositoryModules = module {
        single<QueueRepository> {
            QueueRepositoryImpl(get(), get(), get(), get())
        }
    }

    val modules: List<Module> = listOf(repositoryModules)
}