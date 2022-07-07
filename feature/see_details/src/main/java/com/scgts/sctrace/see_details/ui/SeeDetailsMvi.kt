package com.scgts.sctrace.see_details.ui

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.AssetProductInformation
import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.base.model.Task
import com.scgts.sctrace.base.model.UnitType
import com.scgts.sctrace.ui.components.ExpandableRow

interface SeeDetailsMvi {
    sealed class Intent : MviIntent {
        // view intents
        data class ToggleRowExpanded(val row: ExpandableRow) : Intent()
        object XClicked : Intent()

        // data intents
        data class TaskData(val task: Task) : Intent()
        data class UnitTypeUpdate(val unitType: UnitType) : Intent()
        data class CaptureMethodUpdate(val captureMethod: CaptureMethod) : Intent()
        data class AssetProductData(val info: List<AssetProductInformation>) : Intent()
        data class HideInstructions(val hideInstructions: Boolean) : Intent()
        data class TaskTypeValidation(val isDispatchOrBuildOrder: Boolean) : Intent()
    }

    data class ViewState(
        override val error: Throwable? = null,
        override val loading: Boolean = false,
        val task: Task? = null,
        val unitType: UnitType = UnitType.FEET,
        val captureMethod: CaptureMethod = CaptureMethod.Camera,
        val assetProductDescription: List<AssetProductInformation> = emptyList(),
        val instructionsExpanded: Boolean = false,
        val assetsExpanded: Boolean = false,
        val hideInstructions: Boolean = false,
        val isDispatchOrBuildOrder: Boolean = false,
    ) : MviViewState
}
