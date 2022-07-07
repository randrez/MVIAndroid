package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.base.model.AssetProductInformation
import com.scgts.sctrace.database.model.ExpectedAmountEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface ExpectedAmountsDao : BaseDao<ExpectedAmountEntity> {

    /**
     *
     *      This is what the SQL Query for getAssetProductionInformations looks like when
     *      broken down into multiple lines instead of written in a single line:
     *
     *      SELECT
     *          ExpectedAmountEntity.productDescription,
     *          ExpectedAmountEntity.expectedJoints,
     *          ExpectedAmountEntity.expectedTally,
     *          (SELECT COUNT(TraceEventEntity.assetId) FROM TraceEventEntity
     *              LEFT JOIN AssetEntity ON TraceEventEntity.assetId=AssetEntity.id            // This will get the number of Trace Events
     *              WHERE AssetEntity.productId=ExpectedAmountEntity.productId                  // with matching product ID
     *                  AND CASE WHEN ExpectedAmountEntity.contractNumber IS NOT NULL           // and matching contract number if contract number is not null
     *                      THEN AssetEntity.contractNumber=ExpectedAmountEntity.contractNumber
     *                      ELSE 1=1 END
     *                  AND CASE WHEN ExpectedAmountEntity.shipmentNumber IS NOT NULL           // and matching shipping number if shipping number is not null
     *                      THEN AssetEntity.shipmentNumber=ExpectedAmountEntity.shipmentNumber
     *                      ELSE 1=1 END
     *                  AND CASE WHEN ExpectedAmountEntity.conditionId IS NOT NULL              // and matching condition ID if condition ID is not null
     *                      THEN COALESCE(TraceEventEntity.conditionId, AssetEntity.conditionId)=ExpectedAmountEntity.conditionId
     *                      ELSE 1=1 END
     *                  AND CASE WHEN ExpectedAmountEntity.rackLocationId IS NOT NULL           // and matching rack location ID if rack location is not null
     *                      THEN COALESCE(TraceEventEntity.rackLocationId, AssetEntity.rackLocationId)=ExpectedAmountEntity.rackLocationId
     *                      ELSE 1=1 END
     *                  AND TraceEventEntity.taskId=:taskId
     *          ) AS capturedJoints,
     *          (SELECT COALESCE(SUM(COALESCE(TraceEventEntity.laserLength, AssetEntity.length)), 0) FROM TraceEventEntity
     *              LEFT JOIN AssetEntity ON TraceEventEntity.assetId=AssetEntity.id            // This will get the sum length of the Trace Events
     *              WHERE AssetEntity.productId=ExpectedAmountEntity.productId                  // with matching product ID
     *                  AND CASE WHEN ExpectedAmountEntity.contractNumber IS NOT NULL           // and matching contract number if contract number is not null
     *                      THEN AssetEntity.contractNumber=ExpectedAmountEntity.contractNumber
     *                      ELSE 1=1 END
     *                  AND CASE WHEN ExpectedAmountEntity.shipmentNumber IS NOT NULL           // and matching shipping number if shipping number is not null
     *                      THEN AssetEntity.shipmentNumber=ExpectedAmountEntity.shipmentNumber
     *                      ELSE 1=1 END
     *                  AND CASE WHEN ExpectedAmountEntity.conditionId IS NOT NULL              // and matching condition ID if condition ID is not null
     *                      THEN COALESCE(TraceEventEntity.conditionId, AssetEntity.conditionId)=ExpectedAmountEntity.conditionId
     *                      ELSE 1=1 END
     *                  AND CASE WHEN ExpectedAmountEntity.rackLocationId IS NOT NULL           // and matching rack location ID if rack location is not null
     *                      THEN COALESCE(TraceEventEntity.rackLocationId, AssetEntity.rackLocationId)=ExpectedAmountEntity.rackLocationId
     *                      ELSE 1=1 END
     *                  AND TraceEventEntity.taskId=:taskId
     *          ) AS capturedTally,
     *          ExpectedAmountEntity.contractNumber,
     *          ExpectedAmountEntity.shipmentNumber,
     *          (SELECT name FROM ConditionEntity WHERE ConditionEntity.id=ExpectedAmountEntity.conditionId) AS conditionName,
     *          (SELECT name FROM RackLocationEntity WHERE RackLocationEntity.id=ExpectedAmountEntity.rackLocationId) AS rackLocationName
     *      FROM ExpectedAmountEntity
     *      WHERE ExpectedAmountEntity.orderId=:orderId
     *      GROUP BY ExpectedAmountEntity.productId, ExpectedAmountEntity.contractNumber, ExpectedAmountEntity.shipmentNumber, ExpectedAmountEntity.conditionId, ExpectedAmountEntity.rackLocationId
     *
     *
     *      NOTES: The getAssetProductInformations get batches of assets that is grouped by Product ID, Contract No., Shipment No., Condition, and Rack Location
     *             as is defined in the expected amount of the order.
     */
    @Transaction
    @Query(
        "SELECT ExpectedAmountEntity.productDescription, ExpectedAmountEntity.expectedJoints, ExpectedAmountEntity.expectedTally, (SELECT COUNT(TraceEventEntity.assetId) FROM TraceEventEntity LEFT JOIN AssetEntity ON TraceEventEntity.assetId=AssetEntity.id WHERE AssetEntity.productId=ExpectedAmountEntity.productId AND CASE WHEN ExpectedAmountEntity.contractNumber IS NOT NULL THEN AssetEntity.contractNumber=ExpectedAmountEntity.contractNumber ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.shipmentNumber IS NOT NULL THEN AssetEntity.shipmentNumber=ExpectedAmountEntity.shipmentNumber ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.conditionId IS NOT NULL THEN COALESCE(TraceEventEntity.conditionId, AssetEntity.conditionId)=ExpectedAmountEntity.conditionId ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.rackLocationId IS NOT NULL THEN COALESCE(TraceEventEntity.rackLocationId, AssetEntity.rackLocationId)=ExpectedAmountEntity.rackLocationId ELSE 1=1 END AND TraceEventEntity.taskId=:taskId) AS capturedJoints, (SELECT COALESCE(SUM(COALESCE(TraceEventEntity.laserLength, AssetEntity.length)), 0) FROM TraceEventEntity LEFT JOIN AssetEntity ON TraceEventEntity.assetId=AssetEntity.id WHERE AssetEntity.productId=ExpectedAmountEntity.productId AND CASE WHEN ExpectedAmountEntity.contractNumber IS NOT NULL THEN AssetEntity.contractNumber=ExpectedAmountEntity.contractNumber ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.shipmentNumber IS NOT NULL THEN AssetEntity.shipmentNumber=ExpectedAmountEntity.shipmentNumber ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.conditionId IS NOT NULL THEN COALESCE(TraceEventEntity.conditionId, AssetEntity.conditionId)=ExpectedAmountEntity.conditionId ELSE 1=1 END AND CASE WHEN ExpectedAmountEntity.rackLocationId IS NOT NULL THEN COALESCE(TraceEventEntity.rackLocationId, AssetEntity.rackLocationId)=ExpectedAmountEntity.rackLocationId ELSE 1=1 END AND TraceEventEntity.taskId=:taskId) AS capturedTally, ExpectedAmountEntity.contractNumber, ExpectedAmountEntity.shipmentNumber, (SELECT name FROM ConditionEntity WHERE ConditionEntity.id=ExpectedAmountEntity.conditionId) AS conditionName, (SELECT name FROM RackLocationEntity WHERE RackLocationEntity.id=ExpectedAmountEntity.rackLocationId) AS rackLocationName FROM ExpectedAmountEntity WHERE ExpectedAmountEntity.orderId=:orderId GROUP BY ExpectedAmountEntity.productId, ExpectedAmountEntity.contractNumber, ExpectedAmountEntity.shipmentNumber, ExpectedAmountEntity.conditionId, ExpectedAmountEntity.rackLocationId"
    )
    fun getAssetProductInformations(
        orderId: String,
        taskId: String
    ): Observable<List<AssetProductInformation>>

    @Transaction
    @Query("SELECT * FROM ExpectedAmountEntity WHERE orderId=:orderId")
    fun getExpectedAmountsForOrder(orderId: String): Single<List<ExpectedAmountEntity>>

    @Transaction
    @Query("SELECT EXISTS(SELECT productId FROM ExpectedAmountEntity WHERE orderId=:orderId AND productId=:productVariant)")
    fun productVariantExpectedInOrder(orderId: String, productVariant: String): Single<Boolean>

    @Transaction
    @Query("SELECT * FROM ExpectedAmountEntity WHERE orderId=:orderId AND productId=:productId")
    fun getExpectedAmountForOrderAndProduct(
        orderId: String,
        productId: String,
    ): Single<List<ExpectedAmountEntity>>

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM ExpectedAmountEntity WHERE orderId=:orderId AND productId=:productId AND contractNumber=:contractNumber AND shipmentNumber=:shipmentNumber AND contractNumber=:conditionId AND rackLocationId=:rackLocationId)")
    fun hasExpectedAmountWith(
        orderId: String,
        productId: String,
        contractNumber: String,
        shipmentNumber: String,
        conditionId: String,
        rackLocationId: String,
    ): Single<Boolean>

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM ExpectedAmountEntity WHERE orderId=:orderId AND productId=:productId AND contractNumber=:contractNumber)")
    fun hasExpectedAmountContractNumber(
        orderId: String,
        productId: String,
        contractNumber: String,
    ): Single<Boolean>

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM ExpectedAmountEntity WHERE orderId=:orderId AND productId=:productId AND shipmentNumber=:shipmentNumber)")
    fun haxExpectedAmountShipmentNumber(
        orderId: String,
        productId: String,
        shipmentNumber: String,
    ): Single<Boolean>

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM ExpectedAmountEntity WHERE orderId=:orderId AND productId=:productId AND conditionId=:conditionId)")
    fun hasExpectedAmountCondition(
        orderId: String,
        productId: String,
        conditionId: String,
    ): Single<Boolean>

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM ExpectedAmountEntity WHERE orderId=:orderId AND productId=:productId AND rackLocationId=:rackLocationId)")
    fun hasExpectedAmountRackLocation(
        orderId: String,
        productId: String,
        rackLocationId: String,
    ): Single<Boolean>

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM ExpectedAmountEntity WHERE orderId=:orderId AND productId=:productId AND conditionId!=:conditionId AND rackLocationId!=:rackLocationId)")
    fun differentExpectedAmountConditionAndRackLocation(
        orderId: String,
        productId: String,
        conditionId: String,
        rackLocationId: String,
    ): Single<Boolean>

    @Transaction()
    @Query("DELETE FROM ExpectedAmountEntity WHERE id NOT IN (:ids)")
    fun deleteAllExcept(ids: List<String>): Completable
}