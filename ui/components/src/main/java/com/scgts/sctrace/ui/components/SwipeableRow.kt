package com.scgts.sctrace.ui.components

import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyModel
import com.scgts.sctrace.base.model.AssetCardUiModel

abstract class SwipeableRow : EpoxyModel<ConstraintLayout>() {
    abstract val asset: AssetCardUiModel
}