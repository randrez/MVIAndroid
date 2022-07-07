package com.scgts.sctrace.ad_hoc_action.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.ad_hoc_action.R
import com.scgts.sctrace.ad_hoc_action.composable.ad_hoc_action_section.AdHocDate
import com.scgts.sctrace.base.model.Facility
import com.scgts.sctrace.base.model.Named
import com.scgts.sctrace.base.model.Project
import com.scgts.sctrace.ui.components.SCTraceDropdown
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun AdHocInboundToWellAction(
    projects: List<Project>,
    rigs: List<Facility>,
    wells: List<Facility>,
    selectedProject: Project?,
    selectedRig: Facility?,
    selectedWell: Facility?,
    onSelectedProject: (Project) -> Unit,
    onSelectedRig: (Facility) -> Unit,
    onSelectedWell: (Facility) -> Unit,
    date: ZonedDateTime,
    onClickDatePicker: () -> Unit
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
        title = stringResource(id = R.string.arrival_date)
    )
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
    ) {
        SCTraceDropdown(
            label = R.string.rig,
            list = rigs,
            placeholder = stringResource(id = R.string.rig_hint),
            selectedItem = selectedRig,
            onItemSelected = onSelectedRig as (Named) -> Unit,
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
            list = wells,
            placeholder = stringResource(id = R.string.well_hint),
            selectedItem = selectedWell,
            onItemSelected = onSelectedWell as (Named) -> Unit,
            enabled = (selectedProject != null)
        )
    }
}