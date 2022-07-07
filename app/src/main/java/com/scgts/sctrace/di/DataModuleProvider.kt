package com.scgts.sctrace.di

import com.scgts.sctrace.BuildConfig
import com.scgts.sctrace.auth.di.AuthModules
import com.scgts.sctrace.database.di.PersistenceModules
import com.scgts.sctrace.login.di.LoginDataModule
import com.scgts.sctrace.network.NetworkModules
import com.scgts.sctrace.queue.di.QueueModule
import com.scgts.sctrace.tasks.di.TasksDataModule
import com.scgts.sctrace.user.di.UserDataModule
import org.koin.core.module.Module

object DataModuleProvider {

    val modules: List<Module>
        get() = ArrayList<Module>().apply {
            addAll(TasksDataModule.modules)
            addAll(LoginDataModule.modules)
            addAll(UserDataModule.modules)
            addAll(QueueModule.modules)
            addAll(
                NetworkModules(
                    debug = BuildConfig.DEBUG,
                    graphQlBaseUrl = BuildConfig.GRAPH_QL_URL,
                    expressBaseUrl = BuildConfig.EXPRESS_BASE_URL
                ).modules
            )
            addAll(
                AuthModules(
                    oktaClientId = BuildConfig.OKTA_CLIENT_ID,
                    redirectUri = BuildConfig.OKTA_REDIRECT_URL,
                    endSessionRedirectUri = BuildConfig.OKTA_END_SESSION_REDIRECT_URL,
                    discoveryUri = BuildConfig.OKTA_DISCOVERY_URL
                ).modules
            )
            addAll(PersistenceModules.modules)
        }
}
