package com.scgts.sctrace.user

import RolesQuery
import com.scgts.sctrace.base.model.User
import com.scgts.sctrace.base.model.UserRole
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface UserRepository {
    fun getUser(): Observable<User>

    fun getUserRolesForAllProject(): Observable<UserRole>

    fun getUserRolesByProject(projectId: String): Single<UserRole>

    fun storeUserInfo(user: User): Completable

    fun updateUserRoles(projects: List<RolesQuery.Project_role>): Completable

    fun clearUser(): Completable
}