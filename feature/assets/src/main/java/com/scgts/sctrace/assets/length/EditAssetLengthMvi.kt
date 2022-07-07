package com.scgts.sctrace.assets.length

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.Length
import com.scgts.sctrace.base.model.Length.Companion.emptyLength
import com.scgts.sctrace.base.model.UnitType

interface EditAssetLengthMvi {
    sealed class Intent : MviIntent {
        //view intents
        data class IsEditing(val isEditing: Boolean) : Intent()
        data class SaveLength(val length: Double) : Intent()
        object Done : Intent()

        //data intents
        data class SetLength(val length: Length) : Intent()
    }

    data class ViewState(
        val isEditing: Boolean = false,
        val length: Length = emptyLength(),
        val unitType: UnitType,
        override val error: Throwable? = null,
        override val loading: Boolean = false
    ) : MviViewState
}