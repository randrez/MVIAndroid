package com.scgts.sctrace

import com.scgts.sctrace.base.auth.AuthManager
import com.scgts.sctrace.base.auth.UserPreferences
import com.scgts.sctrace.base.model.AuthState
import com.scgts.sctrace.base.model.AuthState.LOGGED_IN
import com.scgts.sctrace.base.model.AuthState.LOGGED_OUT
import com.scgts.sctrace.base.model.LoginData
import com.scgts.sctrace.base.model.User
import com.scgts.sctrace.base.model.UserKey.*
import com.scgts.sctrace.user.UserRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class AuthManagerImpl(
    private val userPreferences: UserPreferences,
    private val userRepository: UserRepository
) : AuthManager {
    private val authState = BehaviorSubject.create<AuthState>()

    override fun getAccessToken(): Single<String> {
        return userPreferences.getStringSingle(AccessToken)
    }

    override fun getRefreshToken(): String {
        return userPreferences.getString(RefreshToken)
    }

    override fun isTokenExpired(): Boolean {
        return userPreferences.getInt(TokenExpirationDateTime) < (System.currentTimeMillis() / 1000)
    }

    override fun storeUserData(user: User): Completable {
        return userRepository.storeUserInfo(user)
    }

    override fun storeNewTokens(
        accessToken: String,
        refreshToken: String,
        tokenExpiresIn: Int
    ): Completable {
        val tokenExpirationDateTime = (System.currentTimeMillis() / 1000 + tokenExpiresIn).toInt()
        return userPreferences.putStringSingle(AccessToken, accessToken)
            .andThen(userPreferences.putStringSingle(RefreshToken, refreshToken))
            .andThen(
                userPreferences.putIntSingle(TokenExpirationDateTime, tokenExpirationDateTime)
            )
    }

    override fun authStateObs(): Observable<AuthState> {
        return authState.hide().startWith(loadToken())
    }

    override fun notifyLoggedOut(): Completable {
        return deleteTokens()
            .andThen(userRepository.clearUser())
            .andThen { authState.onNext(LOGGED_OUT) }
    }

    override fun notifyLoggedIn(loginData: LoginData): Completable {
        return storeNewTokens(
            accessToken = loginData.accessToken,
            refreshToken = loginData.refreshToken,
            tokenExpiresIn = loginData.tokenExpirationTime
        ).andThen { authState.onNext(LOGGED_IN) }
    }

    private fun deleteTokens(): Completable {
        return userPreferences.delete(AccessToken)
            .andThen(userPreferences.delete(RefreshToken))
            .andThen(userPreferences.delete(TokenExpirationDateTime))
    }

    private fun loadToken(): Single<AuthState> {
        return userPreferences.getStringSingle(AccessToken).map {
            if (it.isNotEmpty()) {
                LOGGED_IN
            } else {
                LOGGED_OUT
            }
        }
    }
}
