package com.scgts.sctrace.rack_transfer.rackdetails

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.base.model.RackTransferModel
import com.scgts.sctrace.base.model.Task

interface RackDetailsMvi {
    sealed class Intent : MviIntent {
        //view intents
        object BackClicked : Intent()
        data class AssetClicked(val assetId: String) : Intent()

        //data intents
        data class Assets(val assets: List<AssetCardUiModel>) :
            Intent()

        data class RackTransferData(val assetsRackTransfer: RackTransferModel) : Intent()
        object NoOp : Intent()
    }

    data class ViewState(
        val task: Task? = null,
        val assets: List<AssetCardUiModel> = emptyList(),
        val assetsRackTransfer: RackTransferModel? = null,
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState

}