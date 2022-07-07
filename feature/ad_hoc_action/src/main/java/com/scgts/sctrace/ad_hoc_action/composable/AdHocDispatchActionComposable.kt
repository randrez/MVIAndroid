package com.scgts.sctrace.ad_hoc_action.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.ad_hoc_action.R
import com.scgts.sctrace.ad_hoc_action.composable.ad_hoc_action_section.AdHocDate
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.DispatchType.AWAITING_SELECTION
import com.scgts.sctrace.ui.components.SCTraceDropdown
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import theme.*

@Composable
fun AdHocDispatchAction(
    projects: List<Project>,
    rigs: List<Facility>,
    wells: List<Facility>,
    yards: List<Facility>,
    fromRigSelected: Facility?,
    fromWellSelected: Facility?,
    toRigSelected: Facility?,
    toWellSelected: Facility?,
    toYardSelected: Facility?,
    selectedProject: Project?,
    onSelectedProject: (Project) -> Unit,
    date: ZonedDateTime,
    onClickDatePicker: () -> Unit,
    dispatchTypeSelected: DispatchType,
    onSelectDispatchType: (DispatchType) -> Unit,
    onSelectDropDownFacility: (AdHocDropdownInputType) -> Unit
) {
    SCTraceDropdown(
        label = R.string.project,
        list = projects,
        placeholder = stringResource(id = R.string.project_hint),
        selectedItem = selectedProject,
        onItemSelected = onSelectedProject as (Named) -> Unit
    )
    AdHocDate(
        selectedDate = date.format(DateTimeFormatter.ofPattern("MMM dd, YYYY")),
        onClickDatePicker = onClickDatePicker,
        title = stringResource(id = R.string.dispatch_date)
    )
    DispatchType(
        onSelectDispatchType = onSelectDispatchType,
        dispatchTypeSelected = dispatchTypeSelected
    )
    if (dispatchTypeSelected != AWAITING_SELECTION) {
        DispatchFrom(
            selectedProject = selectedProject,
            fromRigs = rigs,
            onSelectDispatchFromRig = {
                onSelectDropDownFacility(
                    AdHocDropdownInputType.DispatchFromRig(it)
                )
            },
            fromRigSelected = fromRigSelected,
            fromWells = wells,
            onSelectDispatchFromWell = {
                onSelectDropDownFacility(
                    AdHocDropdownInputType.DispatchFromWell(it)
                )
            },
            fromWellSelected = fromWellSelected
        )
        DispatchTo(
            selectedProject = selectedProject,
            dispatchTypeSelected = dispatchTypeSelected,
            toRigs = rigs,
            toRigSelected = toRigSelected,
            onSelectDispatchToRig = {
                onSelectDropDownFacility(
                    AdHocDropdownInputType.DispatchToRig(it)
                )
            },
            toWells = wells,
            toWellSelected = toWellSelected,
            onSelectDispatchToWell = {
                onSelectDropDownFacility(
                    AdHocDropdownInputType.DispatchToWell(it)
                )
            },
            toYards = yards,
            toYardSelected = toYardSelected,
            onSelectDispatchToYard = {
                onSelectDropDownFacility(
                    AdHocDropdownInputType.DispatchToYard(it)
                )
            }
        )
    }
}

@Composable
fun DispatchType(onSelectDispatchType: (DispatchType) -> Unit, dispatchTypeSelected: DispatchType) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 19.dp)
    ) {
        val selectedDispatchYard = (DispatchType.DISPATCH_TO_YARD == dispatchTypeSelected)
        val selectedDispatchWell = (DispatchType.DISPATCH_TO_WELL == dispatchTypeSelected)
        Text(
            text = stringResource(id = R.string.type),
            style = MaterialTheme.typography.body1,
            color = N900.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold,
        )
        BoxDispatchType(
            dispatchType = DispatchType.DISPATCH_TO_YARD,
            onSelectDispatchType = onSelectDispatchType,
            borderColor = if (selectedDispatchYard) Blue500 else Cyan,
            backgroundColor = if (selectedDispatchYard) CyanBlue else Color.White,
            label = stringResource(id = R.string.dispatch_to_yard_button),
            width = if (selectedDispatchYard) 2.dp else 1.dp,
            drawable = if (selectedDispatchYard) R.drawable.ic_radio_button_checked else R.drawable.ic_radio_button_unchecked
        )
        BoxDispatchType(
            dispatchType = DispatchType.DISPATCH_TO_WELL,
            onSelectDispatchType = onSelectDispatchType,
            borderColor = if (selectedDispatchWell) Blue500 else Cyan,
            backgroundColor = if (selectedDispatchWell) CyanBlue else Color.White,
            label = stringResource(id = R.string.dispatch_to_well_button),
            width = if (selectedDispatchWell) 2.dp else 1.dp,
            drawable = if (selectedDispatchWell) R.drawable.ic_radio_button_checked else R.drawable.ic_radio_button_unchecked
        )
    }
}

