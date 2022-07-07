package com.scgts.sctrace.database.di

import com.scgts.sctrace.database.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

object PersistenceModules {
    private const val NAME = "sctrace-database"

    private val databaseModule = module {
        single { AppDatabase.init(androidApplication(), NAME) }
    }

    private val daoModules = module {
        single { get<AppDatabase>().tasksDao() }
        single { get<AppDatabase>().traceEventsDao() }
        single { get<AppDatabase>().assetsDao() }
        single { get<AppDatabase>().userDao() }
        single { get<AppDatabase>().userProjectRolesDao() }
        single { get<AppDatabase>().expectedAmountsDao() }
        single { get<AppDatabase>().conditionsDao() }
        single { get<AppDatabase>().facilitiesDao() }
        single { get<AppDatabase>().rackLocationsDao() }
        single { get<AppDatabase>().projectsDao() }
        single { get<AppDatabase>().projectConditionsDao() }
        single { get<AppDatabase>().miscellaneousQueueDao() }
        single { get<AppDatabase>().projectFacilitiesDao() }
    }

    val modules: List<Module> = listOf(databaseModule, daoModules)
}
