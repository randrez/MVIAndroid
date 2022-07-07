package com.scgts.sctrace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.scgts.sctrace.network.NetworkChangeListener
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class NetworkReceiver : BroadcastReceiver(), NetworkChangeListener {

    private val subject = BehaviorSubject.create<Boolean>()

    override fun onReceive(context: Context, intent: Intent?) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        subject.onNext(networkInfo?.isConnected == true)
    }

    override fun isConnectedObs(): Observable<Boolean> = subject.hide()

    override fun isConnected(): Boolean = subject.value
}
