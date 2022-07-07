package com.scgts.sctrace.network

import io.reactivex.rxjava3.core.Observable

interface NetworkChangeListener {
    fun isConnectedObs(): Observable<Boolean>
    fun isConnected(): Boolean
}
