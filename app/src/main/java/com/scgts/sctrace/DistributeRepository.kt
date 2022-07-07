package com.scgts.sctrace

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit


interface DistributeRepository {

    fun getStatus(): Observable<UpdateStatus>
    fun setStatus(status: UpdateStatus)
}

class DistributeRepositoryImpl(val scheduler: Scheduler): DistributeRepository {

    private val statusSubject = BehaviorSubject.createDefault(getDefault())

    private fun getDefault(): UpdateStatus {
      return  if (BuildConfig.DEBUG) {
            UpdateStatus.DEBUG_BUILD
        } else {
            UpdateStatus.NO_STATUS
        }
    }

    override fun getStatus(): Observable<UpdateStatus> {
        return statusSubject.hide().debounce(3, TimeUnit.SECONDS, scheduler)
    }

    override fun setStatus(status: UpdateStatus) {
        statusSubject.onNext(status)
    }
}

enum class UpdateStatus {
    NO_UPDATE,
    UPDATE,
    NO_STATUS,
    DEBUG_BUILD
}