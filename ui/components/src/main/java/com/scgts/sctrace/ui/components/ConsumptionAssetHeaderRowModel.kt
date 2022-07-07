package com.scgts.sctrace.ui.components

import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R

@EpoxyModelClass
abstract class ConsumptionAssetHeaderRowModel : EpoxyModel<ConstraintLayout>() {
    override fun getDefaultLayout() = R.layout.row_asset_consumption_header
}
