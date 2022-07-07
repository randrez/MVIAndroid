package com.scgts.sctrace.assets.confirmation

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.*

interface AssetConfirmationMvi {
    sealed class Intent : MviIntent {
        //view intents
        object Save : Intent()

        object DiscardClick : Intent()
        object ToggleClick : Intent()
        object TagClick : Intent()
        object LengthClick : Intent()

        data class ConditionSelected(val condition: Condition) : Intent()
        data class LocationSelected(val rackLocation: RackLocation) : Intent()

        //data intents
        data class TaskTypeData(val type: TaskType) : Intent()
        data class AssetData(val asset: Asset, val show: Boolean) : Intent()
        data class RackLocationData(val rackLocations: List<RackLocation>) : Intent()
        data class Conditions(val conditions: List<Condition>) : Intent()
        data class SetInputData(
            val condition: Condition?,
            val rackLocation: RackLocation?,
            val length: Length?,
        ) : Intent()

        data class UnitTypeData(val unitType: UnitType) : Intent()

        object Dismiss : Intent()
        object OfflineSubmission : Intent()
        object OfflineAcknowledged : Intent()

        object NoOp : Intent()
        data class SetRole(val userRole: UserRole) : Intent()
        object Submitted : Intent()
    }

    data class ViewState(
        val name: String = "",
        val isExpanded: Boolean = false,
        val typeOrderWarning: TypeWarnings = TypeWarnings.NO_WARNING,
        val dismiss: Boolean = false,
        val hasSubmitted: Boolean = false,
        val pipeName: String = "",
        val millWorkNo: String = "",
        val heatNumber: String = "",
        val pipeNumber: String = "",
        val numTags: Int = 0,
        val newAsset: Boolean,
        val assetDetailList: List<AssetDetail> = emptyList(),
        val selectedCondition: Condition? = null,
        val selectedLocation: RackLocation? = null,
        val laserLength: Length? = null,
        val unitType: UnitType,
        val conditions: List<Condition> = listOf(),
        val rackLocations: List<RackLocation> = listOf(),
        val isOfflineSubmitted: Boolean = false,
        val scannedTag: String? = null,
        val isAdHoc: Boolean,
        val taskType: TaskType? = null,
        val userRole: UserRole = UserRole(
            isDrillingEngineer = false,
            isYardOperator = false,
            isAuditor = false
        ),
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState
}
