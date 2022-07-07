package com.scgts.sctrace.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.scgts.sctrace.database.model.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

/**
 * This is just to store data about the logged in user and there should only be one entry
 */
@Dao
interface UserDao : BaseDao<UserEntity> {
    @Transaction
    @Query("SELECT * FROM UserEntity")
    fun getUser(): Observable<UserEntity>

    @Transaction
    @Query("DELETE FROM UserEntity")
    fun deleteUser(): Completable
}