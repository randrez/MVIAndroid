package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.database.model.ConditionEntity
import io.reactivex.rxjava3.core.Single

@Dao
interface ConditionsDao : BaseDao<ConditionEntity> {
    @Transaction
    @Query("SELECT * FROM ConditionEntity")
    fun getAllConditions(): Single<List<ConditionEntity>>

    @Transaction
    @Query("SELECT * FROM ConditionEntity LEFT JOIN ProjectConditionEntity ON id=conditionId WHERE projectId=:projectId ORDER BY name ASC")
    fun getConditionsByProject(projectId: String): Single<List<ConditionEntity>>

    @Transaction
    @Query("SELECT * FROM ConditionEntity LEFT JOIN ProjectConditionEntity ON id=conditionId WHERE id=:conditionId AND projectId=:projectId")
    fun getConditionByIdAndProjectId(
        conditionId: String,
        projectId: String,
    ): Single<ConditionEntity>

    @Transaction
    @Query("SELECT * FROM ConditionEntity WHERE name=:name")
    fun getConditionByName(name: String): Single<ConditionEntity>

    @Transaction
    @Query("SELECT * FROM ConditionEntity LEFT JOIN ProjectConditionEntity ON id=conditionId WHERE projectId=:projectId ORDER BY conditionCode ASC LIMIT 1")
    fun getDefaultCondition(projectId: String): Single<ConditionEntity>

    @Transaction
    @Query("SELECT EXISTS( SELECT * FROM ConditionEntity WHERE id=:conditionId)")
    fun hasCondition(conditionId: String): Single<Boolean>

    @Transaction
    @Query("SELECT * FROM ConditionEntity WHERE id=:conditionId")
    fun getConditionById(conditionId: String): Single<ConditionEntity>
}