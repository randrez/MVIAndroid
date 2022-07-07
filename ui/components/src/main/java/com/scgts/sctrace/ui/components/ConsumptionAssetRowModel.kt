package com.scgts.sctrace.ui.components

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowConsumptionAssetBinding

@EpoxyModelClass
abstract class ConsumptionAssetRowModel : SwipeableRow() {
    @EpoxyAttribute
    override lateinit var asset: AssetCardUiModel

    @EpoxyAttribute
    var position: Int = 0

    @EpoxyAttribute
    var showMillWorkNum: Boolean = false

    @EpoxyAttribute
    var clickListener: AssetClickListener? = null

    override fun getDefaultLayout() = R.layout.row_consumption_asset

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowConsumptionAssetBinding.bind(view)
        with(binding) {
            assetConsNumber.text = position.toString()
            assetConsNotInOrderWarningIndicator.isVisible = !asset.expectedInOrder
            assetConsName.text = asset.name
            assetConsHeat.text = if (showMillWorkNum) {
                view.context.getString(R.string.mill_work_no_format, asset.millWorkNum)
            } else {
                view.context.getString(R.string.heat_no_format, asset.heatNumber)
            }
            assetConsPipeNumber.text =
                view.context.getString(R.string.pipe_no_format, asset.pipeNumber)
            assetConsTally.text = String.format("%.2f", asset.tally)

            val tint = when (asset.consumed) {
                true -> R.color.green
                false -> R.color.red
                else -> R.color.red
            }
            assetConsStatus.background.setTint(ContextCompat.getColor(view.context, tint))
            assetConsStatus.text = view.context.getString(
                if (asset.consumed == true) R.string.consume else R.string.reject
            )
        }

        view.setOnClickListener { clickListener?.assetClick(asset.id) }
    }
}