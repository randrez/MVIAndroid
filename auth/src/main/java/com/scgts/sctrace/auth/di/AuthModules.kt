package com.scgts.sctrace.auth.di

import com.scgts.sctrace.auth.OktaClient
import com.scgts.sctrace.auth.OktaService
import com.scgts.sctrace.auth.OktaServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

class AuthModules(
    private val oktaClientId: String,
    private val redirectUri: String,
    private val endSessionRedirectUri: String,
    private val discoveryUri: String
) {
    private val authModules = module {
        single<OktaService> {
            OktaServiceImpl(
                OktaClient(
                    get(),
                    oktaClientId,
                    redirectUri,
                    endSessionRedirectUri,
                    discoveryUri
                ).getService(),
                get(),
                get(),
                get()
            )
        }
    }

    val modules: List<Module> = listOf(authModules)
}