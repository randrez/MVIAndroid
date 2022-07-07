package com.scgts.sctrace.tasks.mappers

import ProjectsQuery
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.OrderType.*
import com.scgts.sctrace.base.model.SubmitStatus.SUBMITTED
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.base.util.formatTally
import com.scgts.sctrace.base.util.toZonedDateTime
import com.scgts.sctrace.database.model.TaskEntity
import com.scgts.sctrace.database.model.TraceEventEntity
import com.scgts.sctrace.tasks.R
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

fun TaskEntity.toUiModel() =
    Task(
        id = id,
        type = type,
        status = status,
        description = description,
        totalExpectedLength = totalExpectedLength,
        totalNumJoints = totalNumJoints,
        specialInstructions = specialInstructions,
        projectId = projectId,
        orderId = orderId,
        orderType = orderType,
        unitOfMeasure = UnitType[unitOfMeasure],
        toLocationId = toLocationId,
        toLocationName = toLocationName,
        fromLocationId = fromLocationId,
        fromLocationName = fromLocationName,
        arrivalDate = arrivalDate,
        deliveryDate = deliveryDate,
        dispatchDate = dispatchDate,
        defaultRackLocationId = defaultRackLocationId,
        wellSection = wellSection,
        organizationName = organizationName,
        assetIdsFromPreviousTask = assetIdsFromPreviousTask,
    )

fun ProjectsQuery.Mobile_task.toEntity(
    order: ProjectsQuery.Mobile_order,
    projectId: String,
    projectUoM: String,
    operatingParty: ProjectsQuery.Operating_party,
): TaskEntity {
    val taskType = TaskType[task_type]
    return TaskEntity(
        id = id,
        createdAt = (created_at as String?).toZonedDateTime()
            ?: ZonedDateTime.now(ZoneId.of("UTC")),
        type = taskType,
        typeForFiltering = taskType.typeForFiltering,
        description = order.description,
        totalExpectedLength = order.expected_total_amount.length,
        totalNumJoints = order.expected_total_amount.number_joints,
        specialInstructions = order.special_instructions,
        projectId = projectId,
        orderId = order.id,
        orderType = OrderType[order.order_type],
        unitOfMeasure = projectUoM,
        toLocationId = order.to_location?.id,
        toLocationName = order.to_location?.name,
        fromLocationId = order.from_location?.id,
        fromLocationName = order.from_location?.name,
        arrivalDate = null, // TODO: user server arrivalDate when available
        deliveryDate = order.delivery_date as String?,
        dispatchDate = order.dispatch_date as String?,
        status = TaskStatus[mobile_status],
        defaultRackLocationId = null, // This is only used for locally created ad hoc tasks
        wellSection = order.well_section?.od_section?.section,
        organizationName = operatingParty.organization_name,
        assetIdsFromPreviousTask = when {
            // Outbound dispatch tasks should include assets from associated outbound
            // build order task in the same order
            taskType == DISPATCH && !order.build_order_assets.isNullOrEmpty() ->
                order.build_order_assets?.let { assets -> assets.map { it!!.id } } ?: emptyList()
            // Inbound to well tasks (both outbound and return/transfer) should include
            // assets from associated dispatch task in the same order
            taskType == INBOUND_TO_WELL && !order.dispatch_assets.isNullOrEmpty() ->
                order.dispatch_assets?.let { assets -> assets.map { it!!.id } } ?: emptyList()
            else -> emptyList()
        }
    )
}

fun ProjectsQuery.Trace_event.toEntity(): TraceEventEntity =
    TraceEventEntity(
        taskId = task_id,
        assetId = asset_id,
        submitStatus = SUBMITTED,
        // TODO: facilityId should come back as non-nullable from server eventually
        facilityId = facility_id ?: "",
        updatedAt = (updated_at as String?).toZonedDateTime(),
        consumed = consumption_status?.let {
            it == ConsumptionStatus.CONSUME.status
        },
        userId = scanner_user_id,
        userName = scanner_user_name ?: ""
    )

fun Task.toTaskCardUiModel(showWarningIcon:Boolean,  tallies: Tallies): TaskCardUiModel {
    val wellSectionEntry = TextEntry(R.string.well_section, wellSection ?: "")
    val fromLocationEntry = TextEntry(R.string.from, fromLocationName ?: "")
    val toLocationEntry = TextEntry(R.string.to, toLocationName ?: "")
    val totalTallyEntry = TextEntry(
        label = R.string.total_tally,
        body = formatTally(tallies.total, tallies.totalJoints, unitOfMeasure, 2)
    )
    val totalConsumedEntry = TextEntry(
        label = R.string.total_consumed,
        body = formatTally(tallies.totalConsumed, tallies.consumedJoints, unitOfMeasure, 2)
    )
    val expectedTallyEntry = TextEntry(
        label = R.string.expected_tally,
        body = formatTally(totalExpectedLength, totalNumJoints, unitOfMeasure, 2)
    )
    val defaultEntries = Pair(toLocationEntry, totalTallyEntry)
    val (location, tally) = when (orderType) {
        INBOUND -> when (type) {
            INBOUND_FROM_MILL -> Pair(fromLocationEntry, expectedTallyEntry)
            else -> defaultEntries
        }
        OUTBOUND -> when (type) {
            BUILD_ORDER, DISPATCH -> Pair(toLocationEntry, expectedTallyEntry)
            INBOUND_TO_WELL -> Pair(fromLocationEntry, expectedTallyEntry)
            else -> defaultEntries
        }
        CONSUMPTION -> when (type) {
            CONSUME -> Pair(wellSectionEntry, totalConsumedEntry)
            else -> defaultEntries
        }
        RETURN_TRANSFER -> when (type) {
            DISPATCH, DISPATCH_TO_YARD, DISPATCH_TO_WELL, RACK_TRANSFER ->
                Pair(toLocationEntry, totalTallyEntry)
            INBOUND_FROM_MILL, INBOUND_TO_WELL, INBOUND_FROM_WELL_SITE ->
                Pair(fromLocationEntry, totalTallyEntry)
            else -> defaultEntries
        }
        else -> defaultEntries
    }
    return TaskCardUiModel(
        id = id,
        label = orderAndTask(),
        name = descriptionOrLocation() ?: "",
        status = status,
        descriptionOne = location,
        descriptionTwo = tally,
        showWarningIcon = showWarningIcon
    )
}