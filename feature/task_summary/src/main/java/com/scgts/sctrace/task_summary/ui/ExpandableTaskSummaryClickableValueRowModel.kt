package com.scgts.sctrace.task_summary.ui

import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowExpandableTaskSummaryBinding

@EpoxyModelClass
abstract class ExpandableTaskSummaryClickableValueRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    lateinit var field: String

    @EpoxyAttribute
    lateinit var value: String

    @EpoxyAttribute
    lateinit var clickListener: () -> Unit

    override fun getDefaultLayout() = R.layout.row_expandable_task_summary_clickable_value

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowExpandableTaskSummaryBinding.bind(view)
        with(binding) {
            summaryField.text = field
            summaryValue.text = value
            summaryValue.setOnClickListener { clickListener() }
        }
    }
}
