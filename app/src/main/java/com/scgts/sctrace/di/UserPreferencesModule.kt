package com.scgts.sctrace.di

import android.content.SharedPreferences
import com.scgts.sctrace.AuthManagerImpl
import com.scgts.sctrace.SettingsManagerImpl
import com.scgts.sctrace.UserPreferencesImpl
import com.scgts.sctrace.base.auth.AuthManager
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.auth.UserPreferences
import org.koin.core.module.Module
import org.koin.dsl.module

class UserPreferencesModule(
    sharedPreferences: SharedPreferences
) {
    private val userPreferencesModule = module {
        single<UserPreferences> { UserPreferencesImpl(sharedPreferences) }
    }

    private val settingModule = module {
        single<SettingsManager> { SettingsManagerImpl(sharedPreferences) }
    }

    private val authManagerModule = module {
        single<AuthManager> { AuthManagerImpl(get(), get()) }
    }

    val modules: List<Module> = listOf(userPreferencesModule, authManagerModule, settingModule)
}
