package com.scgts.sctrace.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.rxjava3.core.Completable

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg items: T): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun insert(items: Iterable<T>): Completable

    @Update
    fun update(vararg items: T): Completable

    @Update
    @JvmSuppressWildcards
    fun update(items: Iterable<T>): Completable

    @Delete
    fun delete(vararg items: T): Completable

    @Delete
    @JvmSuppressWildcards
    fun delete(items: Iterable<T>): Completable
}
