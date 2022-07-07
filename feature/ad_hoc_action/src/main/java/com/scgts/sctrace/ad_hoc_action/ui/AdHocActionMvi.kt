package com.scgts.sctrace.ad_hoc_action.ui

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.DispatchType.AWAITING_SELECTION
import org.threeten.bp.ZonedDateTime

interface AdHocActionMvi {

    sealed class Intent : MviIntent {
        //data intents
        data class ProjectsData(val projects: List<Project>) : Intent()
        data class LocationsData(val locations: List<RackLocation>) : Intent()
        data class SetInputList(
            val yards: List<Facility>,
            val rigs: List<Facility>,
            val wells: List<Facility>,
        ) : Intent()

        data class UpdateInputData(val inputData: AdHocActionInput) : Intent()
        object NoOp : Intent()

        // view intents
        object XClicked : Intent()
        object StartClicked : Intent()
        data class DateSet(val date: ZonedDateTime) : Intent()
        data class StartEnabled(val enabled: Boolean) : Intent()

        // ad hoc dispatch intents
        object DispatchTransferSelected : Intent()
        object DispatchReturnSelected : Intent()
        data class OnSelectOptionDropDown(val adHocDropDown: AdHocDropdownInputType) : Intent()
    }

    data class ViewState(
        val adHocAction: String,
        val projects: List<Project> = listOf(),
        val locations: List<RackLocation> = listOf(),
        val yards: List<Facility> = listOf(),
        val rigs: List<Facility> = listOf(),
        val wells: List<Facility> = listOf(),
        var selectedProject: Project? = null,
        val date: ZonedDateTime = ZonedDateTime.now(),
        val selectedYard: Facility? = null,
        val selectedLocation: RackLocation? = null,
        val selectedRig: Facility? = null,
        val selectedWell: Facility? = null,
        val startEnabled: Boolean = false,
        // ad hoc dispatch view state fields
        var selectedDispatchType: DispatchType = AWAITING_SELECTION,
        var selectedFromRig: Facility? = null,
        var selectedFromWell: Facility? = null,
        var selectedToRig: Facility? = null,
        var selectedToWell: Facility? = null,
        var selectedToYard: Facility? = null,
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState
}
