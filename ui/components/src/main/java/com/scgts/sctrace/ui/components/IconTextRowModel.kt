package com.scgts.sctrace.ui.components

import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowTextIconBinding

@EpoxyModelClass
abstract class IconTextRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute lateinit var label: String
    @EpoxyAttribute var showDivider: Boolean = false
    @EpoxyAttribute @DrawableRes var iconResId: Int = 0
    @EpoxyAttribute(DoNotHash) lateinit var listener: IconTextRowClickListener

    override fun getDefaultLayout() = R.layout.row_text_icon

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowTextIconBinding.bind(view)
        with(binding) {
            textRowLabel.apply {
                text = label
                setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
            }
            textRowDivider.isVisible = showDivider == true
            root.setOnClickListener { listener.onClick(label) }
        }
    }

    interface IconTextRowClickListener {
        fun onClick(label: String)
    }
}
