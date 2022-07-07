package com.scgts.sctrace.network

import CreateFeedbackMutation
import DeleteTagMutation
import ProjectsQuery
import PushAdHocRackTransferTraceEventMutation
import PushAdHocTraceEventMutation
import PushQuickRejectTraceEventMutation
import PushTraceEventMutation
import QuickDispatchReturnMutation
import QuickDispatchTransferMutation
import QuickInboundFromMillMutation
import QuickInboundTallyMutation
import QuickInboundToWellMutation
import RolesQuery
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.scgts.sctrace.base.auth.AuthManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class ApolloServiceImpl(
    private val apolloClient: ApolloClient,
    private val authManager: AuthManager
) : ApolloService {

    override fun getProjects(): Single<List<ProjectsQuery.GetMobileUserProject>> {
        return apolloClient.querySingle(ProjectsQuery(), authManager) {
            it.getMobileUserProjects.filterNotNull()
        }
    }

    override fun pushTraceEvent(
        taskId: String,
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        facilityId: String,
        rackLocationId: String?,
        consumptionStatus: String?,
        rejectReason: String?,
        rejectComment: String?,
        tags: List<String>?,
        laserLength: Double?,
        userId: String,
        userName: String
    ): Completable {
        return apolloClient.mutationSingle(
            PushTraceEventMutation(
                taskId = taskId,
                assetId = assetId,
                conditionId = Input.optional(conditionId),
                scannedAt = scannedAt,
                rackLocationId = Input.optional(rackLocationId),
                consumptionStatus = Input.optional(consumptionStatus),
                rejectReason = Input.optional(rejectReason),
                rejectComment = Input.optional(rejectComment),
                facilityId = facilityId,
                tags = Input.optional(tags),
                laserLength = Input.optional(laserLength),
                userId = userId,
                userName = userName
            ), authManager
        ) { it.pushTraceEventToDb }.flatMapCompletable { Completable.complete() }
    }

    override fun quickInboundFromMill(
        taskId: String,
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        toLocationId: String,
        facilityId: String,
        rackLocationId: String?,
        tags: List<String>?,
        laserLength: Double?,
        userId: String,
        userName: String
    ): Completable {
        return apolloClient.mutationSingle(
            QuickInboundFromMillMutation(
                taskId = taskId,
                assetId = assetId,
                conditionId = Input.optional(conditionId),
                scannedAt = scannedAt,
                toLocationId = toLocationId,
                rackLocationId = Input.optional(rackLocationId),
                facilityId = facilityId,
                tags = Input.optional(tags),
                laserLength = Input.optional(laserLength),
                userId = userId,
                userName = userName
            ), authManager
        ) { it.quickInboundFromMill }.flatMapCompletable { Completable.complete() }
    }

    override fun quickInboundTally(
        taskId: String,
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        toLocationId: String,
        facilityId: String,
        rackLocationId: String?,
        tags: List<String>?,
        laserLength: Double?,
        userId: String,
        userName: String
    ): Completable {
        return apolloClient.mutationSingle(
            QuickInboundTallyMutation(
                taskId = taskId,
                assetId = assetId,
                conditionId = Input.optional(conditionId),
                scannedAt = scannedAt,
                toLocationId = toLocationId,
                rackLocationId = Input.optional(rackLocationId),
                facilityId = facilityId,
                tags = Input.optional(tags),
                laserLength = Input.optional(laserLength),
                userId = userId,
                userName = userName
            ), authManager
        ) { it.quickInboundTally }.flatMapCompletable { Completable.complete() }
    }

    override fun quickInboundToWell(
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
    ): Completable {
        return apolloClient.mutationSingle(
            QuickInboundToWellMutation(
                taskId = taskId,
                assetId = assetId,
                conditionId = Input.optional(conditionId),
                scannedAt = scannedAt,
                toLocationId = toLocationId,
                rackLocationId = Input.optional(rackLocationId),
                facilityId = facilityId,
                tags = Input.optional(tags),
                userId = userId,
                userName = userName
            ), authManager
        ) { it.quickInboundToWell }.flatMapCompletable { Completable.complete() }
    }

    override fun quickDispatchReturn(
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
    ): Completable {
        return apolloClient.mutationSingle(
            QuickDispatchReturnMutation(
                taskId = taskId,
                assetId = assetId,
                conditionId = Input.optional(conditionId),
                scannedAt = scannedAt,
                rackLocationId = Input.optional(rackLocationId),
                facilityId = facilityId,
                toLocationId = toLocationId,
                fromLocationId = fromLocationId,
                dispatchDate = dispatchDate,
                tags = Input.optional(tags),
                userId = userId,
                userName = userName
            ), authManager
        ) { it.quickDispatchReturn }.flatMapCompletable { Completable.complete() }
    }

    override fun quickDispatchTransfer(
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
    ): Completable {
        return apolloClient.mutationSingle(
            QuickDispatchTransferMutation(
                taskId = taskId,
                assetId = assetId,
                conditionId = Input.optional(conditionId),
                scannedAt = scannedAt,
                rackLocationId = Input.optional(rackLocationId),
                facilityId = facilityId,
                toLocationId = toLocationId,
                fromLocationId = fromLocationId,
                dispatchDate = dispatchDate,
                tags = Input.optional(tags),
                userId = userId,
                userName = userName
            ), authManager
        ) { it.quickDispatchTransfer }.flatMapCompletable { Completable.complete() }
    }

    override fun getRoles(): Single<List<RolesQuery.Project_role>> {
        return apolloClient.querySingle(RolesQuery(), authManager) {
            it.project_roles.fold(mutableListOf(),
                { list, projectRole ->
                    if (projectRole != null) list.add(projectRole)
                    list
                }
            )
        }
    }

    override fun pushAdHocRackTransfer(
        assetId: String,
        taskId: String,
        scannedAt: String,
        facilityId: String,
        rackLocationId: String,
        userId: String,
        userName: String
    ): Completable {
        return apolloClient.mutationSingle(
            PushAdHocRackTransferTraceEventMutation(
                assetId = assetId,
                taskId = taskId,
                scannedAt = scannedAt,
                facilityId = facilityId,
                rackLocationId = rackLocationId,
                userId = userId,
                userName = userName
            ), authManager
        ) { it.adhocRackTransfer }.flatMapCompletable { Completable.complete() }
    }

    override fun pushAdHocTraceEvent(
        assetId: String,
        conditionId: String?,
        scannedAt: String,
        rackLocationId: String?,
        tags: List<String>?,
        laserLength: Double?,
        userId: String,
        userName: String
    ): Completable {
        return apolloClient.mutationSingle(
            PushAdHocTraceEventMutation(
                assetId = assetId,
                conditionId = Input.optional(conditionId),
                scannedAt = scannedAt,
                rackLocationId = Input.optional(rackLocationId),
                tags = Input.optional(tags),
                laserLength = Input.optional(laserLength),
                userId = userId,
                userName = userName
            ), authManager
        ) { it.adhocScan.asset_id }.flatMapCompletable { Completable.complete() }
    }

    override fun pushQuickRejectEvent(
        taskId: String,
        assetId: String,
        scannedAt: String,
        consumptionStatus: String,
        rejectReason: String?,
        rejectComment: String?,
        tags: List<String>?,
        userId: String,
        userName: String
    ): Completable {
        return apolloClient.mutationSingle(
            PushQuickRejectTraceEventMutation(
                taskId = taskId,
                assetId = assetId,
                scannedAt = scannedAt,
                consumptionStatus = Input.optional(consumptionStatus),
                rejectReason = Input.optional(rejectReason),
                rejectComment = Input.optional(rejectComment),
                tags = Input.optional(tags),
                userId = userId,
                userName = userName
            ), authManager
        ) { it.quickReject.id }.flatMapCompletable { Completable.complete() }
    }

    override fun pushDeleteTag(assetId: String, tag: String): Completable {
        return apolloClient.mutationSingle(
            DeleteTagMutation(
                assetId = assetId,
                tag = tag
            ), authManager
        ) { it.deleteTag.was_deleted }.flatMapCompletable { Completable.complete() }
    }

    override fun pushFeedback(content: String, email: String, tags: List<String>): Completable {
        return apolloClient.mutationSingle(
            CreateFeedbackMutation(
                content = content,
                email = email,
                tag = tags
            ), authManager
        ) { it.createFeedback.id }.flatMapCompletable { Completable.complete() }
    }
}
