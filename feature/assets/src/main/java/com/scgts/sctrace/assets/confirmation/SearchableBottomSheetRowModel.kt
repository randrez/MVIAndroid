package com.scgts.sctrace.assets.confirmation

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.assets.confirmation.databinding.RowSearchableBottomSheetBinding

@EpoxyModelClass
abstract class SearchableBottomSheetRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    lateinit var item: SearchableBottomSheetData

    @EpoxyAttribute
    lateinit var clickListener: () -> Unit

    override fun bind(view: ConstraintLayout) {
        super.bind(view)
        val binding = RowSearchableBottomSheetBinding.bind(view)

        with(binding) {
            rowCheckableDivider.isVisible = true
            rowCheckableLabel.text = item.name
            root.setOnClickListener { clickListener() }
        }
    }

    override fun getDefaultLayout() = R.layout.row_searchable_bottom_sheet
}