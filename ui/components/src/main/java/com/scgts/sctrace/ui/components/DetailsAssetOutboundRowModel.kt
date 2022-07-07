package com.scgts.sctrace.ui.components

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowDetailsAssetOutboundBinding

@EpoxyModelClass
abstract class DetailsAssetOutboundRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    var number: Int = 0

    @EpoxyAttribute
    lateinit var name: String

    @EpoxyAttribute
    lateinit var joint: String

    @EpoxyAttribute
    lateinit var length: String

    @EpoxyAttribute
    lateinit var contractNumber: String

    @EpoxyAttribute
    lateinit var shipmentNumber: String

    @EpoxyAttribute
    var percentCompleteRow: Int = 0

    @EpoxyAttribute
    lateinit var conditionName: String

    @EpoxyAttribute
    lateinit var rackLocationName: String

    override fun getDefaultLayout() = R.layout.row_details_asset_outbound

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowDetailsAssetOutboundBinding.bind(view)
        with(binding) {
            assetNumber.text = "$number"
            assetName.text = name
            assetExpectedJoint.text = joint
            assetExpectedLength.text = length

            if (contractNumber.isNotEmpty()) {
                contractNumberTextView.isVisible = true
                contractNumberTextView.text =
                    view.context.getString(R.string.contract_no_format, contractNumber)
            }

            if (shipmentNumber.isNotEmpty()) {
                shipmentNumberTextView.isVisible = true
                shipmentNumberTextView.text =
                    view.context.getString(R.string.shipment_no_format, shipmentNumber)
            }

            if (conditionName.isNotEmpty()) {
                conditionNameTextView.isVisible = true
                conditionNameTextView.text =
                    view.context.getString(R.string.condition_format, conditionName)
            }

            if (rackLocationName.isNotEmpty()) {
                rackLocationNameTextView.isVisible = true
                rackLocationNameTextView.text =
                    view.context.getString(R.string.rack_location_format, rackLocationName)
            }

            progressBarDetail.progress = percentCompleteRow
        }
    }
}