package com.scgts.sctrace.queue

import com.scgts.sctrace.base.model.UserFeedbackPayload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface QueueRepository {

    fun hasMiscellaneousQueue(): Observable<Boolean>

    fun addDeleteTagToMiscQueue(assetId: String, tag: String): Completable

    fun submitMiscellaneousQueue(): Completable

    fun addFeedbackToMiscQueue(feedbackType: String, feedbackSeverity: String?, feedback: String): Completable

    fun getUnSubmittedFeedback(): Single<List<UserFeedbackPayload>>

}