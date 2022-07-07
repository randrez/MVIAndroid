package com.scgts.sctrace.ad_hoc_action.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.scgts.sctrace.ad_hoc_action.R
import com.scgts.sctrace.base.model.Named
import com.scgts.sctrace.base.model.Project
import com.scgts.sctrace.ui.components.SCTraceDropdown

@Composable
fun DefaultAdHocAction(
    projects: List<Project>,
    selectedProject: Project?,
    onSelectDropDownProject:(Project) -> Unit
) {
    SCTraceDropdown(
        label = R.string.project,
        list = projects,
        placeholder = stringResource(id = R.string.project_hint),
        selectedItem = selectedProject,
        onItemSelected = onSelectDropDownProject as (Named) -> Unit,
    )
}