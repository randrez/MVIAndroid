package com.scgts.sctrace.ad_hoc_action.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.scgts.sctrace.ad_hoc_action.R
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionMvi.*
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.AdHocAction.*
import com.scgts.sctrace.ui.components.SCTraceButton

@Composable
fun AdHocActionScreen(
    viewState: LiveData<ViewState>,
    onCloseClick: () -> Unit,
    onStartClick: () -> Unit,
    onClickDatePicker: () -> Unit,
    onSelectDispatchType: (DispatchType) -> Unit,
    onSelectDropDownProject: (AdHocDropdownInputType) -> Unit,
    onSelectDropDownFacility: (AdHocDropdownInputType) -> Unit,
    onSelectDropDownLocation: (AdHocDropdownInputType) -> Unit
) {
    viewState.observeAsState().value?.let { state ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 42.dp, start = 16.dp, end = 16.dp)
        ) {
            HeaderAdHocAction(headerTitle = state.adHocAction, onCloseClick = onCloseClick)
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(top = 38.dp)
                    .weight(7f)
            ) {
                when (state.adHocAction) {
                    QuickScan.displayName, RejectScan.displayName -> {
                        DefaultAdHocAction(
                            projects = state.projects,
                            selectedProject = state.selectedProject,
                            onSelectDropDownProject = { project ->
                                onSelectDropDownProject(
                                    AdHocDropdownInputType.TypeProject(project)
                                )
                            }
                        )
                    }
                    Dispatch.displayName -> {
                        AdHocDispatchAction(
                            projects = state.projects,
                            rigs = state.rigs,
                            wells = state.wells,
                            yards = state.yards,
                            fromRigSelected = state.selectedFromRig,
                            fromWellSelected = state.selectedFromWell,
                            toRigSelected = state.selectedToRig,
                            toWellSelected = state.selectedToWell,
                            toYardSelected = state.selectedToYard,
                            selectedProject = state.selectedProject,
                            onSelectedProject = { project ->
                                onSelectDropDownProject(
                                    AdHocDropdownInputType.TypeProject(project)
                                )
                            },
                            date = state.date,
                            onClickDatePicker = onClickDatePicker,
                            dispatchTypeSelected = state.selectedDispatchType,
                            onSelectDispatchType = onSelectDispatchType,
                            onSelectDropDownFacility = onSelectDropDownFacility
                        )
                    }
                    InboundFromMill.displayName, InboundFromWellSite.displayName -> {
                        AdHocInboundFromMillAction(
                            projects = state.projects,
                            yards = state.yards,
                            locations = state.locations,
                            selectedProject = state.selectedProject,
                            selectedYard = state.selectedYard,
                            selectedLocation = state.selectedLocation,
                            onSelectedProject = { project ->
                                onSelectDropDownProject(
                                    AdHocDropdownInputType.TypeProject(project)
                                )
                            },
                            onSelectedYard = {
                                onSelectDropDownFacility(
                                    AdHocDropdownInputType.Yard(it)
                                )
                            },
                            onSelectedLocation = {
                                onSelectDropDownLocation(
                                    AdHocDropdownInputType.Location(it)
                                )
                            },
                            date = state.date,
                            onClickDatePicker = onClickDatePicker
                        )
                    }
                    InboundToWell.displayName -> {
                        AdHocInboundToWellAction(
                            projects = state.projects,
                            rigs = state.rigs,
                            wells = state.wells,
                            selectedProject = state.selectedProject,
                            selectedRig = state.selectedRig,
                            selectedWell = state.selectedWell,
                            onSelectedProject = { project ->
                                onSelectDropDownProject(
                                    AdHocDropdownInputType.TypeProject(project)
                                )
                            },
                            onSelectedRig = {
                                onSelectDropDownFacility(
                                    AdHocDropdownInputType.Rig(it)
                                )
                            },
                            onSelectedWell = {
                                onSelectDropDownFacility(
                                    AdHocDropdownInputType.Well(it)
                                )
                            },
                            date = state.date,
                            onClickDatePicker = onClickDatePicker
                        )
                    }
                    RackTransfer.displayName -> {
                        AdHocRackTransferAction(
                            projects = state.projects,
                            selectedProject = state.selectedProject,
                            onSelectedProject = { project ->
                                onSelectDropDownProject(
                                    AdHocDropdownInputType.TypeProject(project)
                                )
                            },
                            yards = state.yards,
                            selectedYard = state.selectedYard,
                            onSelectedYard = {
                                onSelectDropDownFacility(
                                    AdHocDropdownInputType.Yard(it)
                                )
                            }
                        )
                    }
                }
                SCTraceButton(
                    onClick = onStartClick,
                    enabled = state.startEnabled,
                    text = stringResource(id = R.string.start),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            }
        }
    }
}

@Composable
fun HeaderAdHocAction(headerTitle: String, onCloseClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.then(Modifier.size(18.dp))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.close_icon_description),
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = headerTitle,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}