package com.scgts.sctrace.tasks

import android.os.Build
import androidx.sqlite.db.SimpleSQLiteQuery
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.AssetAttribute.*
import com.scgts.sctrace.base.model.SubmitStatus.NOT_SUBMITTED
import com.scgts.sctrace.base.model.SubmitStatus.PENDING
import com.scgts.sctrace.base.model.TaskStatus.IN_PROGRESS
import com.scgts.sctrace.base.model.TaskStatus.NOT_STARTED
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.base.util.formatTally
import com.scgts.sctrace.database.dao.*
import com.scgts.sctrace.database.model.*
import com.scgts.sctrace.network.ApolloService
import com.scgts.sctrace.network.retrofit.AssetService
import com.scgts.sctrace.tasks.BuildConfig.VERSION_NAME
import com.scgts.sctrace.tasks.mappers.toEntity
import com.scgts.sctrace.tasks.mappers.toUiModel
import com.scgts.sctrace.tasks.mappers.uiModel
import com.scgts.sctrace.user.UserRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.flatMapIterable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import util.sendErrorToDtrace
import java.util.*

class TasksRepositoryImpl(
    private val apolloService: ApolloService,
    private val tasksDao: TasksDao,
    private val assetsDao: AssetsDao,
    private val traceEventsDao: TraceEventsDao,
    private val expectedAmountsDao: ExpectedAmountsDao,
    private val conditionsDao: ConditionsDao,
    private val facilitiesDao: FacilitiesDao,
    private val projectsDao: ProjectsDao,
    private val userRepository: UserRepository,
    private val rackLocationsDao: RackLocationsDao,
    private val assetService: AssetService,
    private val projectConditionsDao: ProjectConditionsDao,
    private val projectFacilitiesDao: ProjectFacilitiesDao,
    private val miscellaneousQueueDao: MiscellaneousQueueDao,
) : TasksRepository, ProjectsRepository {

    override fun syncAssets(): Completable {
        return getProjects().toObservable().flatMapIterable().flatMapCompletable { project ->
            if (project.lastUpdated != null) {
                assetService.getArchivedAssetsByProject(project.id, formatTime(project.lastUpdated))
                    .flatMapCompletable { archivedAssets -> assetsDao.deleteByIds(archivedAssets.map { it.id }) }
                    .andThen(
                        assetService.getAssetsByProject(
                            projectId = project.id,
                            lastUpdated = formatTime(project.lastUpdated)
                        )
                    )
            } else {
                assetService.getAssetsByProject(project.id)
            }.flatMapCompletable { assets ->
                insertAssets(assets.map { it.toEntity() })
            }.andThen(
                projectsDao.updateProjectLastUpdated(
                    id = project.id,
                    lastUpdated = ZonedDateTime.now(ZoneId.of("UTC"))
                )
            ).doOnError { error: Throwable -> sendError(error) }
        }.subscribeOn(Schedulers.io())
    }

    override fun getTask(taskId: String): Single<Task> {
        return tasksDao.getById(taskId).map { task -> task.toUiModel() }
            .doOnError { error: Throwable -> sendError(error) }
            .subscribeOn(Schedulers.io())
    }

    override fun getTaskObs(taskId: String): Observable<Task> {
        return tasksDao.getByIdObs(taskId).map { task -> task.toUiModel() }
            .doOnError { error: Throwable -> sendError(error) }
            .subscribeOn(Schedulers.io())
    }

    override fun hasTask(taskId: String): Single<Boolean> {
        return tasksDao.hasItem(taskId).doOnError { error: Throwable -> sendError(error) }
            .subscribeOn(Schedulers.io())
    }

    override fun updateTaskId(id: String, newId: String): Completable {
        return tasksDao.updateId(id, newId).subscribeOn(Schedulers.io())
    }

    override fun deleteLocalAdHocActionTask(taskId: String): Completable {
        return tasksDao.deleteById(taskId).subscribeOn(Schedulers.io())
    }

    override fun getTasksObs(): Observable<List<Task>> {
        return tasksDao.getAllObs().map { it.map { task -> task.toUiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getTasksCountObs(): Observable<Int> {
        return tasksDao.getCountObs().subscribeOn(Schedulers.io())
    }

    override fun getFilteredTasks(tasksFilterAndSort: TasksFilterAndSort): Single<List<Task>> {
        return tasksDao.getFilterAndSortedTasks(tasksFilterAndSort)
            .map { it.map { task -> task.toUiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getTasksCountByProject(tasksFilter: TasksFilterAndSort): Single<List<ProjectWithNumOfTasks>> {
        return tasksDao.getTasksCountByProject(tasksFilter).subscribeOn(Schedulers.io())
    }

    override fun getTasksCountByTaskType(tasksFilter: TasksFilterAndSort): Single<List<TaskTypeWithNumOfTasks>> {
        return tasksDao.getTasksCountByTaskType(tasksFilter).subscribeOn(Schedulers.io())
    }

    override fun getTasksCountByStatus(tasksFilter: TasksFilterAndSort): Single<List<TaskStatusWithNumOfTasks>> {
        return tasksDao.getTasksCountByTaskStatus(tasksFilter).subscribeOn(Schedulers.io())
    }

    override fun getTasksCountByLocation(
        fromLocation: Boolean,
        tasksFilter: TasksFilterAndSort,
    ): Single<List<FacilityWithNumOfTasks>> {
        return tasksDao.getTasksCountByFacility(fromLocation, tasksFilter)
            .subscribeOn(Schedulers.io())
    }

    override fun getPendingTaskIdsObs(): Observable<List<String>> {
        return traceEventsDao.getTasksWithPendingTraceEventsObs().subscribeOn(Schedulers.io())
    }

    override fun updateTasksStatusToPending(tasksIds: List<String>): Completable {
        return tasksDao.getTasksForIds(tasksIds).toObservable().flatMapIterable()
            .flatMapCompletable { task ->
                if (task.status != TaskStatus.PENDING) {
                    tasksDao.updateStatus(task.id, TaskStatus.PENDING)
                } else Completable.complete()
            }
    }

    override fun getTaskAssets(taskId: String): Observable<List<Asset>> {
        return assetsDao.getAllAssetsForTask(taskId).subscribeOn(Schedulers.io())
    }

    override fun getUnsubmittedAssets(taskId: String): Observable<List<Asset>> {
        return assetsDao.getNotSubmittedAssetsForTask(taskId).subscribeOn(Schedulers.io())
    }

    override fun getAssetIdsOfNotSubmittedTraceEventForTask(taskId: String): Single<List<String>> {
        return traceEventsDao.getAssetIdsOfNotSubmittedTraceEventForTask(taskId)
            .subscribeOn(Schedulers.io())
    }

    override fun getAssets(assetIds: List<String>): Single<List<Asset>> {
        return assetsDao.getAssetsForIds(assetIds)
            .map { it.map { assetEntity -> assetEntity.uiModel() } }.subscribeOn(Schedulers.io())
    }

    override fun getTasks(taskIds: List<String>): Single<List<Task>> {
        return tasksDao.getTasksForIds(taskIds)
            .map { it.map { taskEntity -> taskEntity.toUiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getAllAssets(projectId: String): Single<List<Asset>> {
        return assetsDao.getAssetsSingle(projectId)
            .map { entities -> entities.map { it.uiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getAssetsByTag(tag: String): Single<List<Asset>> {
        return assetsDao.getAssetsForTag(tag).map { assets -> assets.map { it.uiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getAsset(id: String): Single<Asset> {
        return assetsDao.getAssetById(id).map { it.uiModel() }.subscribeOn(Schedulers.io())
    }

    override fun getAssetWithTraceEventData(assetId: String, taskId: String?): Single<Asset> {
        return if (taskId != null && traceEventExists(taskId, assetId).blockingGet()) {
            traceEventsDao.getTraceEvent(taskId, assetId).flatMap { traceEvent ->
                assetsDao.getAssetById(assetId).map {
                    it.uiModel(
                        traceEventLength = traceEvent.laserLength,
                        traceEventRackLocationId = traceEvent.rackLocationId,
                        traceEventConditionId = traceEvent.conditionId
                    )
                }
            }.subscribeOn(Schedulers.io())
        } else getAsset(assetId)
    }

    override fun getAssetTags(id: String): Single<List<String>> {
        return assetsDao.getAssetById(id).map { it.tags.sorted() }.subscribeOn(Schedulers.io())
    }

    override fun captureByAttributes(
        manufacturer: String,
        millWorkNumber: String,
        pipeNumber: String,
        heatNumber: String,
        exMillDate: String,
    ): Single<Asset> {
        return assetsDao.getAssetByAttributes(
            manufacturer = manufacturer,
            millWorkNumber = millWorkNumber,
            pipeNumber = pipeNumber,
            heatNumber = heatNumber,
            exMillDate = exMillDate
        )
            .map { it.uiModel() }
            .subscribeOn(Schedulers.io())
    }

    override fun updateAssetTagList(assetId: String, tags: List<String>): Completable {
        return assetsDao.updateAssetTags(assetId, tags).subscribeOn(Schedulers.io())
    }

    override fun addTraceEvent(
        taskId: String,
        assetId: String,
        facilityId: String,
        rackLocationId: String?,
        fromLocationId: String?,
        toLocationId: String?,
        conditionId: String?,
        laserLength: Double?,
        consumed: Boolean?,
        rejectReason: String?,
        rejectComment: String?,
        adHocActionTaskType: String?,
    ): Completable {
        val user = userRepository.getUser().blockingFirst()
        return traceEventsDao.insert(
            TraceEventEntity(
                taskId = taskId,
                assetId = assetId,
                submitStatus = if (taskId == AD_HOC_QUICK_SCAN.id) PENDING else NOT_SUBMITTED,
                facilityId = facilityId,
                rackLocationId = rackLocationId,
                fromLocationId = fromLocationId,
                toLocationId = toLocationId,
                scannedAt = ZonedDateTime.now(ZoneId.of("UTC")),
                conditionId = conditionId,
                laserLength = laserLength,
                consumed = consumed,
                rejectReason = rejectReason,
                rejectComment = rejectComment,
                adHocActionTaskType = adHocActionTaskType,

                userId = user.id,
                userName = user.name ?: ""
            )
        ).andThen(
            if (taskId == AD_HOC_QUICK_SCAN.id || taskId == AD_HOC_REJECT_SCAN.id) Completable.complete() else checkAndUpdateTaskStatus(
                taskId
            )
        ).subscribeOn(Schedulers.io())
    }

    override fun removeCapturedAsset(taskId: String, assetId: String): Completable {
        return traceEventsDao.delete(taskId, assetId).andThen(checkAndUpdateTaskStatus(taskId))
            .subscribeOn(Schedulers.io())
    }

    override fun syncRemote(): Completable {
        return apolloService.getProjects().flatMapCompletable { projects ->
            val tasks = mutableListOf<TaskEntity>()
            val userProjects = mutableListOf<ProjectPartialEntity>()
            val traceEvents = mutableListOf<TraceEventEntity>()
            val expectedAmounts = mutableListOf<ExpectedAmountEntity>()
            val facilities = mutableListOf<FacilityEntity>()
            val rackLocations = mutableListOf<RackLocationEntity>()
            val conditions = mutableListOf<ConditionEntity>()
            val projectConditions = mutableListOf<ProjectConditionEntity>()
            val projectFacilities = mutableListOf<ProjectFacilityEntity>()
            projects.forEach { project ->
                userProjects.add(project.toEntity())
                project.mobile_orders.forEach { order ->
                    expectedAmounts.addAll(order.expected_amounts.map { it.toEntity() })
                    order.mobile_tasks?.map { task ->
                        tasks.add(
                            task.toEntity(
                                order = order,
                                projectId = project.id,
                                projectUoM = project.uom_type,
                                operatingParty = project.operating_party
                            )
                        )
                        task.trace_events.map { traceEvents.add(it.toEntity()) }
                    }
                }
                project.mills.map { mill ->
                    facilities.add(mill.toEntity())
                    projectFacilities.add(ProjectFacilityEntity(project.id, mill.id))
                }
                project.yards.map { yard ->
                    facilities.add(yard.toEntity())
                    projectFacilities.add(ProjectFacilityEntity(project.id, yard.id))
                    rackLocations.addAll(
                        yard.locations.map { location -> location.toEntity(yard.id) }
                    )
                }
                project.wells.map { well ->
                    facilities.add(well.toEntity())
                    projectFacilities.add(ProjectFacilityEntity(project.id, well.id))
                }
                project.rigs.map { rig ->
                    facilities.add(rig.toEntity())
                    projectFacilities.add(ProjectFacilityEntity(project.id, rig.id))
                }
                project.condition_codes.map { condition ->
                    conditions.add(condition.toEntity())
                    projectConditions.add(ProjectConditionEntity(project.id, condition.id))
                }
            }

            updateProjects(userProjects)
                .andThen(updateExpectedAmounts(expectedAmounts))
                .andThen(updateTraceEvents(traceEvents))
                .andThen(updateTasks(tasks))
                .andThen(insertProjectConditions(projectConditions))
                .andThen(insertConditions(conditions))
                .andThen(insertProjectFacilities(projectFacilities))
                .andThen(updateFacilities(facilities))
                .andThen(updateRackLocations(rackLocations))
        }
            .andThen(syncRoles())
            .andThen(syncAssets())
            .subscribeOn(Schedulers.io())
    }

    private fun syncRoles(): Completable {
        return apolloService.getRoles().flatMapCompletable { projects ->
            userRepository.updateUserRoles(projects)
        }
    }

    override fun getProjects(): Single<List<Project>> {
        return projectsDao.getAllProjects().map { projects -> projects.map { it.toUiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getProjectById(projectId: String): Single<Project> {
        return projectsDao.getProjectById(projectId).map { it.toUiModel() }
            .subscribeOn(Schedulers.io())
    }

    override fun getProjectsName(projectIds: List<String>): Single<List<String>> {
        return projectsDao.getProjectsName(projectIds).subscribeOn(Schedulers.io())
    }

    override fun getProjectConditions(projectId: String): Single<List<Condition>> {
        return conditionsDao.getConditionsByProject(projectId)
            .map { it.map { conditionEntity -> conditionEntity.toUiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getConditionByIdAndProjectId(
        conditionId: String,
        projectId: String,
    ): Single<Condition> {
        return projectConditionsDao.hasCondition(conditionId, projectId).flatMap { conditionExist ->
            if (conditionExist) conditionsDao.getConditionByIdAndProjectId(conditionId, projectId)
            else conditionsDao.getDefaultCondition(projectId)
        }.map { it.toUiModel() }.subscribeOn(Schedulers.io())
    }

    override fun getDefaultCondition(projectId: String): Single<Condition> {
        return conditionsDao.getDefaultCondition(projectId).map { it.toUiModel() }
            .subscribeOn(Schedulers.io())
    }

    override fun getTraceEvent(taskId: String, assetId: String): Single<TraceEvent> {
        return traceEventsDao.getTraceEvent(taskId, assetId).map { it.toUiModel() }
            .subscribeOn(Schedulers.io())
    }

    override fun traceEventExists(taskId: String, assetId: String): Single<Boolean> {
        return traceEventsDao.hasTraceEvent(taskId, assetId).subscribeOn(Schedulers.io())
    }

    override fun getAttributes(
        selections: Map<AssetAttribute, String?>,
        projectId: String,
    ): Single<Map<AssetAttribute, List<String>>> {
        fun getWhereQuery(forAttribute: AssetAttribute): String {
            var whereQuery = ""

            AssetAttribute.values().forEach { attribute ->
                if (forAttribute != attribute && selections[attribute] != null) {
                    whereQuery += " ${attribute.attributeName}='${selections[attribute]}' AND"
                }
            }
            return " WHERE $whereQuery projectId='$projectId'"
        }

        fun getAttributeQuery(attribute: AssetAttribute) =
            SimpleSQLiteQuery(
                "SELECT DISTINCT ${attribute.attributeName} FROM AssetEntity ${
                    getWhereQuery(attribute)
                }"
            )

        return Single.zip(
            assetsDao.getAttributes(getAttributeQuery(Manufacturer)),
            assetsDao.getAttributes(getAttributeQuery(MillWorkNumber)),
            assetsDao.getAttributes(getAttributeQuery(PipeNumber)),
            assetsDao.getAttributes(getAttributeQuery(HeatNumber)),
            assetsDao.getAttributes(getAttributeQuery(ExMillDate)),
            { manufacturers, millWorkNums, pipeNumbers, heatNumbers, exMillDates ->
                mapOf(
                    Manufacturer to manufacturers,
                    MillWorkNumber to millWorkNums,
                    PipeNumber to pipeNumbers,
                    HeatNumber to heatNumbers,
                    ExMillDate to exMillDates
                )
            }
        ).subscribeOn(Schedulers.io())
    }

    override fun createAdHocTask(
        taskType: TaskType,
        project: Project,
        description: String,
        toLocationId: String,
        toLocationName: String,
        orderType: OrderType,
        fromLocationId: String?,
        fromLocationName: String?,
        arrivalDate: ZonedDateTime?,
        dispatchDate: ZonedDateTime?,
        defaultRackLocationId: String?,
    ): Completable {
        return TaskEntity(
            id = taskType.id,
            createdAt = ZonedDateTime.now(ZoneId.of("UTC")),
            type = taskType,
            typeForFiltering = TaskTypeForFiltering.AD_HOC,
            totalExpectedLength = 0.0,
            totalNumJoints = 0,
            projectId = project.id,
            orderId = "",
            status = IN_PROGRESS,
            orderType = orderType,
            unitOfMeasure = project.unitOfMeasure.abbreviation,
            specialInstructions = null,
            description = description,
            toLocationId = toLocationId,
            toLocationName = toLocationName,
            fromLocationId = fromLocationId,
            fromLocationName = fromLocationName,
            deliveryDate = null,
            arrivalDate = arrivalDate.toString(),
            dispatchDate = dispatchDate.toString(),
            defaultRackLocationId = defaultRackLocationId
        ).let { tasksDao.insert(it).subscribeOn(Schedulers.io()) }
    }

    override fun getTaskLastUpdatedDate(taskId: String): Observable<ZonedDateTime> {
        return traceEventsDao.getUpdatedAtTraceEvents(taskId).subscribeOn(Schedulers.io())
    }

    override fun getAssetProductInformations(
        orderId: String,
        taskId: String
    ): Observable<List<AssetProductInformation>> {
        return expectedAmountsDao.getAssetProductInformations(orderId, taskId)
            .subscribeOn(Schedulers.io())
    }

    override fun productVariantExpectedInOrder(
        orderId: String,
        productVariant: String,
    ): Single<Boolean> {
        return expectedAmountsDao.productVariantExpectedInOrder(orderId, productVariant)
            .subscribeOn(Schedulers.io())
    }

    /**
     * In the event where we are submitting an asset which is only associated with a task from
     * another task in the same order there will be no trace event for it! We should check if
     * a trace event exists before submitting it and if not create a new one.
     */
    override fun addToQueue(task: Task, assetIds: List<String>): Completable {
        return traceEventsDao.hasTraceEvent(task.id, assetIds[0]).flatMapCompletable { hasEvent ->
            if (hasEvent) {
                traceEventsDao.addToQueue(task.id, assetIds)
            } else {
                val user = userRepository.getUser().blockingFirst()
                val entities = assetIds.map { assetId ->
                    TraceEventEntity(
                        taskId = task.id,
                        assetId = assetId,
                        submitStatus = PENDING,
                        facilityId = task.toLocationId!!,
                        scannedAt = ZonedDateTime.now(ZoneId.of("UTC")),
                        adHocActionTaskType = if (task.isAdHocAction()) task.type.serverName else null,
                        userId = user.id,
                        userName = user.name ?: ""
                    )
                }
                traceEventsDao.insert(entities)
            }.subscribeOn(Schedulers.io())
        }
    }

    override fun updateTraceEventTaskId(taskId: String, newTaskId: String): Completable {
        return traceEventsDao.updateId(taskId, newTaskId).subscribeOn(Schedulers.io())
    }

    override fun addToQueue(taskId: String, assetId: String): Completable {
        return traceEventsDao.addToQueue(taskId, assetId).subscribeOn(Schedulers.io())
    }

    override fun hasQueue(): Observable<Boolean> {
        return traceEventsDao.hasQueue().distinctUntilChanged().subscribeOn(Schedulers.io())
    }

    override fun submitQueue(): Completable {
        return traceEventsDao.getPendingTraceEventsSingle().flatMapCompletable { traceEvents ->
            // Quick action trace events have a separate mutation and
            // must have a UUID generated client side

            val completables = mutableListOf<Completable>()
            completables.addAll(
                traceEvents.map { traceEvent ->
                    getAssetTags(traceEvent.assetId).flatMapCompletable { tags ->
                        when (traceEvent.adHocActionTaskType) {
                            AD_HOC_QUICK_SCAN.serverName ->
                                apolloService.pushAdHocTraceEvent(
                                    assetId = traceEvent.assetId,
                                    conditionId = traceEvent.conditionId,
                                    scannedAt = formatTime(traceEvent.scannedAt),
                                    rackLocationId = traceEvent.rackLocationId,
                                    tags = tags,
                                    laserLength = traceEvent.laserLength,
                                    userId = traceEvent.userId,
                                    userName = traceEvent.userName
                                )
                                    .andThen(traceEventsDao.delete(traceEvent))
                                    .onErrorComplete { err ->
                                        sendError(err)
                                        true
                                    }
                                    .subscribeOn(Schedulers.io())
                            AD_HOC_DISPATCH_TO_YARD.serverName ->
                                tasksDao.getDispatchDate(traceEvent.taskId)
                                    .flatMapCompletable { dispatchDate ->
                                        apolloService.quickDispatchReturn(
                                            taskId = traceEvent.taskId,
                                            assetId = traceEvent.assetId,
                                            conditionId = traceEvent.conditionId,
                                            scannedAt = formatTime(traceEvent.scannedAt),
                                            toLocationId = traceEvent.toLocationId!!,
                                            fromLocationId = traceEvent.fromLocationId!!,
                                            facilityId = traceEvent.facilityId,
                                            rackLocationId = traceEvent.rackLocationId,
                                            dispatchDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                .format(
                                                    ZonedDateTime.parse(dispatchDate)
                                                        .toLocalDateTime()
                                                ),
                                            tags = tags,
                                            userId = traceEvent.userId,
                                            userName = traceEvent.userName
                                        )
                                            .andThen(traceEventsDao.delete(traceEvent))
                                            .onErrorComplete { err ->
                                                sendError(err)
                                                true
                                            }
                                            .subscribeOn(Schedulers.io())
                                    }
                            AD_HOC_DISPATCH_TO_WELL.serverName ->
                                tasksDao.getDispatchDate(traceEvent.taskId)
                                    .flatMapCompletable { dispatchDate ->
                                        apolloService.quickDispatchTransfer(
                                            taskId = traceEvent.taskId,
                                            assetId = traceEvent.assetId,
                                            conditionId = traceEvent.conditionId,
                                            scannedAt = formatTime(traceEvent.scannedAt),
                                            toLocationId = traceEvent.toLocationId!!,
                                            fromLocationId = traceEvent.fromLocationId!!,
                                            facilityId = traceEvent.facilityId,
                                            rackLocationId = traceEvent.rackLocationId,
                                            dispatchDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                .format(
                                                    ZonedDateTime.parse(dispatchDate)
                                                        .toLocalDateTime()
                                                ),
                                            tags = tags,
                                            userId = traceEvent.userId,
                                            userName = traceEvent.userName
                                        )
                                            .andThen(traceEventsDao.delete(traceEvent))
                                            .onErrorComplete { err ->
                                                sendError(err)
                                                true
                                            }
                                            .subscribeOn(Schedulers.io())
                                    }
                            AD_HOC_INBOUND_TO_WELL.serverName ->
                                apolloService.quickInboundToWell(
                                    taskId = traceEvent.taskId,
                                    assetId = traceEvent.assetId,
                                    conditionId = traceEvent.conditionId,
                                    scannedAt = formatTime(traceEvent.scannedAt),
                                    toLocationId = traceEvent.toLocationId!!,
                                    facilityId = traceEvent.facilityId,
                                    rackLocationId = traceEvent.rackLocationId,
                                    tags = tags,
                                    userId = traceEvent.userId,
                                    userName = traceEvent.userName
                                )
                                    .andThen(traceEventsDao.delete(traceEvent))
                                    .onErrorComplete { err ->
                                        sendError(err)
                                        true
                                    }
                                    .subscribeOn(Schedulers.io())
                            AD_HOC_REJECT_SCAN.serverName ->
                                apolloService.pushQuickRejectEvent(
                                    taskId = UUID.randomUUID().toString(),
                                    assetId = traceEvent.assetId,
                                    scannedAt = formatTime(traceEvent.scannedAt),
                                    consumptionStatus = ConsumptionStatus.REJECT.status,
                                    rejectReason = traceEvent.rejectReason,
                                    rejectComment = traceEvent.rejectComment,
                                    tags = tags,
                                    userId = traceEvent.userId,
                                    userName = traceEvent.userName
                                )
                                    .andThen(traceEventsDao.delete(traceEvent))
                                    .onErrorComplete { err ->
                                        sendError(err)
                                        true
                                    }
                                    .subscribeOn(Schedulers.io())
                            AD_HOC_INBOUND_FROM_MILL.serverName ->
                                apolloService.quickInboundFromMill(
                                    taskId = traceEvent.taskId,
                                    assetId = traceEvent.assetId,
                                    conditionId = traceEvent.conditionId,
                                    scannedAt = formatTime(traceEvent.scannedAt),
                                    toLocationId = traceEvent.toLocationId!!,
                                    facilityId = traceEvent.facilityId,
                                    rackLocationId = traceEvent.rackLocationId,
                                    tags = tags,
                                    laserLength = traceEvent.laserLength,
                                    userId = traceEvent.userId,
                                    userName = traceEvent.userName
                                )
                                    .andThen(traceEventsDao.delete(traceEvent))
                                    .onErrorComplete { err ->
                                        sendError(err)
                                        true
                                    }
                                    .subscribeOn(Schedulers.io())
                            AD_HOC_INBOUND_FROM_WELL_SITE.serverName ->
                                apolloService.quickInboundTally(
                                    taskId = traceEvent.taskId,
                                    assetId = traceEvent.assetId,
                                    conditionId = traceEvent.conditionId,
                                    scannedAt = formatTime(traceEvent.scannedAt),
                                    toLocationId = traceEvent.toLocationId!!,
                                    facilityId = traceEvent.facilityId,
                                    rackLocationId = traceEvent.rackLocationId,
                                    tags = tags,
                                    laserLength = traceEvent.laserLength,
                                    userId = traceEvent.userId,
                                    userName = traceEvent.userName
                                )
                                    .andThen(traceEventsDao.delete(traceEvent))
                                    .onErrorComplete { err ->
                                        sendError(err)
                                        true
                                    }
                                    .subscribeOn(Schedulers.io())
                            AD_HOC_RACK_TRANSFER.serverName ->
                                apolloService.pushAdHocRackTransfer(
                                    assetId = traceEvent.assetId,
                                    taskId = traceEvent.taskId,
                                    scannedAt = formatTime(traceEvent.scannedAt),
                                    facilityId = traceEvent.facilityId,
                                    rackLocationId = traceEvent.rackLocationId?:"",
                                    userId = traceEvent.userId,
                                    userName = traceEvent.userName
                                ).andThen(traceEventsDao.delete(traceEvent))
                                    .onErrorComplete { err ->
                                        sendError(err)
                                        true
                                    }
                                    .subscribeOn(Schedulers.io())
                            else -> {
                                val consumptionStatus = when (traceEvent.consumed) {
                                    true -> ConsumptionStatus.CONSUME
                                    false -> ConsumptionStatus.REJECT
                                    else -> null
                                }

                                apolloService.pushTraceEvent(
                                    taskId = traceEvent.taskId,
                                    assetId = traceEvent.assetId,
                                    conditionId = traceEvent.conditionId,
                                    scannedAt = formatTime(traceEvent.scannedAt),
                                    facilityId = traceEvent.facilityId,
                                    rackLocationId = traceEvent.rackLocationId,
                                    consumptionStatus = consumptionStatus?.status,
                                    rejectReason = traceEvent.rejectReason,
                                    rejectComment = traceEvent.rejectComment,
                                    tags = tags,
                                    laserLength = traceEvent.laserLength,
                                    userId = traceEvent.userId,
                                    userName = traceEvent.userName
                                )
                                    .onErrorComplete { err ->
                                        sendError(err)
                                        true
                                    }
                                    .subscribeOn(Schedulers.io())
                            }
                        }
                    }
                }
            )
            Completable.concatDelayError(completables)
                .doOnError { error: Throwable -> sendError(error) }
                .andThen(cleanSubmittedAdHocActionTasks())
                .andThen(syncRemote())
        }
            .subscribeOn(Schedulers.io())
            .doOnError { error: Throwable -> sendError(error) }
    }

    private fun cleanSubmittedAdHocActionTasks(): Completable {
        val adHocActionTypes = listOf(
            AD_HOC_INBOUND_TO_WELL,
            AD_HOC_INBOUND_FROM_MILL,
            AD_HOC_INBOUND_FROM_WELL_SITE,
            AD_HOC_DISPATCH_TO_YARD,
            AD_HOC_DISPATCH_TO_WELL
        )
        val completables = mutableListOf<Completable>()

        completables.addAll(
            adHocActionTypes.map { type ->
                traceEventsDao.hasTraceEvent(type.serverName).flatMapCompletable { hasTraceEvent ->
                    if (!hasTraceEvent) tasksDao.deleteTaskByType(type)
                    else Completable.complete()
                }
            }
        )

        return Completable.mergeDelayError(completables)
    }

    override fun getPendingTraceEvents(): Observable<List<TraceEvent>> {
        return traceEventsDao.getPendingTraceEvents()
            .map { it.map { entity -> entity.toUiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getTotalUnsubmittedCount(): Observable<Int> {
        return Observable.combineLatest(
            getPendingTraceEvents(),
            miscellaneousQueueDao.getMiscFeedbackCount(),
            { tasks, miscCount ->
                val taskCount = tasks.groupBy { event -> event.taskId }.keys.size
                taskCount.plus(miscCount)
            }
        )
    }

    override fun getYardsForProject(projectId: String): Single<List<Facility>> {
        return facilitiesDao.getYardsByProject(projectId)
            .map { it.map { entity -> entity.uiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getRigsForProject(projectId: String): Single<List<Facility>> {
        return facilitiesDao.getRigsByProject(projectId)
            .map { it.map { entity -> entity.uiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getWellsForProject(projectId: String): Single<List<Facility>> {
        return facilitiesDao.getWellsByProject(projectId)
            .map { it.map { entity -> entity.uiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getFacilityById(facilityId: String): Single<Facility> {
        return facilitiesDao.getFacilityById(facilityId).map { it.uiModel() }
            .subscribeOn(Schedulers.io())
    }

    override fun getRacksForYard(locationId: String): Single<List<RackLocation>> {
        return rackLocationsDao.getRackLocationsForFacility(locationId)
            .map { it.map { entity -> entity.uiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getProjectRackLocations(projectId: String): Single<List<RackLocation>> {
        return rackLocationsDao.getRackLocationsByProject(projectId)
            .map { it.map { entity -> entity.uiModel() } }
            .subscribeOn(Schedulers.io())
    }

    override fun getRackLocationById(id: String): Single<RackLocation> {
        return rackLocationsDao.getRackLocationById(id).map { it.uiModel() }
            .subscribeOn(Schedulers.io())
    }

    override fun updateDefaultRackLocationForTask(
        taskId: String,
        defaultRackLocationId: String,
    ): Completable {
        return tasksDao.updateDefaultRackLocation(taskId, defaultRackLocationId)
            .subscribeOn(Schedulers.io())
    }

    override fun getTalliesForAssets(assetIds: List<String>): Observable<Tallies> {
        return assetsDao.getAssetsTallies(assetIds).subscribeOn(Schedulers.io())
    }

    override fun getTalliesForTask(
        taskId: String,
        sessionOnly: Boolean,
    ): Observable<Tallies> {
        return if (sessionOnly) {
            assetsDao.getSessionTallies(taskId)
        } else {
            assetsDao.getTaskTallies(taskId)
        }.subscribeOn(Schedulers.io())
    }

    override fun getTalliesForTaskSingle(
        taskId: String,
        sessionOnly: Boolean,
    ): Single<Tallies> {
        return if (sessionOnly) {
            assetsDao.getSessionTalliesSingle(taskId)
        } else {
            assetsDao.getTaskTalliesSingle(taskId)
        }.subscribeOn(Schedulers.io())
    }

    override fun getAssetLength(assetId: String, taskId: String?): Single<Double> {
        return if (
            taskId != null &&
            traceEventExists(taskId, assetId).blockingGet() &&
            getTraceEvent(taskId, assetId).blockingGet().laserLength != null
        ) getTraceEvent(taskId, assetId).map { it.laserLength }
        else getAsset(assetId).map { it.length }
    }

    override fun validateTaskIsOutboundDispatchOrBuildOrder(taskId: String?): Single<Boolean> {
        return if (taskId != null) {
            getTask(taskId).map { task -> task.orderType == OrderType.OUTBOUND && (task.type == BUILD_ORDER || task.type == DISPATCH) }
        } else Single.just(false)
    }

    override fun updateOutboundTraceEventCheckedStatus(
        taskId: String,
        assetId: String,
        checked: Boolean,
    ): Completable {
        return traceEventsDao.updateCheckedStatusForOutboundTraceEvent(taskId, assetId, checked)
            .subscribeOn(Schedulers.io())
    }

    override fun deleteUncheckedOutboundTraceEvents(taskId: String): Completable {
        return traceEventsDao.deleteUncheckedOutboundTraceEvents(taskId)
            .subscribeOn(Schedulers.io())
    }

    override fun hasExpectedAmountWith(
        orderId: String,
        productId: String,
        contractNumber: String,
        shipmentNumber: String,
        conditionId: String,
        rackLocationId: String,
    ): Single<Boolean> {
        return expectedAmountsDao.hasExpectedAmountWith(
            orderId,
            productId,
            contractNumber,
            shipmentNumber,
            conditionId,
            rackLocationId
        )
    }

    override fun getConditionById(conditionId: String): Single<Condition> {
        return conditionsDao.getConditionById(conditionId).map { it.toUiModel() }
            .subscribeOn(Schedulers.io())
    }

    override fun getDetailFeedbackDtrace(): Single<DetailFeedbackDtrace> {
        return Single.zip(
            userRepository.getUser().firstOrError(),
            userRepository.getUserRolesForAllProject().firstOrError(),
            getProjects(),
            { user, userRole, projects ->
                val projectCodes = ArrayList<String>()
                projects.map {
                    projectCodes.add(it.itimsProjectCode1.toString())
                    projectCodes.add(it.itimsProjectCode2.toString())
                }
                DetailFeedbackDtrace(
                    type = "Mobile",
                    appVersionNumber = VERSION_NAME,
                    timestamp = ZonedDateTime.now(ZoneId.of("UTC")).toString(),
                    deviceType = "${Build.DEVICE} ${Build.MODEL}",
                    userEmail = user.email ?: "",
                    projectCodes = projectCodes.toString(),
                    roles = getRoleNames(userRole),
                    userName = user.name ?: ""
                )
            }
        )
    }

    override fun hasExpectedAmountContractNumber(
        orderId: String,
        productId: String,
        contractNumber: String,
    ): Single<Boolean> {
        return expectedAmountsDao.hasExpectedAmountContractNumber(
            orderId,
            productId,
            contractNumber
        )
    }

    override fun haxExpectedAmountShipmentNumber(
        orderId: String,
        productId: String,
        shipmentNumber: String,
    ): Single<Boolean> {
        return expectedAmountsDao.haxExpectedAmountShipmentNumber(
            orderId,
            productId,
            shipmentNumber
        )
    }

    override fun hasExpectedAmountCondition(
        orderId: String,
        productId: String,
        conditionId: String,
    ): Single<Boolean> {
        return expectedAmountsDao.hasExpectedAmountCondition(
            orderId,
            productId,
            conditionId
        )
    }

    override fun hasExpectedAmountRackLocation(
        orderId: String,
        productId: String,
        rackLocationId: String,
    ): Single<Boolean> {
        return expectedAmountsDao.hasExpectedAmountRackLocation(
            orderId,
            productId,
            rackLocationId
        )
    }

    override fun differentExpectedAmountConditionAndRackLocation(
        orderId: String,
        productId: String,
        conditionId: String,
        rackLocationId: String,
    ): Single<Boolean> {
        return expectedAmountsDao.differentExpectedAmountConditionAndRackLocation(
            orderId,
            productId,
            conditionId,
            rackLocationId
        )
    }

    override fun getRackTransferAssets(
        taskId: String,
        unitType: UnitType,
    ): Observable<List<RackTransferModel>> {
        return assetsDao.getRackTransfersFromTraceEventAndAsset(taskId = taskId).map { list ->
            list.map {
                RackTransferModel(
                    productDescription = it.productDescription(),
                    millWorkNum = it.millWorkNumber,
                    rackLocationId = it.rackId,
                    rackLocationName = it.rackName,
                    totalJointsAndLength = formatTally(
                        expectedLength = it.expectedLength,
                        numJoints = it.joints,
                        unitType = unitType
                    )
                )
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun createRackTransferTraceEvents(
        assetIds: List<String>,
        taskId: String,
        rackLocationId: String,
    ): Completable {
        return getTask(taskId).flatMapCompletable { task ->
            val completables = assetIds.map { assetId ->
                addTraceEvent(
                    taskId = taskId,
                    assetId = assetId,
                    facilityId = task.toLocationId!!,
                    rackLocationId = rackLocationId,
                    fromLocationId = task.fromLocationId,
                    toLocationId = task.toLocationId,
                    adHocActionTaskType = if (task.isAdHocAction()) task.type.serverName else null,
                )
            }
            Completable.concatDelayError(completables)
        }
    }

    override fun deleteTraceEvents(
        assetIds: List<String>,
        taskId: String
    ): Completable {
        return traceEventsDao.deleteTraceEvents(taskId, assetIds)
    }

    override fun hasRackLocation(rackLocationId: String): Single<Boolean> {
        return rackLocationsDao.hasRackLocation(rackLocationId).subscribeOn(Schedulers.io())
    }

    override fun checkIfNotSubmittedTraceEventExistForTaskSingle(taskId: String): Single<Boolean> {
        return traceEventsDao.checkIfNotSubmittedTraceEventExistForTaskSingle(taskId = taskId).subscribeOn(Schedulers.io())
    }

    override fun checkIfNotSubmittedTraceEventExistForTaskObs(taskId: String):Observable<Boolean>{
        return traceEventsDao.checkIfNotSubmittedTraceEventExistForTaskObs(taskId = taskId).subscribeOn(Schedulers.io())
    }

    override fun getRackTransferAssetsForTraceEvent(
        taskId: String,
        rackLocationId: String,
        millWorkNum: String,
        productDescription: String,
    ): Single<List<Asset>> {
        return traceEventsDao.getAssetIdsTraceEventsByTaskAndRackLocation(taskId, rackLocationId)
            .flatMap {
                assetsDao.getAssetsForIds(it)
            }.map {
                val newList = it.filter { assetEntity ->
                    assetEntity.millWorkNumber == millWorkNum && assetsDao.getProductDescriptionById(
                        assetEntity.id
                    ).blockingGet().formattedDescription() == productDescription
                }
                newList
            }.map { entities ->
                entities.map { it.uiModel() }
            }.subscribeOn(Schedulers.io())
    }

    private fun getRoleNames(userRole: UserRole): String {
        val roles = mutableListOf<String>()
        if (userRole.isYardOperator) roles.add("Yard Operator")
        if (userRole.isDrillingEngineer) roles.add("Drilling Engineer")
        return if (roles.size > 0) roles.joinToString(", ") else "No Role"
    }

    private fun updateTasks(tasks: List<TaskEntity>): Completable {
        val AdHocActionTasks = listOf(
            AD_HOC_INBOUND_TO_WELL,
            AD_HOC_INBOUND_FROM_MILL,
            AD_HOC_INBOUND_FROM_WELL_SITE,
            AD_HOC_DISPATCH_TO_YARD,
            AD_HOC_DISPATCH_TO_WELL
        )
        val taskIds = tasks.map { it.id }

        return tasksDao.deleteAllExcept(taskIds, AdHocActionTasks)
            .andThen(cleanSubmittedAdHocActionTasks())
            .andThen(tasksDao.insert(tasks))
            .andThen(
                Observable.just(tasks).flatMapIterable()
                    .flatMapCompletable { task -> checkAndUpdateTaskStatus(task.id) }
            )
            .subscribeOn(Schedulers.io())
    }

    private fun insertAssets(assets: List<AssetEntity>): Completable {
        return assetsDao.insert(assets).subscribeOn(Schedulers.io())
    }

    private fun updateTraceEvents(traceEvents: List<TraceEventEntity>): Completable {
        return traceEventsDao.deleteSubmitted().andThen(traceEventsDao.insert(traceEvents))
            .subscribeOn(Schedulers.io())
    }

    private fun updateExpectedAmounts(expectedAmounts: List<ExpectedAmountEntity>): Completable {
        return expectedAmountsDao.deleteAllExcept(expectedAmounts.map { it.id })
            .andThen(expectedAmountsDao.insert(expectedAmounts))
            .subscribeOn(Schedulers.io())
    }

    private fun updateFacilities(newFacilities: List<FacilityEntity>): Completable {
        return facilitiesDao.getAllFacilities().flatMapCompletable { oldFacilities ->
            val facilitiesToBeDeleted = oldFacilities.filterNot { oldFacility ->
                newFacilities.any { newFacility -> newFacility.id == oldFacility.id }
            }
            facilitiesDao.insert(newFacilities)
                .andThen(checkAndDeleteUnlinkedFacilities(facilitiesToBeDeleted))
        }.subscribeOn(Schedulers.io())
    }

    private fun updateRackLocations(newRacks: List<RackLocationEntity>): Completable {
        return rackLocationsDao.getAllRackLocations().flatMapCompletable { oldRacks ->
            val rackIdsToBeDeleted = oldRacks.filterNot { oldRack ->
                newRacks.any { newRack -> newRack.id == oldRack.id }
            }
            rackLocationsDao.insert(newRacks)
                .andThen(checkAndDeleteUnlinkedRacks(rackIdsToBeDeleted))
        }.subscribeOn(Schedulers.io())
    }

    private fun updateProjects(newProjects: List<ProjectPartialEntity>): Completable {
        return projectsDao.getAllProjects().flatMapCompletable { oldProjects ->
            val projectIdsToBeDeleted = oldProjects.filterNot { oldProject ->
                newProjects.any { newProject -> oldProject.id == newProject.id }
            }.map { it.id }
            deleteProjects(projectIdsToBeDeleted).andThen(
                Observable.just(newProjects).flatMapIterable().flatMapCompletable { newProject ->
                    if (projectsDao.hasProject(newProject.id)) projectsDao.update(newProject)
                    else projectsDao.insert(newProject.toEntity())
                }
            )
        }.subscribeOn(Schedulers.io())
    }

    private fun insertConditions(conditions: List<ConditionEntity>): Completable {
        return conditionsDao.insert(conditions).subscribeOn(Schedulers.io())
    }

    private fun insertProjectConditions(projectConditions: List<ProjectConditionEntity>): Completable {
        return projectConditionsDao.insert(projectConditions).subscribeOn(Schedulers.io())
    }

    private fun insertProjectFacilities(projectFacilities: List<ProjectFacilityEntity>): Completable {
        return projectFacilitiesDao.insert(projectFacilities).subscribeOn(Schedulers.io())
    }

    private fun deleteProjects(projectIds: List<String>): Completable {
        return projectsDao.deleteProjectsByIds(projectIds)
            .andThen(tasksDao.deleteByProjectsIds(projectIds))
            .andThen(assetsDao.deleteByProjectsIds(projectIds))
            .andThen(projectConditionsDao.deleteByProjectIds(projectIds))
            .andThen(projectFacilitiesDao.deleteByProjectIds(projectIds))
            .subscribeOn(Schedulers.io())
    }

    private fun checkAndDeleteUnlinkedFacilities(facilities: List<FacilityEntity>): Completable {
        return Observable.just(facilities).flatMapIterable().flatMapCompletable { facility ->
            facilitiesDao.checkFacilityAssociation(facility.id).flatMapCompletable { isAssociated ->
                if (isAssociated) facilitiesDao.setFacilitySelectabilityToFalse(facility.id)
                else facilitiesDao.delete(facility)
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun checkAndDeleteUnlinkedRacks(racks: List<RackLocationEntity>): Completable {
        return Observable.just(racks).flatMapIterable().flatMapCompletable { rack ->
            rackLocationsDao.checkRackAssociation(rack.id).flatMapCompletable { isAssociated ->
                if (isAssociated) rackLocationsDao.setRackLocationSelectabilityToFalse(rack.id)
                else tasksDao.updateDefaultRackLocationToNull(rack.id)
                    .andThen(rackLocationsDao.delete(rack))
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun formatTime(dateTime: ZonedDateTime?): String {
        return (dateTime ?: ZonedDateTime.now(ZoneId.of("UTC"))).toString()
            .substringBefore("[UTC]")
    }

    private fun sendError(exception: Throwable) {
        exception.sendErrorToDtrace(this.javaClass.name)
    }

    private fun checkAndUpdateTaskStatus(taskId: String): Completable {
        return Single.zip(
            tasksDao.getById(taskId),
            traceEventsDao.getTraceEventsForTask(taskId),
            { task, traceEvents -> Pair(task, traceEvents) }
        ).flatMapCompletable { (task, traceEvents) ->
            when {
                traceEvents.isEmpty() && task.status != NOT_STARTED ->
                    tasksDao.updateStatus(taskId, NOT_STARTED)
                traceEvents.isNotEmpty() && task.status == NOT_STARTED ->
                    tasksDao.updateStatus(taskId, IN_PROGRESS)
                else -> Completable.complete()
            }
        }
    }
}
