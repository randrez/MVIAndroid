package com.scgts.sctrace.auth

import android.graphics.Color
import com.okta.oidc.OIDCConfig
import com.okta.oidc.Okta
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.params.Scope
import com.okta.oidc.storage.SharedPreferenceStorage
import com.scgts.sctrace.auth.di.ActivityContext

class OktaClient(
    private val activityContext: ActivityContext,
    private val clientId: String,
    private val redirectUri: String,
    private val endSessionRedirectUri: String,
    private val discoveryUri: String
) {
    private val config by lazy {
        OIDCConfig.Builder()
            .clientId(clientId)
            .redirectUri(redirectUri)
            .endSessionRedirectUri(endSessionRedirectUri)
            .scopes(Scope.OPENID, Scope.PROFILE, Scope.EMAIL, Scope.OFFLINE_ACCESS)
            .discoveryUri(discoveryUri)
            .create()
    }

    private val oktaClient by lazy {
        Okta.WebAuthBuilder()
            .withConfig(config)
            .withContext(activityContext.context)
            .withStorage(SharedPreferenceStorage(activityContext.context))
            .withTabColor(Color.BLUE)
            .supportedBrowsers()
            .create()
    }

    fun getService(): WebAuthClient = oktaClient
}