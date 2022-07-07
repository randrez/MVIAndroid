package com.scgts.sctrace.assets.consumption

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.Reason
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType

interface ConsumptionIntent {
    sealed class Intent : MviIntent {
        data class AssetData(val pipeNo: String): Intent()
        object DiscardClick: Intent()
        object RejectClick: Intent()
        data class ReasonSelected(val reason: Reason): Intent()
        data class CommentUpdated(val comment: String): Intent()

        data class InputUpdated(val reason: String?) : Intent()
    }

    data class ViewState(
        val pipeNo: String = "",
        val reason: String? = null,
        val statusChange: ConsumptionSwitchType? = null,
        override val error: Throwable? = null,
        override val loading: Boolean = false
    ): MviViewState
}