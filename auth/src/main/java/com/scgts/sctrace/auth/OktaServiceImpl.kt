package com.scgts.sctrace.auth

import com.dynatrace.android.agent.Dynatrace
import com.okta.oidc.AuthorizationStatus
import com.okta.oidc.AuthorizationStatus.AUTHORIZED
import com.okta.oidc.AuthorizationStatus.SIGNED_OUT
import com.okta.oidc.RequestCallback
import com.okta.oidc.ResultCallback
import com.okta.oidc.Tokens
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.params.TokenTypeHint
import com.okta.oidc.net.response.IntrospectInfo
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.util.AuthorizationException
import com.scgts.sctrace.auth.di.ActivityContext
import com.scgts.sctrace.base.auth.AuthManager
import com.scgts.sctrace.base.model.LoginData
import com.scgts.sctrace.base.model.User
import com.scgts.sctrace.network.NetworkChangeListener
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.SingleSubject
import timber.log.Timber

class OktaServiceImpl(
    private val oktaClient: WebAuthClient,
    private val activityContext: ActivityContext,
    private val authManager: AuthManager,
    private val networkChangeListener: NetworkChangeListener
) : OktaService {
    private val sessionClient = oktaClient.sessionClient
    private val disposables = CompositeDisposable()

    private val tokenSubject = SingleSubject.create<String>()

    override fun registerCallback(): Completable {
        oktaClient.registerCallback(
            object : ResultCallback<AuthorizationStatus, AuthorizationException> {
                override fun onSuccess(result: AuthorizationStatus) {
                    when (result) {
                        AUTHORIZED -> {
                            checkTokens(sessionClient.tokens) { accessToken, refreshToken ->
                                disposables.add(
                                    authManager.notifyLoggedIn(
                                        LoginData(
                                            accessToken = accessToken,
                                            refreshToken = refreshToken,
                                            tokenExpirationTime = sessionClient.tokens.expiresIn
                                        )
                                    ).andThen(getUserInfo()).subscribe()
                                )
                            }
                        }
                        SIGNED_OUT -> disposables.add(logUserOutOfTheApp().subscribe())
                        else -> disposables.add(logUserOutOfTheApp().subscribe())
                    }
                }

                override fun onCancel() {
                    disposables.add(logUserOutOfTheApp().subscribe())
                }

                override fun onError(error: String?, exception: AuthorizationException?) {
                    Timber.e("$exception: $error")
                    Dynatrace.reportError(error, exception?.cause ?: Throwable(error))
                    disposables.add(logUserOutOfTheApp().subscribe())
                }
            }, activityContext.activity
        )
        return Completable.complete()
    }

    override fun login(): Completable {
        oktaClient.signIn(activityContext.activity, null)
        return Completable.complete()
    }

    override fun refreshTokens(): Single<String> {
        if (sessionClient.isAuthenticated && sessionClient.tokens.refreshToken != null) {
            sessionClient.introspectToken(
                sessionClient.tokens.refreshToken,
                TokenTypeHint.REFRESH_TOKEN,
                object : RequestCallback<IntrospectInfo, AuthorizationException> {
                    override fun onSuccess(result: IntrospectInfo) {
                        if (result.isActive) sessionClient.refreshToken(getRefreshTokenCallback())
                        else disposables.add(logout().subscribe())
                    }

                    override fun onError(error: String?, exception: AuthorizationException?) {
                        Dynatrace.reportError(error, exception?.cause ?: Throwable(error))
                        tokenSubject.onError(Throwable(error))
                        disposables.add(logout().subscribe())
                    }
                }
            )
        } else disposables.add(logout().subscribe())
        return tokenSubject.hide()
    }

    override fun logout(): Completable {
        return if (networkChangeListener.isConnected()) {
            oktaClient.signOutOfOkta(activityContext.activity)
            Completable.complete()
        } else logUserOutOfTheApp()
    }

    private fun logUserOutOfTheApp(): Completable {
        sessionClient.clear()
        return authManager.notifyLoggedOut()
    }

    private fun getRefreshTokenCallback(): RequestCallback<Tokens?, AuthorizationException?> {
        return object : RequestCallback<Tokens?, AuthorizationException?> {
            override fun onSuccess(result: Tokens) {
                checkTokens(result) { accessToken, refreshToken ->
                    tokenSubject.onSuccess(accessToken)
                    disposables.add(
                        authManager.storeNewTokens(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            tokenExpiresIn = result.expiresIn
                        ).subscribe()
                    )
                }
            }

            override fun onError(error: String, exception: AuthorizationException?) {
                Dynatrace.reportError(error, exception?.cause ?: Throwable(error))
                tokenSubject.onError(Throwable(error))
                disposables.add(logout().subscribe())
            }
        }
    }

    private fun getUserInfo(): Completable {
        sessionClient.getUserProfile(
            object : RequestCallback<UserInfo, AuthorizationException> {
                override fun onSuccess(result: UserInfo) {
                    val username = (result["name"] ?: "") as String
                    Dynatrace.identifyUser(username)
                    disposables.add(
                        authManager.storeUserData(
                            User(
                                id = (result["sub"] ?: "") as String,
                                name = username,
                                email = (result["email"] ?: "") as String
                            )
                        ).subscribe()
                    )
                }

                override fun onError(error: String?, exception: AuthorizationException?) {
                    Timber.e("$exception: $error")
                    Dynatrace.reportError(error, exception?.cause ?: Throwable(error))
                }
            }
        )
        return Completable.complete()
    }

    private fun checkTokens(
        tokens: Tokens,
        onValidTokens: (accessToken: String, refreshToken: String) -> Unit
    ) {
        tokens.accessToken?.let { accessToken ->
            tokens.refreshToken?.let { refreshToken ->
                onValidTokens(accessToken, refreshToken)
            } ?: run {
                val throwable = Throwable("Refresh Token is NULL")
                tokenSubject.onError(throwable)
                Dynatrace.reportError("Null Refresh Token", throwable)
            }
        } ?: run {
            val throwable = Throwable("Access Token is NULL")
            tokenSubject.onError(throwable)
            Dynatrace.reportError("Null Access Token", throwable)
        }
    }
}