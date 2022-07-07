package com.scgts.sctrace.tasks.mappers

import com.scgts.sctrace.base.model.Asset
import com.scgts.sctrace.base.model.AssetRest
import com.scgts.sctrace.base.util.inchesToFeet
import com.scgts.sctrace.database.model.AssetEntity

fun AssetEntity.uiModel(
    expectedInOrder: Boolean = true,
    consumed: Boolean? = null,
    checkedForOutbound: Boolean? = null,
    traceEventLength: Double? = null,
    traceEventConditionId: String? = null,
    traceEventRackLocationId: String? = null
) =
    Asset(
        id = id,
        pipeNumber = pipeNumber,
        length = traceEventLength ?: length,
        weight = weight,
        millName = millName,
        rackLocationId = traceEventRackLocationId ?: rackLocationId,
        tags = tags,
        runningLength = runningLength,
        heatNumber = heatNumber,
        commodity = commodity,
        outerDiameter = outerDiameter,
        grade = grade,
        range = range,
        endFinish = endFinish,
        millWorkNumber = millWorkNumber,
        productId = productId,
        expectedInOrder = expectedInOrder,
        consumed = consumed,
        checkedForOutbound = checkedForOutbound,
        makeUpLossFt = makeUpLossFt,
        conditionId = traceEventConditionId ?: conditionId,
        shipmentNumber = shipmentNumber,
        contractNumber = contractNumber,
        projectId = projectId,
    )

fun AssetRest.toEntity() =
    AssetEntity(
        id = id,
        pipeNumber = pipe_number.toString(),
        length = length,
        runningLength = running_length ?: 0.0,
        heatNumber = heat_number,
        outerDiameter = outer_diameter_in,
        endFinish = end_finish,
        grade = grade_type,
        range = range,
        weight = standard_weight_lbs_ft,
        projectId = project_id,
        millName = mill_name,
        rackLocationId = latest_rack_location_id,
        tags = tags,
        millWorkNumber = mill_work_number,
        productId = product_id,
        commodity = commodity,
        exMillDate = ex_mill_date,
        makeUpLossFt = make_up_loss_in?.inchesToFeet() ?: 0.0,
        conditionId = latest_condition_id,
        shipmentNumber = shipment_number,
        contractNumber = contract_number,
    )
