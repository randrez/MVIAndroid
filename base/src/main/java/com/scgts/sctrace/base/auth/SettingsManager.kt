package com.scgts.sctrace.base.auth

import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.base.model.UnitType
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface SettingsManager {
    fun unitType(): Observable<UnitType>
    fun captureMethod(): Observable<CaptureMethod>

    fun setUnitType(unitType: UnitType): Completable
    fun setDefaultCaptureMethod(defaultCaptureMethod: CaptureMethod): Completable
}