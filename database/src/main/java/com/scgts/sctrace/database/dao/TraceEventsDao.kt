package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.database.model.TraceEventEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.threeten.bp.ZonedDateTime

@Dao
interface TraceEventsDao : BaseDao<TraceEventEntity> {
    @Transaction
    @Query("DELETE FROM TraceEventEntity WHERE taskId=:taskId AND assetId=:assetId")
    fun delete(taskId: String, assetId: String): Completable

    @Transaction
    @Query("DELETE  FROM TraceEventEntity WHERE taskId=:taskId AND assetId IN (:assetIds)")
    fun deleteTraceEvents(taskId: String, assetIds: List<String>): Completable

    @Transaction
    @Query("DELETE FROM TraceEventEntity WHERE submitStatus='2_SUBMITTED'")
    fun deleteSubmitted(): Completable

    @Transaction
    @Query("SELECT * FROM TraceEventEntity WHERE taskId=:taskId")
    fun getTraceEventsForTask(taskId: String): Single<List<TraceEventEntity>>

    @Transaction
    @Query("SELECT assetId FROM TraceEventEntity WHERE taskId=:taskId AND submitStatus='0_NOT_SUBMITTED'")
    fun getAssetIdsOfNotSubmittedTraceEventForTask(taskId: String): Single<List<String>>

    @Transaction
    @Query("SELECT DISTINCT updatedAt FROM TraceEventEntity WHERE taskId=:taskId AND updatedAt IS NOT NULL ORDER BY updatedAt DESC LIMIT 1")
    fun getUpdatedAtTraceEvents(taskId: String): Observable<ZonedDateTime>

    @Transaction
    @Query("SELECT * FROM TraceEventEntity WHERE submitStatus='1_PENDING'")
    fun getPendingTraceEvents(): Observable<List<TraceEventEntity>>

    @Transaction
    @Query("SELECT * FROM TraceEventEntity WHERE submitStatus='1_PENDING'")
    fun getPendingTraceEventsSingle(): Single<List<TraceEventEntity>>

    @Query("UPDATE TraceEventEntity SET submitStatus='1_PENDING' WHERE taskId=:taskId AND assetId=:assetId")
    fun addToQueue(taskId: String, assetId: String): Completable

    @Transaction
    @Query("UPDATE TraceEventEntity SET submitStatus='1_PENDING' WHERE taskId=:taskId AND assetId IN (:assetIds)")
    fun addToQueue(taskId: String, assetIds: List<String>): Completable

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM TraceEventEntity WHERE submitStatus='1_PENDING')")
    fun hasQueue(): Observable<Boolean>

    @Transaction
    @Query("SELECT DISTINCT taskId FROM TraceEventEntity WHERE submitStatus='1_PENDING'")
    fun getTasksWithPendingTraceEventsObs(): Observable<List<String>>

    @Transaction
    @Query("SELECT * FROM TraceEventEntity WHERE taskId=:taskId AND assetId=:assetId")
    fun getTraceEvent(taskId: String, assetId: String): Single<TraceEventEntity>

    @Transaction
    @Query("SELECT EXISTS (SELECT * FROM TraceEventEntity WHERE taskId=:taskId AND assetId=:assetId)")
    fun hasTraceEvent(taskId: String, assetId: String): Single<Boolean>

    @Transaction
    @Query("SELECT EXISTS (SELECT * FROM TraceEventEntity WHERE adHocActionTaskType=:type)")
    fun hasTraceEvent(type: String): Single<Boolean>

    @Transaction
    @Query("UPDATE TraceEventEntity SET taskId=:newId WHERE taskId=:id")
    fun updateId(id: String, newId: String): Completable

    @Transaction
    @Query("UPDATE TraceEventEntity SET checkedForOutbound=:checked WHERE taskId=:taskId AND assetId=:assetId")
    fun updateCheckedStatusForOutboundTraceEvent(
        taskId: String,
        assetId: String,
        checked: Boolean
    ): Completable

    @Transaction
    @Query("DELETE FROM TraceEventEntity WHERE taskId=:taskId and checkedForOutbound=0")
    fun deleteUncheckedOutboundTraceEvents(taskId: String): Completable

    @Transaction
    @Query("SELECT assetId FROM TraceEventEntity WHERE taskId=:taskId AND submitStatus='0_NOT_SUBMITTED' AND rackLocationId=:rackLocationId")
    fun getAssetIdsTraceEventsByTaskAndRackLocation(
        taskId: String,
        rackLocationId: String
    ): Single<List<String>>

    @Transaction
    @Query("SELECT EXISTS( SELECT * FROM TraceEventEntity WHERE taskId=:taskId AND checkedForOutbound=1 AND submitStatus IS '0_NOT_SUBMITTED')")
    fun checkIfNotSubmittedTraceEventExistForTaskSingle(taskId: String): Single<Boolean>

    @Transaction
    @Query("SELECT EXISTS( SELECT * FROM TraceEventEntity WHERE taskId=:taskId AND checkedForOutbound=1 AND submitStatus IS '0_NOT_SUBMITTED')")
    fun checkIfNotSubmittedTraceEventExistForTaskObs(taskId: String): Observable<Boolean>

}
