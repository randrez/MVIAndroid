package com.scgts.sctrace.user.di

import com.scgts.sctrace.user.UserRepository
import com.scgts.sctrace.user.UserRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

object UserDataModule {
    private val userModules = module {
        single<UserRepository> { UserRepositoryImpl(get(), get()) }
    }

    val modules: List<Module> = listOf(userModules)
}