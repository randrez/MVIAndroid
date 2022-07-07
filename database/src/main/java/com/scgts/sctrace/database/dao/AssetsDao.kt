package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.scgts.sctrace.base.model.Asset
import com.scgts.sctrace.base.model.ProductDescription
import com.scgts.sctrace.base.model.RackTransferData
import com.scgts.sctrace.base.model.Tallies
import com.scgts.sctrace.database.model.AssetEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface AssetsDao : BaseDao<AssetEntity> {
    @Transaction
    @Query("DELETE FROM AssetEntity WHERE id IN (:ids)")
    fun deleteByIds(ids: List<String>): Completable

    @Transaction
    @Query("DELETE FROM AssetEntity WHERE projectId IN (:projectIds)")
    fun deleteByProjectsIds(projectIds: List<String>): Completable

    @Transaction
    @Query("SELECT * FROM AssetEntity WHERE projectId=:projectId")
    fun getAssetsSingle(projectId: String): Single<List<AssetEntity>>

    @Transaction
    @Query("SELECT * FROM AssetEntity WHERE id in (:assetIds)")
    fun getAssetsForIds(assetIds: List<String>): Single<List<AssetEntity>>

    @Transaction
    @Query("SELECT * FROM AssetEntity WHERE tags LIKE '%\"' || :tag || '\"%'")
    fun getAssetsForTag(tag: String): Single<List<AssetEntity>>

    @Transaction
    @Query("SELECT * FROM AssetEntity WHERE tags LIKE '%\"' || :tag || '\"%'")
    fun getAssetByTag(tag: String): Single<AssetEntity>

    @Transaction
    @Query("SELECT * FROM AssetEntity WHERE id=:id")
    fun getAssetById(id: String): Single<AssetEntity>

    @Transaction
    @Query("UPDATE AssetEntity SET tags=:tags WHERE id=:id")
    fun updateAssetTags(id: String, tags: List<String>): Completable

    @RawQuery
    fun getAttributes(query: SupportSQLiteQuery): Single<List<String>>

    @Transaction
    @Query("SELECT * FROM AssetEntity WHERE millName=:manufacturer AND pipeNumber=:pipeNumber AND heatNumber=:heatNumber AND millWorkNumber=:millWorkNumber AND exMillDate=:exMillDate")
    fun getAssetByAttributes(
        manufacturer: String,
        pipeNumber: String,
        heatNumber: String,
        millWorkNumber: String,
        exMillDate: String,
    ): Single<AssetEntity>

    @Transaction
    @Query("SELECT SUM(runningLength) FROM AssetEntity WHERE id in (:assetIds)")
    fun getTotalRunningLength(assetIds: List<String>): Single<Double>

    @Transaction
    @Query("SELECT DISTINCT productId, outerDiameter, weight, grade, endFinish, range, commodity FROM AssetEntity")
    fun getProductDescriptions(): Single<List<ProductDescription>>

    @Transaction
    @Query("SELECT DISTINCT productId, outerDiameter, weight, grade, endFinish, range, commodity, shipmentNumber, contractNumber, conditionId, rackLocationId FROM AssetEntity")
    fun getProductDescriptionsOutbounds(): Single<List<ProductDescription>>

    @Transaction
    @Query("SELECT DISTINCT productId, outerDiameter, weight, grade, endFinish, range, commodity FROM AssetEntity WHERE id=:assetId")
    fun getProductDescriptionById(assetId: String): Single<ProductDescription>

    @Transaction
    @Query("SELECT SUM(length) FROM AssetEntity WHERE id in (:assetIds)")
    fun getTotalSummedLength(assetIds: List<String>): Single<Double>

    /**
     *  The following Query will return a list of all Assets for a given task with the
     *      laser length, condition, and rack location that the user specified (if any) in the
     *      trace event of that asset in the given task and also checks if the asset is expected
     *      in the task's order.
     */
    @Transaction
    @Query(
        "SELECT AssetEntity.id, COALESCE(TraceEventEntity.laserLength, AssetEntity.length) AS length, AssetEntity.weight, AssetEntity.millName, COALESCE(TraceEventEntity.rackLocationId, AssetEntity.rackLocationId) as rackLocationId, AssetEntity.tags, AssetEntity.heatNumber, AssetEntity.pipeNumber, AssetEntity.outerDiameter, AssetEntity.grade, AssetEntity.range, AssetEntity.endFinish, AssetEntity.runningLength, AssetEntity.millWorkNumber, AssetEntity.productId, AssetEntity.commodity, (SELECT CASE WHEN TaskEntity.orderType='OUTBOUND' AND TaskEntity.type IN ('BUILD_ORDER', 'DISPATCH') THEN EXISTS(SELECT ExpectedAmountEntity.productId FROM TaskEntity LEFT JOIN ExpectedAmountEntity ON TaskEntity.orderId=ExpectedAmountEntity.orderId WHERE TaskEntity.id=:taskId AND ExpectedAmountEntity.productId=AssetEntity.productId AND CASE WHEN ExpectedAmountEntity.contractNumber IS NOT NULL THEN AssetEntity.contractNumber=ExpectedAmountEntity.contractNumber ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.shipmentNumber IS NOT NULL THEN AssetEntity.shipmentNumber=ExpectedAmountEntity.shipmentNumber ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.conditionId IS NOT NULL THEN COALESCE(TraceEventEntity.conditionId, AssetEntity.conditionId)=ExpectedAmountEntity.conditionId ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.rackLocationId IS NOT NULL THEN COALESCE(TraceEventEntity.rackLocationId, AssetEntity.rackLocationId)=ExpectedAmountEntity.rackLocationId ELSE 1=1 END) ELSE 1 END FROM TaskEntity WHERE TaskEntity.id=:taskId) AS expectedInOrder, TraceEventEntity.consumed, TraceEventEntity.checkedForOutbound, AssetEntity.makeUpLossFt, COALESCE(TraceEventEntity.conditionId, AssetEntity.conditionId) AS conditionId, AssetEntity.shipmentNumber, AssetEntity.contractNumber, AssetEntity.projectId, TraceEventEntity.submitStatus FROM AssetEntity LEFT JOIN TraceEventEntity ON TraceEventEntity.assetId=AssetEntity.id WHERE TraceEventEntity.taskId=:taskId ORDER BY TraceEventEntity.submitStatus, TraceEventEntity.scannedAt DESC"
    )
    fun getAllAssetsForTask(taskId: String): Observable<List<Asset>>

    /**
     *  The following Query will return a list of NOT SUBMITTED Assets for a given task with the
     *      laser length, condition, and rack location that the user specified (if any) in the
     *      trace event of that asset in the given task and also checks if the asset is expected
     *      in the task's order.
     */
    @Transaction
    @Query(
        "SELECT AssetEntity.id, COALESCE(TraceEventEntity.laserLength, AssetEntity.length) AS length, AssetEntity.weight, AssetEntity.millName, COALESCE(TraceEventEntity.rackLocationId, AssetEntity.rackLocationId) as rackLocationId, AssetEntity.tags, AssetEntity.heatNumber, AssetEntity.pipeNumber, AssetEntity.outerDiameter, AssetEntity.grade, AssetEntity.range, AssetEntity.endFinish, AssetEntity.runningLength, AssetEntity.millWorkNumber, AssetEntity.productId, AssetEntity.commodity, (SELECT CASE WHEN TaskEntity.orderType='OUTBOUND' AND TaskEntity.type IN ('BUILD_ORDER', 'DISPATCH') THEN EXISTS(SELECT ExpectedAmountEntity.productId FROM TaskEntity LEFT JOIN ExpectedAmountEntity ON TaskEntity.orderId=ExpectedAmountEntity.orderId WHERE TaskEntity.id=:taskId AND ExpectedAmountEntity.productId=AssetEntity.productId AND CASE WHEN ExpectedAmountEntity.contractNumber IS NOT NULL THEN AssetEntity.contractNumber=ExpectedAmountEntity.contractNumber ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.shipmentNumber IS NOT NULL THEN AssetEntity.shipmentNumber=ExpectedAmountEntity.shipmentNumber ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.conditionId IS NOT NULL THEN COALESCE(TraceEventEntity.conditionId, AssetEntity.conditionId)=ExpectedAmountEntity.conditionId ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.rackLocationId IS NOT NULL THEN COALESCE(TraceEventEntity.rackLocationId, AssetEntity.rackLocationId)=ExpectedAmountEntity.rackLocationId ELSE 1=1 END) ELSE 1 END FROM TaskEntity WHERE TaskEntity.id=:taskId) AS expectedInOrder, TraceEventEntity.consumed, TraceEventEntity.checkedForOutbound, AssetEntity.makeUpLossFt, COALESCE(TraceEventEntity.conditionId, AssetEntity.conditionId) AS conditionId, AssetEntity.shipmentNumber, AssetEntity.contractNumber, AssetEntity.projectId, TraceEventEntity.submitStatus FROM AssetEntity LEFT JOIN TraceEventEntity ON TraceEventEntity.assetId=AssetEntity.id WHERE TraceEventEntity.taskId=:taskId AND TraceEventEntity.checkedForOutbound AND TraceEventEntity.submitStatus='0_NOT_SUBMITTED' ORDER BY TraceEventEntity.scannedAt DESC"
    )
    fun getNotSubmittedAssetsForTask(taskId: String): Observable<List<Asset>>

    @Transaction
    @Query("SELECT AssetEntity.productId, AssetEntity.outerDiameter, AssetEntity.weight, AssetEntity.grade, AssetEntity.endFinish, AssetEntity.range, AssetEntity.commodity, AssetEntity.millWorkNumber, RackLocationEntity.id as rackId, RackLocationEntity.name as rackName, SUM(runningLength) as expectedLength, COUNT(AssetEntity.id) as joints  FROM AssetEntity JOIN TraceEventEntity ON TraceEventEntity.assetId=AssetEntity.id JOIN RackLocationEntity ON TraceEventEntity.rackLocationId=RackLocationEntity.id WHERE TraceEventEntity.taskId=:taskId GROUP BY TraceEventEntity.rackLocationId, AssetEntity.millWorkNumber, AssetEntity.productId ")
    fun getRackTransfersFromTraceEventAndAsset(taskId: String): Observable<List<RackTransferData>>

    @Transaction
    @Query("SELECT SUM(length) AS total, 0 AS totalConsumed, 0 AS totalRejected, SUM(makeUpLossFt) AS totalMakeUpLoss, COUNT(id) AS totalJoints, 0 AS consumedJoints, 0 AS rejectedJoints, 0 AS totalConsumedRunningLength, SUM(runningLength) AS totalRunningLength FROM AssetEntity WHERE id IN (:assetIds)")
    fun getAssetsTallies(assetIds: List<String>): Observable<Tallies>

    @Transaction
    @Query("SELECT SUM(CASE WHEN TraceEventEntity.laserLength IS NOT NULL THEN TraceEventEntity.laserLength ELSE AssetEntity.length END) AS total, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN AssetEntity.length ELSE 0 END) AS totalConsumed, SUM(CASE WHEN TraceEventEntity.consumed IS 0 THEN AssetEntity.length ELSE 0 END) AS totalRejected, SUM(AssetEntity.makeUpLossFt) AS totalMakeUpLoss, COUNT(AssetEntity.id) AS totalJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN 1 ELSE 0 END) AS consumedJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 0 THEN 1 ELSE 0 END) AS rejectedJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN AssetEntity.runningLength ELSE 0 END) AS totalConsumedRunningLength, SUM(AssetEntity.runningLength) AS totalRunningLength FROM AssetEntity LEFT JOIN TraceEventEntity ON AssetEntity.id=TraceEventEntity.assetId WHERE TraceEventEntity.taskId=:taskId AND TraceEventEntity.checkedForOutbound")
    fun getTaskTallies(taskId: String): Observable<Tallies>

    @Transaction
    @Query("SELECT SUM(CASE WHEN TraceEventEntity.laserLength IS NOT NULL THEN TraceEventEntity.laserLength ELSE AssetEntity.length END) AS total, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN AssetEntity.length ELSE 0 END) AS totalConsumed, SUM(CASE WHEN TraceEventEntity.consumed IS 0 THEN AssetEntity.length ELSE 0 END) AS totalRejected, SUM(AssetEntity.makeUpLossFt) AS totalMakeUpLoss, COUNT(AssetEntity.id) AS totalJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN 1 ELSE 0 END) AS consumedJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 0 THEN 1 ELSE 0 END) AS rejectedJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN AssetEntity.runningLength ELSE 0 END) AS totalConsumedRunningLength, SUM(AssetEntity.runningLength) AS totalRunningLength FROM AssetEntity LEFT JOIN TraceEventEntity ON AssetEntity.id=TraceEventEntity.assetId WHERE TraceEventEntity.taskId=:taskId AND TraceEventEntity.checkedForOutbound AND TraceEventEntity.submitStatus IS '0_NOT_SUBMITTED'")
    fun getSessionTallies(taskId: String): Observable<Tallies>

    @Transaction
    @Query("SELECT SUM(CASE WHEN TraceEventEntity.laserLength IS NOT NULL THEN TraceEventEntity.laserLength ELSE AssetEntity.length END) AS total, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN AssetEntity.length ELSE 0 END) AS totalConsumed, SUM(CASE WHEN TraceEventEntity.consumed IS 0 THEN AssetEntity.length ELSE 0 END) AS totalRejected, SUM(AssetEntity.makeUpLossFt) AS totalMakeUpLoss, COUNT(AssetEntity.id) AS totalJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN 1 ELSE 0 END) AS consumedJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 0 THEN 1 ELSE 0 END) AS rejectedJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN AssetEntity.runningLength ELSE 0 END) AS totalConsumedRunningLength, SUM(AssetEntity.runningLength) AS totalRunningLength FROM AssetEntity LEFT JOIN TraceEventEntity ON AssetEntity.id=TraceEventEntity.assetId WHERE TraceEventEntity.taskId=:taskId AND TraceEventEntity.checkedForOutbound")
    fun getTaskTalliesSingle(taskId: String): Single<Tallies>

    @Transaction
    @Query("SELECT SUM(CASE WHEN TraceEventEntity.laserLength IS NOT NULL THEN TraceEventEntity.laserLength ELSE AssetEntity.length END) AS total, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN AssetEntity.length ELSE 0 END) AS totalConsumed, SUM(CASE WHEN TraceEventEntity.consumed IS 0 THEN AssetEntity.length ELSE 0 END) AS totalRejected, SUM(AssetEntity.makeUpLossFt) AS totalMakeUpLoss, COUNT(AssetEntity.id) AS totalJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN 1 ELSE 0 END) AS consumedJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 0 THEN 1 ELSE 0 END) AS rejectedJoints, SUM(CASE WHEN TraceEventEntity.consumed IS 1 THEN AssetEntity.runningLength ELSE 0 END) AS totalConsumedRunningLength, SUM(AssetEntity.runningLength) AS totalRunningLength FROM AssetEntity LEFT JOIN TraceEventEntity ON AssetEntity.id=TraceEventEntity.assetId WHERE TraceEventEntity.taskId=:taskId AND TraceEventEntity.checkedForOutbound AND TraceEventEntity.submitStatus IS '0_NOT_SUBMITTED'")
    fun getSessionTalliesSingle(taskId: String): Single<Tallies>
}
