package com.scgts.sctrace.ui.components

import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowSpecialInstructionsBinding

@EpoxyModelClass
abstract class SpecialInstructionsRow: EpoxyModel<ConstraintLayout>() {

    @EpoxyAttribute
    var instructions: String? = null

    override fun getDefaultLayout() = R.layout.row_special_instructions

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowSpecialInstructionsBinding.bind(view)
        if(instructions.isNullOrEmpty()){
            binding.instructionsText.text = view.resources.getString(R.string.no_special_instructions_placeholder)
        }else{
            binding.instructionsText.text = instructions
        }
    }
}