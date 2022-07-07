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
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.ui.components.SCTraceDropdown
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun AdHocInboundFromMillAction(
    projects: List<Project>,
    yards: List<Facility>,
    locations: List<RackLocation>,
    selectedProject: Project?,
    selectedYard:Facility?,
    selectedLocation:RackLocation?,
    onSelectedProject: (Project) -> Unit,
    onSelectedYard:(Facility) -> Unit,
    onSelectedLocation:(RackLocation) -> Unit,
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
            label = R.string.yard_name,
            list = yards,
            placeholder = stringResource(id = R.string.yard_hint),
            selectedItem = selectedYard,
            onItemSelected = onSelectedYard as (Named) -> Unit,
            enabled = (selectedProject != null)
        )
    }
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        SCTraceDropdown(
            label = R.string.starting_location,
            list = locations,
            placeholder = stringResource(id = R.string.location_hint),
            selectedItem = selectedLocation,
            onItemSelected = onSelectedLocation as (Named) -> Unit,
            enabled = (selectedProject != null)
        )
    }
}