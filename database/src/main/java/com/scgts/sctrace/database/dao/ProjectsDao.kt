package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.scgts.sctrace.database.model.ProjectEntity
import com.scgts.sctrace.database.model.ProjectPartialEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.threeten.bp.ZonedDateTime

@Dao
interface ProjectsDao : BaseDao<ProjectEntity> {
    @Update(entity = ProjectEntity::class)
    fun update(vararg items: ProjectPartialEntity): Completable

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM ProjectEntity WHERE id=:id)")
    fun hasProject(id: String): Boolean

    @Transaction
    @Query("SELECT * FROM ProjectEntity ORDER BY name ASC")
    fun getAllProjects(): Single<List<ProjectEntity>>

    @Transaction
    @Query("SELECT * FROM ProjectEntity WHERE id=:id")
    fun getProjectById(id: String): Single<ProjectEntity>

    @Transaction
    @Query("SELECT name FROM ProjectEntity WHERE id IN (:ids)")
    fun getProjectsName(ids: List<String>): Single<List<String>>

    @Transaction
    @Query("SELECT unitOfMeasure FROM ProjectEntity WHERE id=:id")
    fun getProjectUnitOfMeasure(id: String): Single<String>

    @Transaction
    @Query("UPDATE ProjectEntity SET lastUpdated=:lastUpdated WHERE id=:id")
    fun updateProjectLastUpdated(id: String, lastUpdated: ZonedDateTime): Completable

    @Transaction
    @Query("DELETE FROM ProjectEntity WHERE id IN (:ids)")
    fun deleteProjectsByIds(ids: List<String>): Completable
}
