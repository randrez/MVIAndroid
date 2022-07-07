package com.scgts.sctrace.ui.components

import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowExpandableSectionBinding

@EpoxyModelClass
abstract class ExpandableSectionRowModel: EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    lateinit var name: String
    @EpoxyAttribute
    var expanded: Boolean = false
    @EpoxyAttribute
    lateinit var clickListener: () -> Unit

    override fun getDefaultLayout() = R.layout.row_expandable_section

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowExpandableSectionBinding.bind(view)
        with(binding) {
            sectionName.text = name
            val iconRes = if (expanded) R.drawable.ic_scgts_collapse else R.drawable.ic_scgts_expand
            expandCollapseIcon.setImageResource(iconRes)
            root.setOnClickListener { clickListener() }
        }
    }
}