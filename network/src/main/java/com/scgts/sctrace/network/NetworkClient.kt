package com.scgts.sctrace.network

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

class Apollo(
    private val okHttpClient: OkHttpClient,
    private val baseUrl: String
) {
    private val apolloClient by lazy {
        ApolloClient.builder()
            .serverUrl(baseUrl)
            .okHttpClient(okHttpClient)
            .build()
    }

    fun getService(): ApolloClient = apolloClient
}
