package com.scgts.sctrace.user

import RolesQuery
import com.scgts.sctrace.base.model.User
import com.scgts.sctrace.base.model.UserRole
import com.scgts.sctrace.database.dao.UserDao
import com.scgts.sctrace.database.dao.UserProjectRolesDao
import com.scgts.sctrace.database.model.UserEntity
import com.scgts.sctrace.user.mappers.toEntity
import com.scgts.sctrace.user.mappers.toUiModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.flatMapIterable
import io.reactivex.rxjava3.schedulers.Schedulers

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val userProjectRolesDao: UserProjectRolesDao,
) : UserRepository {

    override fun getUser(): Observable<User> {
        return userDao.getUser().map { it.toUiModel() }.subscribeOn(Schedulers.io())
    }

    override fun getUserRolesForAllProject(): Observable<UserRole> {
        return userProjectRolesDao.getAll().map {
            it.fold(
                initial = UserRole(
                    isDrillingEngineer = false,
                    isYardOperator = false,
                    isAuditor = false
                )
            ) { acc, userProject ->
                UserRole(
                    isDrillingEngineer = acc.isDrillingEngineer || userProject.isDrillingEngineer,
                    isYardOperator = acc.isYardOperator || userProject.isYardOperator,
                    isAuditor = acc.isAuditor || userProject.isAuditor,
                )
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun getUserRolesByProject(projectId: String): Single<UserRole> {
        return userProjectRolesDao.getByProject(projectId).map { it.toUiModel() }
            .subscribeOn(Schedulers.io())
    }

    override fun storeUserInfo(user: User): Completable {
        return userDao.insert(
            UserEntity(
                id = user.id,
                name = user.name ?: "",
                email = user.email ?: ""
            )
        ).subscribeOn(Schedulers.io())
    }

    override fun updateUserRoles(projects: List<RolesQuery.Project_role>): Completable {
        return userProjectRolesDao.deleteAll().andThen(
            Observable.just(projects).flatMapIterable().flatMapCompletable { project ->
                userProjectRolesDao.insert(project.toEntity())
            }
        ).subscribeOn(Schedulers.io())
    }

    override fun clearUser(): Completable {
        return userDao.deleteUser().andThen(userProjectRolesDao.deleteAll())
            .subscribeOn(Schedulers.io())
    }
}
