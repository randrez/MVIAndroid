package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.TaskStatus.COMPLETED
import com.scgts.sctrace.base.util.toSQLString
import com.scgts.sctrace.database.model.TaskEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface TasksDao : BaseDao<TaskEntity> {
    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE id=:id")
    fun getById(id: String): Single<TaskEntity>

    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE id=:id")
    fun getByIdObs(id: String): Observable<TaskEntity>

    @Transaction
    @Query("SELECT count(*) FROM TaskEntity")
    fun getCount(): Single<Int>

    @Transaction
    @Query("SELECT COUNT(*) FROM TaskEntity WHERE status IS NOT '4_COMPLETED'")
    fun getCountObs(): Observable<Int>

    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE status IS NOT '4_COMPLETED'")
    fun getAllObs(): Observable<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE status IS NOT '4_COMPLETED'")
    fun getAllTasks(): Single<List<TaskEntity>>

    @RawQuery
    fun getFilterAndSortedTasksRawQuery(query: SupportSQLiteQuery): Single<List<TaskEntity>>

    fun getFilterAndSortedTasks(filters: TasksFilterAndSort): Single<List<TaskEntity>> {
        var query = "SELECT * FROM TaskEntity WHERE status IS NOT '${COMPLETED.dbName}'"
        query += addFilters(filters)
        query += " ORDER BY ${filters.sortCategory.serverName}"
        return getFilterAndSortedTasksRawQuery(SimpleSQLiteQuery(query))
    }

    @RawQuery
    fun getTasksCountByProjectRawQuery(query: SupportSQLiteQuery): Single<List<ProjectWithNumOfTasks>>

    fun getTasksCountByProject(filters: TasksFilterAndSort): Single<List<ProjectWithNumOfTasks>> {
        var query =
            "SELECT ProjectEntity.id AS id, ProjectEntity.name AS name, COUNT(TaskEntity.id) AS numOfTasks " +
                    "FROM ProjectEntity LEFT JOIN TaskEntity ON ProjectEntity.id = TaskEntity.projectId " +
                    "AND TaskEntity.status IS NOT '${COMPLETED.dbName}'"
        query += addFilters(filters)
        query += " GROUP BY ProjectEntity.id ORDER BY ProjectEntity.name"
        return getTasksCountByProjectRawQuery(SimpleSQLiteQuery(query))
    }

    @RawQuery
    fun getTasksCountByTaskTypeRawQuery(query: SupportSQLiteQuery): Single<List<TaskTypeWithNumOfTasks>>

    fun getTasksCountByTaskType(filters: TasksFilterAndSort): Single<List<TaskTypeWithNumOfTasks>> {
        var query =
            "SELECT typeForFiltering as type, COUNT(id) AS numOfTasks FROM TaskEntity WHERE status IS NOT '${COMPLETED.dbName}'"
        query += addFilters(filters)
        query += " GROUP BY typeForFiltering ORDER BY typeForFiltering"
        return getTasksCountByTaskTypeRawQuery(SimpleSQLiteQuery(query))
    }

    @RawQuery
    fun getTasksCountByTaskStatusRawQuery(query: SupportSQLiteQuery): Single<List<TaskStatusWithNumOfTasks>>

    fun getTasksCountByTaskStatus(filters: TasksFilterAndSort): Single<List<TaskStatusWithNumOfTasks>> {
        var query =
            "SELECT status, COUNT(id) AS numOfTasks FROM TaskEntity WHERE status IS NOT '${COMPLETED.dbName}'"
        query += addFilters(filters)
        query += " GROUP BY status ORDER BY status"
        return getTasksCountByTaskStatusRawQuery(SimpleSQLiteQuery(query))
    }

    @RawQuery
    fun getTasksCountByFacilityRawQuery(query: SupportSQLiteQuery): Single<List<FacilityWithNumOfTasks>>

    fun getTasksCountByFacility(
        fromLocation: Boolean,
        filters: TasksFilterAndSort,
    ): Single<List<FacilityWithNumOfTasks>> {
        val destination = if (fromLocation) "fromLocationId" else "toLocationId"
        var query =
            "SELECT FacilityEntity.*, COUNT(TaskEntity.id) AS numOfTasks FROM FacilityEntity " +
                    "LEFT JOIN TaskEntity ON FacilityEntity.id = TaskEntity.$destination " +
                    "AND TaskEntity.status IS NOT '${COMPLETED.dbName}'"
        query += addFilters(filters)
        if (destination == "toLocationId") query += " WHERE FacilityEntity.facilityType IS NOT 0"
        query += " GROUP BY FacilityEntity.id ORDER BY FacilityEntity.facilityType, FacilityEntity.name"
        return getTasksCountByFacilityRawQuery(SimpleSQLiteQuery(query))
    }

    @Transaction
    @Query("SELECT * FROM TaskEntity")
    fun getAllSingle(): Single<List<TaskEntity>>

    @Transaction
    @Query("DELETE FROM TaskEntity WHERE type NOT IN (:types) AND id NOT IN (:ids)")
    fun deleteAllExcept(ids: List<String>, types: List<TaskType>): Completable

    @Transaction
    @Query("DELETE FROM TaskEntity WHERE id=:id")
    fun deleteById(id: String): Completable

    @Transaction
    @Query("DELETE FROM TaskEntity WHERE projectId IN (:projectIds)")
    fun deleteByProjectsIds(projectIds: List<String>): Completable

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM TaskEntity WHERE id=:id)")
    fun hasItem(id: String): Single<Boolean>

    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE id IN (:taskIds)")
    fun getTasksForIds(taskIds: List<String>): Single<List<TaskEntity>>

    @Transaction
    @Query("UPDATE taskEntity SET defaultRackLocationId=:defaultFacilityLocationId WHERE id=:taskId")
    fun updateDefaultRackLocation(taskId: String, defaultFacilityLocationId: String): Completable

    @Transaction
    @Query("UPDATE TaskEntity SET id=:newId WHERE id=:id")
    fun updateId(id: String, newId: String): Completable

    @Transaction
    @Query("UPDATE TaskEntity SET status=:newStatus WHERE id=:id")
    fun updateStatus(id: String, newStatus: TaskStatus): Completable

    @Transaction
    @Query("UPDATE TaskEntity SET defaultRackLocationId=NULL WHERE defaultRackLocationId=:rackLocationId")
    fun updateDefaultRackLocationToNull(rackLocationId: String): Completable

    @Transaction
    @Query("DELETE FROM TaskEntity WHERE type=:type")
    fun deleteTaskByType(type: TaskType): Completable

    @Transaction
    @Query("SELECT dispatchDate FROM TaskEntity WHERE id=:taskId")
    fun getDispatchDate(taskId: String): Single<String>

    private fun addFilters(filters: TasksFilterAndSort): String {
        var query = ""
        if (filters.projectFilter.isNotEmpty()) query += " AND TaskEntity.projectId IN (${filters.projectFilter.toSQLString()})"
        if (filters.taskFilter.isNotEmpty()) query += " AND TaskEntity.typeForFiltering IN (${filters.taskFilter.toSQLString()})"
        if (filters.statusFilter.isNotEmpty()) query += " AND TaskEntity.status IN (${filters.statusFilter.toSQLString()})"
        if (filters.fromFilter.isNotEmpty()) query += " AND TaskEntity.fromLocationId IN (${filters.fromFilter.toSQLString()})"
        if (filters.toFilter.isNotEmpty()) query += " AND TaskEntity.toLocationId IN (${filters.toFilter.toSQLString()})"
        return query
    }
}
