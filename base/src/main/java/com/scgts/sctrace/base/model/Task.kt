package com.scgts.sctrace.base.model

import com.scgts.sctrace.base.model.TaskType.*

data class Task(
    val id: String,
    val type: TaskType,
    val status: TaskStatus,
    val totalExpectedLength: Double,
    val totalNumJoints: Int,
    val projectId: String,
    val orderId: String,
    val orderType: OrderType,
    val unitOfMeasure: UnitType,
    val specialInstructions: String?,
    val description: String?,
    val toLocationId: String?,
    val toLocationName: String?,
    val fromLocationId: String?,
    val fromLocationName: String?,
    val arrivalDate: String?,
    val deliveryDate: String?,
    val dispatchDate: String?,
    val defaultRackLocationId: String?,
    val wellSection: String?,
    val organizationName: String?,
    val assetIdsFromPreviousTask: List<String> = emptyList(),
) {
    companion object {
        val adHocActionIds = listOf(
            AD_HOC_QUICK_SCAN,
            AD_HOC_INBOUND_TO_WELL,
            AD_HOC_INBOUND_FROM_MILL,
            AD_HOC_INBOUND_FROM_WELL_SITE,
            AD_HOC_DISPATCH_TO_YARD,
            AD_HOC_DISPATCH_TO_WELL,
            AD_HOC_REJECT_SCAN,
            AD_HOC_RACK_TRANSFER
        )

        val swipeToEditEnabledTasks = listOf(
            INBOUND_FROM_MILL,
            INBOUND_FROM_WELL_SITE,
            AD_HOC_INBOUND_FROM_MILL,
            AD_HOC_INBOUND_FROM_WELL_SITE,
        )
    }

    fun isAdHocAction() = adHocActionIds.contains(type)

    fun swipeToEditEnabled() = swipeToEditEnabledTasks.contains(type)

    fun orderAndTask() =
        if (isAdHocAction()) type.displayName else "${orderType.displayName} / ${type.displayName}"

    // Some tasks should display the to location some should display their description
    fun descriptionOrLocation(): String? {
        return when (type) {
            INBOUND_TO_WELL, INBOUND_FROM_WELL_SITE -> toLocationName
            else -> description
        }
    }
}