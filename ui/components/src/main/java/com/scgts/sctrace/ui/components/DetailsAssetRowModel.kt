package com.scgts.sctrace.ui.components

import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowDetailsAssetBinding

@EpoxyModelClass
abstract class DetailsAssetRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    var number: Int = 0

    @EpoxyAttribute
    lateinit var name: String

    @EpoxyAttribute
    lateinit var joint: String

    @EpoxyAttribute
    lateinit var length: String

    override fun getDefaultLayout() = R.layout.row_details_asset

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowDetailsAssetBinding.bind(view)
        with(binding) {
            assetNumber.text = "$number"
            assetName.text = name
            assetExpectedJoint.text = joint
            assetExpectedLength.text = length
        }
    }
}