package com.scgts.sctrace.ui.components

import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowExpandButtonBinding

@EpoxyModelClass
abstract class ExpandButtonRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    lateinit var clickListener: () -> Unit

    @EpoxyAttribute
    @DrawableRes
    var imageSrc: Int = 0

    override fun getDefaultLayout() = R.layout.row_expand_button

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowExpandButtonBinding.bind(view)
        with(binding) {
            expandCardButton.setImageResource(imageSrc)
            expandCardButton.setOnClickListener {
                clickListener()
            }
        }
    }
}