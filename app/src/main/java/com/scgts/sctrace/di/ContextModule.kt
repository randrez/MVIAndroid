package com.scgts.sctrace.di

import android.app.Activity
import android.content.Context
import com.scgts.sctrace.auth.di.ActivityContext
import org.koin.core.module.Module
import org.koin.dsl.module

class ContextModule(
    private val activity: Activity,
    private val context: Context
) {
    private val appContextModule = module {
        single { ActivityContext(activity, context) }
    }

    val modules: List<Module> = listOf(appContextModule)
}
