package com.scgts.sctrace.auth

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface OktaService {
    fun registerCallback(): Completable
    fun login(): Completable
    fun refreshTokens(): Single<String>
    fun logout(): Completable
}