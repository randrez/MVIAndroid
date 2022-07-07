package com.scgts.sctrace.login.di

import com.scgts.sctrace.login.LoginRepository
import com.scgts.sctrace.login.LoginRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

object LoginDataModule {
    private val repositoryModules = module {
        single<LoginRepository> { LoginRepositoryImpl(get(), get()) }
    }

    val modules: List<Module> = listOf(repositoryModules)
}
