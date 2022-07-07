package com.scgts.sctrace.capture.tag_conflict

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.AssetCardUiModel

interface ConflictHandlerMvi {
    sealed class Intent : MviIntent {

        //view intents
        data class AssetClicked(val assetId: String) : Intent()
        object CancelClicked : Intent()
        object ExpandClicked : Intent()

        //data intents
        data class Assets(val assets: List<AssetCardUiModel>) : Intent()
    }

    data class ViewState(
        val assets: List<AssetCardUiModel> = emptyList(),
        val expanded: Boolean = false,
        override val error: Throwable? = null,
        override val loading: Boolean = false
    ) : MviViewState
}