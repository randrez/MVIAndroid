package com.scgts.sctrace.capture

import com.airbnb.epoxy.TypedEpoxyController
import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.ui.components.IconTextRowModel
import com.scgts.sctrace.ui.components.iconTextRow

class CaptureEpoxyController(
    private val listener: IconTextRowModel.IconTextRowClickListener
) : TypedEpoxyController<List<CaptureMethod>>() {

    override fun buildModels(data: List<CaptureMethod>) {
        data.forEachIndexed { index, captureMethod ->
            iconTextRow {
                id("$captureMethod$index")
                label(captureMethod.name)
                iconResId(captureMethod.iconResource())
                showDivider(true)
                listener(listener)
            }
        }
    }
}
