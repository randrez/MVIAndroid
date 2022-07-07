package com.scgts.sctrace.assets.detail

import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.scgts.sctrace.assets.confirmation.R
import com.scgts.sctrace.assets.confirmation.databinding.AssetDetailRowBinding
import com.scgts.sctrace.base.model.AssetDetail

class AssetDetailController : TypedEpoxyController<List<AssetDetail>>() {
    override fun buildModels(data: List<AssetDetail>) {
        data.forEach {
            assetDetailRow {
                id(it.label)
                label(it.label)
                value(it.value)
            }
        }
    }
}

@EpoxyModelClass
abstract class AssetDetailRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    @StringRes
    var label = 0

    @EpoxyAttribute
    var value = ""
    override fun getDefaultLayout(): Int = R.layout.asset_detail_row

    override fun bind(view: ConstraintLayout) {
        super.bind(view)
        AssetDetailRowBinding.bind(view).apply {
            assetDetailLabel.setText(label)
            assetDetailValue.text = value
        }
    }
}