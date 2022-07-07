package com.scgts.sctrace.tasks.mappers

import ProjectsQuery
import com.scgts.sctrace.database.model.ExpectedAmountEntity

fun ProjectsQuery.Expected_amount.toEntity(): ExpectedAmountEntity =
    ExpectedAmountEntity(
        id = id,
        orderId = order_id,
        productId = product_id,
        productDescription = product.calculated_product_description,
        expectedTally = length,
        expectedJoints = number_joints,
        contractNumber = contract_id,
        shipmentNumber = shipment_number,
        conditionId = condition_id,
        rackLocationId = facility_location_id
    )
