package com.scgts.sctrace.tasks

import com.scgts.sctrace.base.model.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.threeten.bp.ZonedDateTime

interface TasksRepository {
    fun getTask(taskId: String): Single<Task>

    fun getTaskObs(taskId: String): Observable<Task>

    fun hasTask(taskId: String): Single<Boolean>

    fun updateTaskId(id: String, newId: String): Completable

    fun updateTraceEventTaskId(taskId: String, newTaskId: String): Completable

    fun deleteLocalAdHocActionTask(taskId: String): Completable

    fun getTasksObs(): Observable<List<Task>>

    fun getTasksCountObs(): Observable<Int>

    fun getFilteredTasks(tasksFilterAndSort: TasksFilterAndSort): Single<List<Task>>

    fun getTasksCountByProject(tasksFilter: TasksFilterAndSort): Single<List<ProjectWithNumOfTasks>>

    fun getTasksCountByTaskType(tasksFilter: TasksFilterAndSort): Single<List<TaskTypeWithNumOfTasks>>

    fun getTasksCountByStatus(tasksFilter: TasksFilterAndSort): Single<List<TaskStatusWithNumOfTasks>>

    fun getTasksCountByLocation(
        fromLocation: Boolean,
        tasksFilter: TasksFilterAndSort,
    ): Single<List<FacilityWithNumOfTasks>>

    fun getPendingTaskIdsObs(): Observable<List<String>>

    fun updateTasksStatusToPending(tasksIds: List<String>): Completable

    fun getAssets(assetIds: List<String>): Single<List<Asset>>

    fun getTasks(taskIds: List<String>): Single<List<Task>>

    fun getTaskAssets(taskId: String): Observable<List<Asset>>

    fun getUnsubmittedAssets(taskId: String): Observable<List<Asset>>

    fun getAssetIdsOfNotSubmittedTraceEventForTask(taskId: String): Single<List<String>>

    fun getAllAssets(projectId: String): Single<List<Asset>>

    fun getAssetsByTag(tag: String): Single<List<Asset>>

    fun getAsset(id: String): Single<Asset>

    fun getAssetWithTraceEventData(assetId: String, taskId: String?): Single<Asset>

    fun getAssetTags(id: String): Single<List<String>>

    fun updateAssetTagList(assetId: String, tags: List<String>): Completable

    fun captureByAttributes(
        manufacturer: String,
        millWorkNumber: String,
        pipeNumber: String,
        heatNumber: String,
        exMillDate: String,
    ): Single<Asset>

    fun removeCapturedAsset(taskId: String, assetId: String): Completable

    fun getTaskLastUpdatedDate(taskId: String): Observable<ZonedDateTime>

    fun getAssetProductInformations(
        orderId: String,
        taskId: String
    ): Observable<List<AssetProductInformation>>

    fun productVariantExpectedInOrder(orderId: String, productVariant: String): Single<Boolean>

    fun hasQueue(): Observable<Boolean>

    fun addToQueue(task: Task, assetIds: List<String>): Completable

    fun addToQueue(taskId: String, assetId: String): Completable

    fun getAttributes(
        selections: Map<AssetAttribute, String?>,
        projectId: String,
    ): Single<Map<AssetAttribute, List<String>>>

    fun submitQueue(): Completable

    fun getPendingTraceEvents(): Observable<List<TraceEvent>>

    fun getTotalUnsubmittedCount(): Observable<Int>

    fun createAdHocTask(
        taskType: TaskType,
        project: Project,
        description: String,
        toLocationId: String,
        toLocationName: String,
        orderType: OrderType = OrderType.RETURN_TRANSFER,
        fromLocationId: String? = null,
        fromLocationName: String? = null,
        arrivalDate: ZonedDateTime? = null,
        dispatchDate: ZonedDateTime? = null,
        defaultRackLocationId: String? = null,
    ): Completable

