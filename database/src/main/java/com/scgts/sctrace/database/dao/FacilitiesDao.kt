package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.database.model.FacilityEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface FacilitiesDao : BaseDao<FacilityEntity> {
    @Transaction
    @Query("UPDATE FacilityEntity SET selectable=0 WHERE id=:id")
    fun setFacilitySelectabilityToFalse(id: String): Completable

    @Transaction
    @Query("DELETE FROM FacilityEntity WHERE id IN (:ids)")
    fun deleteByIds(ids: List<String>): Completable

    @Transaction
    @Query("SELECT * FROM FacilityEntity WHERE id=:id")
    fun getFacilityById(id: String): Single<FacilityEntity>

    @Transaction
    @Query("SELECT * FROM FacilityEntity")
    fun getAllFacilities(): Single<List<FacilityEntity>>

    @Transaction
    @Query("SELECT * FROM FacilityEntity LEFT JOIN ProjectFacilityEntity ON id=facilityId WHERE projectId=:projectId AND facilityType=1 AND selectable ORDER BY name ASC")
    fun getYardsByProject(projectId: String): Single<List<FacilityEntity>>

    @Transaction
    @Query("SELECT * FROM FacilityEntity LEFT JOIN ProjectFacilityEntity ON id=facilityId WHERE projectId=:projectId AND facilityType=3 AND selectable ORDER BY name ASC")
    fun getWellsByProject(projectId: String): Single<List<FacilityEntity>>

    @Transaction
    @Query("SELECT * FROM FacilityEntity LEFT JOIN ProjectFacilityEntity ON id=facilityId WHERE projectId=:projectId AND facilityType=2 AND selectable ORDER BY name ASC")
    fun getRigsByProject(projectId: String): Single<List<FacilityEntity>>

    // The following Query checks if the facility is linked to any Trace Event or Task
    @Transaction
    @Query("SELECT EXISTS(SELECT taskId FROM TraceEventEntity WHERE facilityId=:facilityId OR toLocationId=:facilityId OR fromLocationId=:facilityId UNION SELECT id FROM TaskEntity WHERE toLocationId=:facilityId OR fromLocationId=:facilityId)")
    fun checkFacilityAssociation(facilityId: String): Single<Boolean>
}