package com.scgts.sctrace.ui.components

import com.airbnb.epoxy.TypedEpoxyController
import com.scgts.sctrace.base.model.AssetCardUiModel

class SelectableAssetsEpoxyController(
    val selectAsset: (String) -> Unit,
    val deselectAsset: (String) -> Unit,
    val isTablet: Boolean = false,
    val clickListener: AssetClickListener
) : TypedEpoxyController<List<AssetCardUiModel>>() {

    override fun isStickyHeader(position: Int) = position == position + 1

    override fun buildModels(assets: List<AssetCardUiModel>) {

        selectableAssetHeaderRow {
            id("selectable header")
            selected(assets.all { it.checked == true } && assets.isNotEmpty())
            toggleAllSelected { selected ->
                if (selected) {
                    assets.forEach { selectAsset(it.id) }
                } else {
                    assets.forEach { deselectAsset(it.id) }
                }
            }
        }

        if (assets.isEmpty()) {
            emptyAssetsRow {
                id("empty assets")
                tablet(isTablet)
            }
        } else {
            assets.forEachIndexed { index, asset ->
                selectableAssetRow {
                    id("$asset$index")
                    asset(asset)
                    position(index + 1)
                    selected(asset.checked!!)
                    toggleSelected { selected ->
                        if (selected) selectAsset(asset.id) else deselectAsset(asset.id)
                    }
                    clickListener(clickListener)
                }
            }
        }
    }
}