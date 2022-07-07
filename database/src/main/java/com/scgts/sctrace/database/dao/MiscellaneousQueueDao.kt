package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.database.model.MiscellaneousQueueEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface MiscellaneousQueueDao : BaseDao<MiscellaneousQueueEntity> {
    @Transaction
    @Query("SELECT * FROM MiscellaneousQueueEntity")
    fun getQueueSingle(): Single<List<MiscellaneousQueueEntity>>

    @Transaction
    @Query("SELECT COUNT(*) FROM MiscellaneousQueueEntity")
    fun getQueueCount(): Observable<Int>

    @Transaction
    @Query("SELECT COUNT(*) FROM MiscellaneousQueueEntity WHERE payloadType='userFeedback' ")
    fun getMiscFeedbackCount(): Observable<Int>

    @Transaction
    @Query("DELETE FROM MiscellaneousQueueEntity")
    fun deleteAll(): Completable
}