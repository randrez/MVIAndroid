package com.scgts.sctrace.ui.components

import com.airbnb.epoxy.TypedEpoxyController
import com.scgts.sctrace.base.model.AssetCardUiModel

class AssetsEpoxyController(
    val showEmptyState: Boolean = true,
    val isTablet: Boolean = false,
    val conflictHandling: Boolean = false,
    val clickListener: AssetClickListener? = null
) : TypedEpoxyController<List<AssetCardUiModel>>() {

    override fun isStickyHeader(position: Int) = position == position + 1

    override fun buildModels(data: List<AssetCardUiModel>) {
        if (data.isNotEmpty()) {
            buildAssets(data)
        } else if (showEmptyState) {
            emptyAssetsRow {
                id("empty assets")
                tablet(isTablet)
            }
        }
    }

    fun containsUnexpectedProduct(): Boolean = currentData?.any { !it.expectedInOrder } ?: false
}

fun AssetsEpoxyController.buildAssets(assets: List<AssetCardUiModel>) {
    assets.forEachIndexed { index, asset ->
        if(index == 0) {
            if(asset.consumed != null) {
                consumptionAssetHeaderRow {
                    id("header row")
                }
            } else {
                assetHeaderRow {
                    id("header row")
                }
            }
        }

        if(asset.consumed == null) {
            assetRow {
                id("$asset$index")
                asset(asset)
                position(assets.size - index)
                showMillWorkNum(conflictHandling)
                clickListener(clickListener)
            }
        } else {
            consumptionAssetRow {
                id("$asset$index")
                asset(asset)
                position(assets.size - index)
                clickListener(clickListener)
            }
        }
    }
}


interface AssetClickListener {
    fun assetClick(assetId: String)
}
