package com.scgts.sctrace

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.scgts.sctrace.base.model.UserKey
import com.scgts.sctrace.base.auth.UserPreferences
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class UserPreferencesImpl(private val sharedPrefs: SharedPreferences) : UserPreferences {
    @SuppressLint("ApplySharedPref")
    override fun putStringSingle(userKey: UserKey, value: String): Completable {
        return Completable.fromAction {
            sharedPrefs.edit().putString(userKey.key, value).commit()
        }
    }

    override fun getStringSingle(userKey: UserKey): Single<String> {
        return Single.just(sharedPrefs.getString(userKey.key, "") ?: "")
    }

    @SuppressLint("ApplySharedPref")
    override fun putIntSingle(userKey: UserKey, value: Int): Completable {
        return Completable.fromAction {
            sharedPrefs.edit().putInt(userKey.key, value).commit()
        }
    }

    override fun getInt(userKey: UserKey): Int {
        return sharedPrefs.getInt(userKey.key, 0)
    }

    @SuppressLint("ApplySharedPref")
    override fun putString(userKey: UserKey, value: String) {
        sharedPrefs.edit().putString(userKey.key, value).commit()
    }

    override fun getString(userKey: UserKey): String {
        return sharedPrefs.getString(userKey.key, "") ?: ""
    }

    @SuppressLint("ApplySharedPref")
    override fun delete(userKey: UserKey): Completable {
        return Completable.fromAction {
            sharedPrefs.edit().remove(userKey.key).commit()
        }
    }
}
