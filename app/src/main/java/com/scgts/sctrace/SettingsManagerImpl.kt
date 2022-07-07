package com.scgts.sctrace

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.base.model.CaptureMethod.Camera
import com.scgts.sctrace.base.model.SettingsKey.Capture
import com.scgts.sctrace.base.model.SettingsKey.UnitOfMeasurement
import com.scgts.sctrace.base.model.UnitType
import com.scgts.sctrace.base.model.UnitType.FEET
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class SettingsManagerImpl(private val sharedPrefs: SharedPreferences) : SettingsManager {
    private val unitTypeCache = InMemoryObjectCache(getUnitTypePref())
    private val captureMethodCache = InMemoryObjectCache(getCaptureMethodPref())

    override fun unitType(): Observable<UnitType> = unitTypeCache.getObservable()
    override fun captureMethod(): Observable<CaptureMethod> = captureMethodCache.getObservable()

    @SuppressLint("ApplySharedPref")
    override fun setUnitType(unitType: UnitType): Completable {
        return Completable.fromAction {
            sharedPrefs.edit().putString(UnitOfMeasurement.key, unitType.abbreviation).commit()
        }.andThen(unitTypeCache.put(unitType))
    }

    @SuppressLint("ApplySharedPref")
    override fun setDefaultCaptureMethod(defaultCaptureMethod: CaptureMethod): Completable {
        return Completable.fromAction {
            sharedPrefs.edit().putString(Capture.key, defaultCaptureMethod.name).commit()
        }.andThen(captureMethodCache.put(defaultCaptureMethod))
    }

    @SuppressLint("ApplySharedPref")
    private fun getUnitTypePref(): UnitType {
        val unitType = sharedPrefs.getString(UnitOfMeasurement.key, null)
        return if (unitType.isNullOrBlank()) {
            sharedPrefs.edit().putString(UnitOfMeasurement.key, FEET.abbreviation).commit()
            FEET
        } else {
            UnitType[unitType]
        }
    }


    @SuppressLint("ApplySharedPref")
    private fun getCaptureMethodPref(): CaptureMethod {
        val captureMethod = sharedPrefs.getString(Capture.key, null)
        return if (captureMethod.isNullOrBlank()) {
            sharedPrefs.edit().putString(Capture.key, Camera.name).commit()
            Camera
        } else {
            CaptureMethod.fromName(captureMethod)
        }
    }
}

