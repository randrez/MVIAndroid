package com.scgts.sctrace.rack_transfer.edittransfer

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.base.model.TextEntry

interface EditRackTransferMvi {
    sealed class Intent : MviIntent {
        //view intents
        object CloseClicked : Intent()
        object SaveClicked : Intent()
        data class OnAssetDeleteClicked(val id: String) : Intent()
        data class RackLocationSelected(val rackLocation: RackLocation?) : Intent()

        //data intents
        data class UpdateRackLocation(val rackLocation: RackLocation?): Intent()
        data class RackLocations(val locations: List<RackLocation>): Intent()
        data class Assets(val rackTransferAssetList: List<AssetCardUiModel>) : Intent()
        data class SetSummaryList(val summaryList: List<TextEntry>) : Intent()
    }

    data class ViewState(
        val rackLocations: List<RackLocation> = emptyList(),
        val summaryList: List<TextEntry> = emptyList(),
        val assetsList: List<AssetCardUiModel> = emptyList(),
        val totalTally: String = "0 JT / 0 FT",
        val selectedRackLocation: RackLocation = RackLocation(id="", name ="Choose location"),
        override val error: Throwable? = null,
        override val loading: Boolean = false
    ) : MviViewState
}