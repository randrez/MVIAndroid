package com.scgts.sctrace.base.auth

import com.scgts.sctrace.base.model.UserKey
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface UserPreferences {
    fun putStringSingle(userKey: UserKey, value: String): Completable
    fun getStringSingle(userKey: UserKey): Single<String>
    fun putIntSingle(userKey: UserKey, value: Int): Completable
    fun getInt(userKey: UserKey): Int
    fun putString(userKey: UserKey, value: String)
    fun getString(userKey: UserKey): String
    fun delete(userKey: UserKey): Completable
}