@Composable
fun BoxDispatchType(
    dispatchType: DispatchType,
    onSelectDispatchType: (DispatchType) -> Unit,
    borderColor: Color,
    backgroundColor: Color,
    label: String,
    width: Dp,
    drawable: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 9.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(backgroundColor)
            .border(BorderStroke(width = width, borderColor))
            .clickable(onClick = { onSelectDispatchType(dispatchType) })
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 12.dp)
        ) {
            Image(
                painter = painterResource(id = drawable),
                contentDescription = "",
                modifier = Modifier.size(14.dp)
            )
            Text(text = label, modifier = Modifier.padding(start = 9.dp))
        }
    }
}

@Composable
fun DispatchFrom(
    selectedProject: Project?,
    fromRigs: List<Facility>,
    onSelectDispatchFromRig: (Facility) -> Unit,
    fromRigSelected: Facility?,
    fromWells: List<Facility>,
    onSelectDispatchFromWell: (Facility) -> Unit,
    fromWellSelected: Facility?
) {
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_pin),
            contentDescription = stringResource(id = R.string.ic_pin_description),
            Modifier.size(30.dp)
        )
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            SCTraceDropdown(
                label = R.string.rig,
                list = fromRigs,
                placeholder = stringResource(id = R.string.rig_hint),
                selectedItem = fromRigSelected,
                onItemSelected = onSelectDispatchFromRig as (Named) -> Unit,
                enabled = (selectedProject != null)
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            SCTraceDropdown(
                label = R.string.well_name,
                list = fromWells,
                placeholder = stringResource(id = R.string.well_hint),
                selectedItem = fromWellSelected,
                onItemSelected = onSelectDispatchFromWell as (Named) -> Unit,
                enabled = (selectedProject != null)
            )
        }
    }
}

@Composable
fun DispatchTo(
    selectedProject: Project?,
    dispatchTypeSelected: DispatchType,
    toRigs: List<Facility>,
    toRigSelected: Facility?,
    onSelectDispatchToRig: (Facility) -> Unit,
    toWells: List<Facility>,
    toWellSelected: Facility?,
    onSelectDispatchToWell: (Facility) -> Unit,
    toYards: List<Facility>,
    toYardSelected: Facility?,
    onSelectDispatchToYard: (Facility) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_location),
            contentDescription = stringResource(id = R.string.ic_location_description),
            Modifier.size(30.dp)
        )
        when (dispatchTypeSelected) {
            DispatchType.DISPATCH_TO_WELL -> {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    SCTraceDropdown(
                        label = R.string.rig,
                        list = toRigs,
                        placeholder = stringResource(id = R.string.rig_hint),
                        selectedItem = toRigSelected,
                        onItemSelected = onSelectDispatchToRig as (Named) -> Unit,
                        enabled = (selectedProject != null)
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                ) {
                    SCTraceDropdown(
                        label = R.string.well_name,
                        list = toWells,
                        placeholder = stringResource(id = R.string.well_hint),
                        selectedItem = toWellSelected,
                        onItemSelected = onSelectDispatchToWell as (Named) -> Unit,
                        enabled = (selectedProject != null)
                    )
                }
            }
            DispatchType.DISPATCH_TO_YARD -> {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    SCTraceDropdown(
                        label = R.string.yard_name,
                        list = toYards,
                        placeholder = stringResource(id = R.string.yard_hint),
                        selectedItem = toYardSelected,
                        onItemSelected = onSelectDispatchToYard as (Named) -> Unit,
                        enabled = (selectedProject != null)
                    )
                }
            }
        }
    }
}