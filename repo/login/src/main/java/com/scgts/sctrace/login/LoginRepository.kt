package com.scgts.sctrace.login

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface LoginRepository {
    fun registerCallback(): Completable
    fun login(): Completable
    fun logout(): Completable
    fun getAccessToken(): Single<String>
}
