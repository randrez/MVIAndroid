package com.scgts.sctrace

import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.UpdateTrack
import com.scgts.sctrace.di.*
import com.scgts.sctrace.root.RootMvi
import com.scgts.sctrace.root.RootMvi.Intent.OnResume
import com.scgts.sctrace.root.RootViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import util.ComponentsModules
import util.sendErrorToDtrace
import java.util.*

class RootActivity : AppCompatActivity() {

    private var modules: MutableList<Module> = mutableListOf()

    private val receiver: NetworkReceiver by inject()

    private val viewModel: RootViewModel by viewModel()

    private val scTraceDistributeListener: ScTraceDistributeListener by inject()

    private val intents = PublishSubject.create<RootMvi.Intent>()

    private lateinit var viewModelDisposable: Disposable

    private var isRestarting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkOrientation()
        if (isRestarting) return
        setContentView(R.layout.activity_root)

        val navController = findViewById<View>(R.id.root_nav_host_fragment).findNavController()
        val userPreferenceModule = UserPreferencesModule(
            this@RootActivity.getSharedPreferences(
                "sctrace_prefs",
                MODE_PRIVATE
            )
        )
        modules.apply {
            addAll(NavigationModule(navController).modules)
            addAll(ContextModule(this@RootActivity, this@RootActivity as Context).modules)
            addAll(userPreferenceModule.modules)
            addAll(DataModuleProvider.modules)
            addAll(FeatureModuleProvider.modules)
            addAll(ComponentsModules.modules)
        }

        loadKoinModules(modules)
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        scTraceDistributeListener.setContext(this)
        Distribute.setUpdateTrack(UpdateTrack.PRIVATE)
        Distribute.setListener(scTraceDistributeListener)
        AppCenter.start(application, BuildConfig.APP_CENTER_KEY, Distribute::class.java)

        viewModelDisposable = viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ render(it) },
                { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) })

        RxJavaPlugins.setErrorHandler { e -> e.printStackTrace() }
    }

    private fun render(viewState: RootMvi.ViewState) {
        if (viewState.toastMessage != "") {
            val layout = findViewById<View>(R.id.toast_layout)
            val view = findViewById<TextView>(R.id.toast_success)
            view.text = viewState.toastMessage
            layout.visibility = View.VISIBLE
        } else if (viewState.toastMessage == "") {
            val layout = findViewById<View>(R.id.toast_layout)
            layout.visibility = View.GONE
        }
    }

    private fun checkOrientation() {
        val newOrientation = if (resources.getBoolean(R.bool.isTablet)) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        isRestarting = if (requestedOrientation != newOrientation) {
            requestedOrientation = newOrientation
            recreate()
            true
        } else false
    }

    override fun onResume() {
        super.onResume()
        intents.onNext(OnResume)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRestarting) return
        unregisterReceiver(receiver)
        if (modules.isNotEmpty()) {
            unloadKoinModules(modules)
        }
        if (!viewModelDisposable.isDisposed) {
            viewModelDisposable.dispose()
        }
    }

    override fun onBackPressed() {
        val navController = findViewById<View>(R.id.root_nav_host_fragment).findNavController()
        if (!navController.popBackStack()) {
            finish()
            return
        }
    }
}
