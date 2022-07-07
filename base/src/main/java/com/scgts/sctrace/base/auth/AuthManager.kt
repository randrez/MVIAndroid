package com.scgts.sctrace.base.auth

import com.scgts.sctrace.base.model.AuthState
import com.scgts.sctrace.base.model.LoginData
import com.scgts.sctrace.base.model.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface AuthManager {
    fun getAccessToken(): Single<String>
    fun getRefreshToken(): String
    fun isTokenExpired(): Boolean

    fun storeUserData(user: User): Completable
    fun storeNewTokens(accessToken: String, refreshToken: String, tokenExpiresIn: Int): Completable

    fun authStateObs(): Observable<AuthState>

    fun notifyLoggedIn(loginData: LoginData): Completable
    fun notifyLoggedOut(): Completable
}