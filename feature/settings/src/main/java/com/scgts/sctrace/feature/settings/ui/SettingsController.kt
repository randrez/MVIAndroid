package com.scgts.sctrace.feature.settings.ui

import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.scgts.sctrace.root.components.databinding.UserSettingsItemBinding
import com.scgts.sctrace.settings.R

class SettingsController(
    private val onItemClicked: UserSettingsModel.UserSettingsRowListener
) : TypedEpoxyController<List<SettingsAction>>() {
    override fun buildModels(data: List<SettingsAction>) {
        data.forEachIndexed { index, item ->
            userSettings {
                id("$item$index")
                name(item.name)
                iconResId(item.icon)
                clickListener(onItemClicked)
            }
        }
    }
}

@EpoxyModelClass
abstract class UserSettingsModel : EpoxyModel<CardView>() {
    @EpoxyAttribute
    lateinit var name: String

    @EpoxyAttribute
    @DrawableRes
    var iconResId: Int = 0

    @EpoxyAttribute
    lateinit var clickListener: UserSettingsRowListener

    override fun bind(view: CardView) {
        super.bind(view)

        with(UserSettingsItemBinding.bind(view)) {
            title.text = name
            icon.setBackgroundResource(iconResId)
            root.setOnClickListener { clickListener.onClick(name) }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.user_settings_item

    interface UserSettingsRowListener {
        fun onClick(label: String)
    }
}
