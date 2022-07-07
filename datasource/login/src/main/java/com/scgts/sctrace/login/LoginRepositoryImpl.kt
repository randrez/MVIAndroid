package com.scgts.sctrace.login

import com.scgts.sctrace.auth.OktaService
import com.scgts.sctrace.base.auth.AuthManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class LoginRepositoryImpl(
    private val oktaService: OktaService,
    private val authManager: AuthManager
) : LoginRepository {
    override fun registerCallback(): Completable {
        return oktaService.registerCallback()
    }

    override fun login(): Completable {
        return oktaService.login()
    }

    override fun logout(): Completable {
        return oktaService.logout()
    }

    override fun getAccessToken(): Single<String> {
        return if (authManager.isTokenExpired()) {
            oktaService.refreshTokens()
        } else authManager.getAccessToken()
    }
}
