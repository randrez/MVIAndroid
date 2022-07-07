package com.scgts.sctrace.ui.components

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R

@EpoxyModelClass
abstract class EmptyAssetsRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    var tablet: Boolean = false
    override fun getDefaultLayout() = R.layout.row_empty_assets

    override fun bind(view: ConstraintLayout) {
        super.bind(view)
        if (tablet) {
            view.findViewById<TextView>(R.id.empty_instructions).text =
                view.context.getString(R.string.tap_bottom_left_to_scan)
        }
    }
}