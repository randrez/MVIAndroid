package util

import com.scgts.sctrace.base.model.Asset
import com.scgts.sctrace.base.model.AssetDetail
import com.scgts.sctrace.base.model.Length
import com.scgts.sctrace.base.model.UnitType
import com.scgts.sctrace.root.components.R

fun Asset.toAssetDetailList(
    condition: String? = null,
    rackLocation: String? = null,
    unitType: UnitType,
    showShipmentContract: Boolean = false
): List<AssetDetail> {
    return mutableListOf<AssetDetail>().apply {
        add(AssetDetail(R.string.mill_work_no, millWorkNumber))
        if (showShipmentContract) {
            add(AssetDetail(R.string.contract_id, contractNumber))
        }
        add(AssetDetail(R.string.heat_no, heatNumber))
        add(AssetDetail(R.string.pipe_no, pipeNumber))
        if (showShipmentContract) {
            add(AssetDetail(R.string.shipment_no, shipmentNumber ?: ""))
        }
        add(AssetDetail(R.string.condition, condition ?: "Unknown"))
        add(AssetDetail(R.string.length, Length(length, unitType).getFormattedLengthString()))
        add(AssetDetail(R.string.rack_location, rackLocation ?: ""))
        add(AssetDetail(R.string.no_of_tags, tags.size))
        add(AssetDetail(R.string.outside_diameter, outerDiameter))
        add(AssetDetail(R.string.weight, weight))
        add(AssetDetail(R.string.grade, grade))
        add(AssetDetail(R.string.end_finish, endFinish))
        add(AssetDetail(R.string.commodity, commodity))
        add(AssetDetail(R.string.running_length, Length(runningLength, unitType).getFormattedLengthString()))
    }.toList()
}