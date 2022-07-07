package com.scgts.sctrace.ad_hoc_action.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.ad_hoc_action.R
import com.scgts.sctrace.base.model.Facility
import com.scgts.sctrace.base.model.Named
import com.scgts.sctrace.base.model.Project
import com.scgts.sctrace.ui.components.SCTraceDropdown

@Composable
fun AdHocRackTransferAction(
    projects: List<Project>,
    selectedProject: Project?,
    onSelectedProject: (Project) -> Unit,
    yards: List<Facility>,
    selectedYard: Facility?,
    onSelectedYard: (Facility) -> Unit
) {
    SCTraceDropdown(
        label = R.string.project,
        list = projects,
        placeholder = stringResource(id = R.string.project_hint),
        selectedItem = selectedProject,
        onItemSelected = onSelectedProject as (Named) -> Unit
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
}