    fun addTraceEvent(
        taskId: String,
        assetId: String,
        facilityId: String,
        rackLocationId: String? = null,
        fromLocationId: String? = null,
        toLocationId: String? = null,
        conditionId: String? = null,
        laserLength: Double? = null,
        consumed: Boolean? = null,
        rejectReason: String? = null,
        rejectComment: String? = null,
        adHocActionTaskType: String? = null,
    ): Completable

    fun getYardsForProject(projectId: String): Single<List<Facility>>

    fun getRigsForProject(projectId: String): Single<List<Facility>>

    fun getWellsForProject(projectId: String): Single<List<Facility>>

    fun getFacilityById(facilityId: String): Single<Facility>

    fun getRacksForYard(locationId: String): Single<List<RackLocation>>

    fun getProjectRackLocations(projectId: String): Single<List<RackLocation>>

    fun getRackLocationById(id: String): Single<RackLocation>

    fun updateDefaultRackLocationForTask(taskId: String, defaultRackLocationId: String): Completable

    fun getProjects(): Single<List<Project>>

    fun getProjectById(projectId: String): Single<Project>

    fun getProjectsName(projectIds: List<String>): Single<List<String>>

    fun getProjectConditions(projectId: String): Single<List<Condition>>

    fun getConditionByIdAndProjectId(conditionId: String, projectId: String): Single<Condition>

    fun getDefaultCondition(projectId: String): Single<Condition>

    fun getTraceEvent(taskId: String, assetId: String): Single<TraceEvent>

    fun traceEventExists(taskId: String, assetId: String): Single<Boolean>

    fun getTalliesForAssets(assetIds: List<String>): Observable<Tallies>

    fun getTalliesForTask(taskId: String, sessionOnly: Boolean = false): Observable<Tallies>

    fun getTalliesForTaskSingle(taskId: String, sessionOnly: Boolean = false): Single<Tallies>

    fun getAssetLength(assetId: String, taskId: String?): Single<Double>

    fun validateTaskIsOutboundDispatchOrBuildOrder(taskId: String?): Single<Boolean>

    fun updateOutboundTraceEventCheckedStatus(
        taskId: String,
        assetId: String,
        checked: Boolean,
    ): Completable

    fun deleteUncheckedOutboundTraceEvents(taskId: String): Completable

    fun hasExpectedAmountWith(
        orderId: String,
        productId: String,
        contractNumber: String,
        shipmentNumber: String,
        conditionId: String,
        rackLocationId: String,
    ): Single<Boolean>

    fun getConditionById(conditionId: String): Single<Condition>

    fun getDetailFeedbackDtrace(): Single<DetailFeedbackDtrace>

    fun hasExpectedAmountContractNumber(
        orderId: String,
        productId: String,
        contractNumber: String,
    ): Single<Boolean>

    fun haxExpectedAmountShipmentNumber(
        orderId: String,
        productId: String,
        shipmentNumber: String,
    ): Single<Boolean>

    fun hasExpectedAmountCondition(
        orderId: String,
        productId: String,
        conditionId: String,
    ): Single<Boolean>

    fun hasExpectedAmountRackLocation(
        orderId: String,
        productId: String,
        rackLocationId: String,
    ): Single<Boolean>

    fun differentExpectedAmountConditionAndRackLocation(
        orderId: String,
        productId: String,
        conditionId: String,
        rackLocationId: String,
    ): Single<Boolean>

    fun getRackTransferAssets(
        taskId: String,
        unitType: UnitType,
    ): Observable<List<RackTransferModel>>

    fun createRackTransferTraceEvents(
        assetIds: List<String>,
        taskId: String,
        rackLocationId: String
    ): Completable

    fun deleteTraceEvents(
        assetIds: List<String>,
        taskId: String
    ): Completable

    fun getRackTransferAssetsForTraceEvent(
        taskId: String,
        rackLocationId: String,
        millWorkNum: String,
        productDescription: String,
    ): Single<List<Asset>>

    fun hasRackLocation(rackLocationId: String): Single<Boolean>

    fun checkIfNotSubmittedTraceEventExistForTaskSingle(taskId: String):Single<Boolean>

    fun checkIfNotSubmittedTraceEventExistForTaskObs(taskId: String):Observable<Boolean>
}
