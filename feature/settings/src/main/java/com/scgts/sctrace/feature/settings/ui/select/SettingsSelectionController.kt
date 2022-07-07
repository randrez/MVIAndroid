package com.scgts.sctrace.feature.settings.ui.select

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.scgts.sctrace.root.components.databinding.UserSettingsSelectionItemBinding
import com.scgts.sctrace.settings.R

class SettingsSelectionController(
    private val onItemClicked: UserSettingsSelectionModel.UserSettingsSelectionRowListener,
) : TypedEpoxyController<List<String>>() {
    var selectedSetting: String = ""

    override fun buildModels(data: List<String>) {
        data.forEachIndexed { index, item ->
            userSettingsSelection {
                id("$item$index")
                value(item)
                selectedSetting(selectedSetting)
                clickListener(onItemClicked)
            }
        }
    }
}

@EpoxyModelClass
abstract class UserSettingsSelectionModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    lateinit var value: String

    @EpoxyAttribute
    lateinit var selectedSetting: String

    @EpoxyAttribute
    lateinit var clickListener: UserSettingsSelectionRowListener

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        with(UserSettingsSelectionItemBinding.bind(view)) {
            title.text = value
            selected.visibility = if (value == selectedSetting) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
            root.setOnClickListener {
                clickListener.onClick(value)
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.user_settings_selection_item

    interface UserSettingsSelectionRowListener {
        fun onClick(setting: String)
    }
}
