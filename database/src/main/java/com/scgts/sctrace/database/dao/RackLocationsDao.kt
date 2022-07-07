package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.database.model.RackLocationEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface RackLocationsDao : BaseDao<RackLocationEntity> {
    @Transaction
    @Query("UPDATE RackLocationEntity SET selectable=0 WHERE id=:id")
    fun setRackLocationSelectabilityToFalse(id: String): Completable

    @Transaction
    @Query("DELETE FROM RackLocationEntity WHERE id IN (:ids)")
    fun deleteByIds(ids: List<String>): Completable

    @Transaction
    @Query("SELECT * FROM RackLocationEntity")
    fun getAllRackLocations(): Single<List<RackLocationEntity>>

    @Transaction
    @Query("SELECT * FROM RackLocationEntity WHERE facilityId=:facilityId AND selectable ORDER BY name ASC")
    fun getRackLocationsForFacility(facilityId: String): Single<List<RackLocationEntity>>

    @Transaction
    @Query("SELECT rle.* FROM RackLocationEntity rle LEFT JOIN FacilityEntity fe ON rle.facilityId=fe.id LEFT JOIN ProjectFacilityEntity pfe ON fe.id=pfe.facilityId WHERE projectId=:projectId AND rle.selectable ORDER BY rle.name ASC")
    fun getRackLocationsByProject(projectId: String): Single<List<RackLocationEntity>>

    @Transaction
    @Query("SELECT * FROM RackLocationEntity WHERE id=:id")
    fun getRackLocationById(id: String): Single<RackLocationEntity>

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM RackLocationEntity WHERE id=:id)")
    fun hasRackLocation(id: String): Single<Boolean>

    // The following Query checks if the Rack Location is linked to any Trace Event, Asset, or Expected Amount.
    @Transaction
    @Query("SELECT EXISTS(SELECT rackLocationId FROM TraceEventEntity WHERE rackLocationId=:rackId UNION SELECT rackLocationId FROM AssetEntity WHERE rackLocationId=:rackId UNION SELECT rackLocationId FROM ExpectedAmountEntity WHERE rackLocationId=:rackId)")
    fun checkRackAssociation(rackId: String): Single<Boolean>
}