package com.scgts.sctrace.ui.components

import android.widget.CheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowSelectableAssetHeaderBinding

@EpoxyModelClass
abstract class SelectableAssetHeaderRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    lateinit var toggleAllSelected: (Boolean) -> Unit

    @EpoxyAttribute
    var selected: Boolean = false

    override fun getDefaultLayout() = R.layout.row_selectable_asset_header

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowSelectableAssetHeaderBinding.bind(view)
        with(binding) {
            checkbox.isChecked = selected
            checkbox.setOnClickListener {
                toggleAllSelected((it  as CheckBox).isChecked)
            }
        }
    }
}