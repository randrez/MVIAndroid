package com.scgts.sctrace.capture.manual.byattribute

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.capture.R
import com.scgts.sctrace.base.model.AssetAttribute

@EpoxyModelClass
abstract class AttributeOptionRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute lateinit var label: String
    @EpoxyAttribute lateinit var attribute: AssetAttribute
    @EpoxyAttribute var selected: Boolean = false
    @EpoxyAttribute var showDivider: Boolean = false
    @EpoxyAttribute(DoNotHash) lateinit var listener: AttributeSelectListener

    override fun getDefaultLayout() = R.layout.row_text_checkable

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        //TODO: update with data binding
        with(view) {
            findViewById<TextView>(R.id.row_checkable_divider).isVisible = showDivider == true
            val labelFormatted = if(attribute == AssetAttribute.ExMillDate) {
                label.substring(0, label.indexOf("T"))
            } else label
            findViewById<TextView>(R.id.row_checkable_label).text = labelFormatted
            findViewById<TextView>(R.id.row_checkable_check).isVisible = selected == true
            setOnClickListener { listener.onClick(attribute, label) }
        }
    }

    interface AttributeSelectListener {
        fun onClick(attribute: AssetAttribute, selectedOption: String)
    }
}

