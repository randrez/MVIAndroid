package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.database.model.ProjectConditionEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ProjectConditionsDao : BaseDao<ProjectConditionEntity> {
    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM ProjectConditionEntity WHERE conditionId=:conditionId AND projectId=:projectId)")
    fun hasCondition(conditionId: String, projectId: String): Single<Boolean>

    @Transaction
    @Query("DELETE FROM ProjectConditionEntity WHERE projectId IN (:projectIds)")
    fun deleteByProjectIds(projectIds: List<String>): Completable
}