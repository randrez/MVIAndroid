package com.scgts.sctrace.rack_transfer.transferselection

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.base.model.TextEntry

interface RackTransferSelectionMvi {
    sealed class Intent : MviIntent {
        //view intents
        object CloseClicked : Intent()
        object ScanClicked : Intent()
        object CreateClicked : Intent()
        data class RackLocationSelected(val rackLocation: RackLocation?) : Intent()
        data class OnAssetDeleteClicked(val id: String) : Intent()

        //data intents
        data class Assets(val assetsList: List<AssetCardUiModel>) : Intent()
        data class RackLocations(val locations: List<RackLocation>) : Intent()
        data class SetSelectedRackLocation(val rackLocation: RackLocation?) : Intent()
        data class SetSummaryList(val summaryList: List<TextEntry>) : Intent()
    }

    data class ViewState(
        val rackLocations: List<RackLocation> = emptyList(),
        val assetsList: List<AssetCardUiModel> = emptyList(),
        val summaryList: List<TextEntry> = emptyList(),
        val selectedRackLocation: RackLocation = RackLocation("", "Choose location"),
        val createEnabled: Boolean = false,
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState
}