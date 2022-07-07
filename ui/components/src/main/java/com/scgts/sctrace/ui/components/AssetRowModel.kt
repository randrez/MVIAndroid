package com.scgts.sctrace.ui.components

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowAssetBinding

@EpoxyModelClass
abstract class AssetRowModel : SwipeableRow() {
    @EpoxyAttribute
    override lateinit var asset: AssetCardUiModel

    @EpoxyAttribute
    var position: Int = 0

    @EpoxyAttribute
    var showMillWorkNum: Boolean = false

    @EpoxyAttribute
    var clickListener: AssetClickListener? = null

    override fun getDefaultLayout() = R.layout.row_asset

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowAssetBinding.bind(view)
        with(binding) {
            assetNumber.text = position.toString()
            assetNotInOrderWarningIndicator.isVisible = !asset.expectedInOrder
            assetName.text = asset.name
            assetHeat.text = if (showMillWorkNum) {
                view.context.getString(R.string.mill_work_no_format, asset.millWorkNum)
            } else {
                view.context.getString(R.string.heat_no_format, asset.heatNumber)
            }
            assetPipeNumber.text = view.context.getString(R.string.pipe_no_format, asset.pipeNumber)
            assetTally.text = String.format("%.2f", asset.tally)
            assetTags.text = "${asset.numTags}"
        }
        view.setOnClickListener { clickListener?.assetClick(asset.id) }
    }
}
