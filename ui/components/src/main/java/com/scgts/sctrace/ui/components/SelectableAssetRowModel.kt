package com.scgts.sctrace.ui.components

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowSelectableAssetBinding

@EpoxyModelClass
abstract class SelectableAssetRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    lateinit var asset: AssetCardUiModel
    @EpoxyAttribute
    var position: Int = 0
    @EpoxyAttribute
    var selected: Boolean = false
    @EpoxyAttribute
    lateinit var toggleSelected: (Boolean) -> Unit
    @EpoxyAttribute
    lateinit var clickListener: AssetClickListener

    override fun getDefaultLayout() = R.layout.row_selectable_asset

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowSelectableAssetBinding.bind(view)
        with(binding) {
            assetNumber.text = position.toString()
            assetNotInOrderWarningIndicator.isVisible = !asset.expectedInOrder
            assetName.text = asset.name
            assetHeat.text = view.context.getString(R.string.heat_no_format, asset.heatNumber)
            assetPipeNumber.text = view.context.getString(R.string.pipe_no_format, asset.pipeNumber)
            checkbox.isChecked = selected
            checkbox.setOnCheckedChangeListener { checkboxView, isChecked ->
                if (checkboxView.isPressed) toggleSelected(isChecked)
            }
            view.setOnClickListener { clickListener.assetClick(asset.id) }

        }
    }
}