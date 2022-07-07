package com.scgts.sctrace.capture.manual.byattribute

import com.airbnb.epoxy.TypedEpoxyController
import com.scgts.sctrace.capture.manual.ManualCaptureMvi

class SelectAttributeController(
    private val listener: AttributeOptionRowModel.AttributeSelectListener
) : TypedEpoxyController<ManualCaptureMvi.SelectorData>() {

    override fun buildModels(data: ManualCaptureMvi.SelectorData) {
        data.selections.forEachIndexed { index, selection ->
            attributeOptionRow {
                id("$selection$index")
                label(selection)
                attribute(data.attribute)
                selected(data.selected == selection)
                showDivider(true)
                listener(listener)
            }
        }
    }
}
