package com.scgts.sctrace

import android.app.Application
//import com.facebook.flipper.android.AndroidFlipperClient
//import com.facebook.flipper.android.utils.FlipperUtils
//import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
//import com.facebook.flipper.plugins.databases.impl.SqliteDatabaseDriver
//import com.facebook.soloader.SoLoader
import com.jakewharton.threetenabp.AndroidThreeTen
import com.scgts.sctrace.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class SctraceApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
        Timber.plant(Timber.DebugTree())

        val context = this@SctraceApplication

        startKoin {
            androidLogger()
            androidContext(context)
            modules(appModule)
        }

//        SoLoader.init(this, false);
//        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
//            val client = AndroidFlipperClient.getInstance(context)
//            client.addPlugin(
//                DatabasesFlipperPlugin(
//                    SqliteDatabaseDriver(context) {
//                        context.databaseList().map {
//                            context.getDatabasePath(it)
//                        }
//                    }
//                )
//            )
//
//            client.start()
//        }
    }
}
