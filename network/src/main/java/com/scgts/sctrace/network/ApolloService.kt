package com.scgts.sctrace.network

import ProjectsQuery
import RolesQuery
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.rx3.Rx3Apollo
import com.scgts.sctrace.base.LoginCredentialException
import com.scgts.sctrace.base.auth.AuthManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

interface ApolloService {
    fun getProjects(): Single<List<ProjectsQuery.GetMobileUserProject>>
    fun pushTraceEvent(
        taskId: String,
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        facilityId: String,
        rackLocationId: String?,
        consumptionStatus: String? = null,
        rejectReason: String? = null,
        rejectComment: String? = null,
        tags: List<String>? = null,
        laserLength: Double? = null,
        userId: String,
        userName: String
    ): Completable

    fun quickInboundFromMill(
        taskId: String,
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        toLocationId: String,
        facilityId: String,
        rackLocationId: String?,
        tags: List<String>? = null,
        laserLength: Double? = null,
        userId: String,
        userName: String
    ): Completable

    fun quickInboundTally(
        taskId: String,
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        toLocationId: String,
        facilityId: String,
        rackLocationId: String?,
        tags: List<String>?,
        laserLength: Double? = null,
        userId: String,
        userName: String
    ): Completable

    fun quickInboundToWell(
        taskId: String,
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        toLocationId: String,
        facilityId: String,
        rackLocationId: String?,
        tags: List<String>?,
        userId: String,
        userName: String
    ): Completable

    fun quickDispatchReturn(
        taskId: String,
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        toLocationId: String,
        fromLocationId: String,
        facilityId: String,
        rackLocationId: String?,
        dispatchDate: String,
        tags: List<String>?,
        userId: String,
        userName: String
    ): Completable

    fun quickDispatchTransfer(
        taskId: String,
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        toLocationId: String,
        fromLocationId: String,
        facilityId: String,
        rackLocationId: String?,
        dispatchDate: String,
        tags: List<String>?,
        userId: String,
        userName: String
    ): Completable

    fun pushAdHocTraceEvent(
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        rackLocationId: String?,
        tags: List<String>? = null,
        laserLength: Double? = null,
        userId: String,
        userName: String
    ): Completable

    fun pushQuickRejectEvent(
        taskId: String,
        assetId: String,
        scannedAt: String,
        consumptionStatus: String,
        rejectReason: String? = null,
        rejectComment: String? = null,
        tags: List<String>?,
        userId: String,
        userName: String
    ): Completable

    fun pushDeleteTag(assetId: String, tag: String): Completable

    fun pushFeedback(content: String, email: String, tags: List<String>): Completable

    fun getRoles(): Single<List<RolesQuery.Project_role>>

    fun pushAdHocRackTransfer(
        assetId: String,
        taskId: String,
        scannedAt: String,
        facilityId: String,
        rackLocationId: String,
        userId: String,
        userName: String
    ):Completable

}

fun <D : Operation.Data, T, V : Operation.Variables, F> ApolloClient.querySingle(
    query: Query<D, T, V>,
    authManager: AuthManager,
    dataExtract: (T) -> F
): Single<F> {
    return rxApolloWithErrorHandling(this.query(query), authManager)
        .map { dataExtract(it) }
        .singleElement()
        .toSingle()
        .subscribeOn(Schedulers.io())
}

fun <D : Operation.Data, T, V : Operation.Variables, F> ApolloClient.mutationSingle(
    mutation: Mutation<D, T, V>,
    authManager: AuthManager,
    dataExtract: (T) -> F,
): Single<F> {
    return rxApolloWithErrorHandling(this.mutate(mutation), authManager)
        .map { dataExtract(it) }
        .singleElement()
        .toSingle()
        .subscribeOn(Schedulers.io())
}

fun <T> rxApolloWithErrorHandling(call: ApolloCall<T>, authManager: AuthManager): Observable<T> {
    return Rx3Apollo.from(call)
        .flatMap {
            if (it.hasErrors()) {
                // this block handles if errors come from inside graphql response
                // 1. parsing error from the call response based on message
                // 2. wrap with custom error type based on how you want to handle them down the stream
                // example: GraphQlException
                when (it.errors?.first()?.message) {
                    "The credentials provided were invalid." ->
                        Observable.error(LoginCredentialException)
                    else -> Observable.error<T>(GraphQlException(it.errors?.first()?.message, null))
                }
            } else {
                Observable.just(it.data!!)
            }
        }
        .onErrorResumeNext { error: Throwable ->
            when (error) {
                !is ApolloHttpException -> Observable.error(error)
                // implement custom error handling for all apollo call errors
                // example: error.code() == 404 -> Custom404Exception
                else -> authManager.notifyLoggedOut()
                    .andThen(Observable.error(GraphQlException("something went wrong", null)))
            }
        }
}
