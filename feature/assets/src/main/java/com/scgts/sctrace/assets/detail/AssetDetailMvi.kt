package com.scgts.sctrace.assets.detail

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.AssetDetail

interface AssetDetailMvi {
    sealed class Intent : MviIntent {
        //view intents
        object BackClick : Intent()

        //data intents
        data class AssetData(
            val assetDetailList: List<AssetDetail>,
            val assetDescription: String,
        ) : Intent()
    }

    data class ViewState(
        val assetDetailList: List<AssetDetail> = emptyList(),
        val assetDescription: String = "",
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState
}