package com.scgts.sctrace.assets.confirmation


import com.airbnb.epoxy.TypedEpoxyController

class SearchableBottomSheetController(
    val onItemClicked: (SearchableBottomSheetData) -> Unit
) : TypedEpoxyController<List<SearchableBottomSheetData>>() {
    override fun buildModels(data: List<SearchableBottomSheetData>) {

        data.forEachIndexed { index, item ->
            searchableBottomSheetRow {
                id("$item$index")
                item(item)
                clickListener { onItemClicked(item) }
            }
        }
    }
}