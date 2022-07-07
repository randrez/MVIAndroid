package com.scgts.sctrace.network

import com.scgts.sctrace.login.LoginRepository
import com.scgts.sctrace.network.retrofit.RetrofitClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.context.GlobalContext.get
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

class NetworkModules(
    private val debug: Boolean = false,
    private val graphQlBaseUrl: String,
    private val expressBaseUrl: String
) {

    private val defaultNamespace = named("default")

    private fun okHttpClient(interceptor: Interceptor): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()

        if (debug) {
            HttpLoggingInterceptor().run {
                level = HttpLoggingInterceptor.Level.BODY
                httpClientBuilder.addInterceptor(this)
            }
        }
        httpClientBuilder.addInterceptor(interceptor)

        return httpClientBuilder.build()
    }

    private fun interceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val loginRepo: LoginRepository = get().get()

            val accessToken = loginRepo.getAccessToken().blockingGet()

            val request = original.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .method(original.method, original.body)
                .build()

            chain.proceed(request)
        }
    }

    private val apolloModule = module {
        single(defaultNamespace) { okHttpClient(interceptor()) }

        single<ApolloService> {
            ApolloServiceImpl(
                Apollo(
                    okHttpClient = get(defaultNamespace),
                    baseUrl = graphQlBaseUrl
                ).getService(),
                get()
            )
        }

        single {
            RetrofitClient(
                okHttpClient = get(defaultNamespace),
                baseUrl = expressBaseUrl
            )
        }
    }

    val modules: List<Module> = listOf(apolloModule)
}
