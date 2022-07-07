package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.database.model.ProjectFacilityEntity
import io.reactivex.rxjava3.core.Completable

@Dao
interface ProjectFacilitiesDao : BaseDao<ProjectFacilityEntity> {
    @Transaction
    @Query("DELETE FROM ProjectFacilityEntity WHERE projectId IN (:projectIds)")
    fun deleteByProjectIds(projectIds: List<String>): Completable
}