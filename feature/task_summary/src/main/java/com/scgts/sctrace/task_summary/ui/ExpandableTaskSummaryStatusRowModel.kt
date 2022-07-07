package com.scgts.sctrace.task_summary.ui

import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowExpandableTaskSummaryStatusBinding

@EpoxyModelClass
abstract class ExpandableTaskSummaryStatusRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    lateinit var field: String

    @EpoxyAttribute
    lateinit var value: String

    @EpoxyAttribute
    @ColorRes
    var statusColor: Int = 0

    override fun getDefaultLayout() = R.layout.row_expandable_task_summary_status

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowExpandableTaskSummaryStatusBinding.bind(view)
        with(binding) {
            summaryStatusField.text = field
            summaryStatusValue.text = value
            summaryStatusValue.background.setTint(ContextCompat.getColor(view.context, statusColor))
        }
    }
}