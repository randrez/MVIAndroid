package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.database.model.UserProjectRoleEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface UserProjectRolesDao : BaseDao<UserProjectRoleEntity> {
    @Transaction
    @Query("SELECT * FROM UserProjectRoleEntity")
    fun getAll(): Observable<List<UserProjectRoleEntity>>

    @Transaction
    @Query("SELECT * FROM UserProjectRoleEntity WHERE projectId=:id")
    fun getByProject(id: String): Single<UserProjectRoleEntity>

    @Transaction
    @Query("DELETE FROM UserProjectRoleEntity")
    fun deleteAll(): Completable
}