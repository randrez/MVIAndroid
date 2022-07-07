package com.scgts.sctrace.ad_hoc_action.ui

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionMvi.Intent
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionMvi.Intent.*
import com.scgts.sctrace.ad_hoc_action.ui.AdHocActionMvi.ViewState
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.AdHocAction
import com.scgts.sctrace.base.model.AdHocDropdownInputType
import com.scgts.sctrace.base.model.DispatchType
import com.scgts.sctrace.base.model.OrderType
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.*
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier

class AdHocActionViewModel(
    private val adHocAction: String,
    private val navigator: AppNavigator,
    private val tasksRepository: TasksRepository,
    private val adHocActionInput: AdHocActionInputCache,
    private val settingsManager: SettingsManager,
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val initialState = Supplier {
        ViewState(
            adHocAction = adHocAction
        )
    }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is ProjectsData -> prev.copy(projects = intent.projects)
            is LocationsData -> prev.copy(locations = intent.locations)
            is SetInputList -> prev.copy(
                yards = intent.yards,
                rigs = intent.rigs,
                wells = intent.wells
            )
            is UpdateInputData -> prev.copy(
                selectedProject = intent.inputData.project,
                date = intent.inputData.date,
                selectedYard = intent.inputData.yard,
                selectedLocation = intent.inputData.rack,
                selectedRig = intent.inputData.rig,
                selectedWell = intent.inputData.well,
                selectedDispatchType = intent.inputData.dispatchType,
                selectedFromRig = intent.inputData.fromRig,
                selectedFromWell = intent.inputData.fromWell,
                selectedToRig = intent.inputData.toRig,
                selectedToWell = intent.inputData.toWell,
                selectedToYard = intent.inputData.toYard
            )
            is StartEnabled -> prev.copy(startEnabled = intent.enabled)
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {
            dataIntent(tasksRepository.getProjects()) {
                it.map<Intent> { projects -> ProjectsData(projects) }.toObservable()
            }

            dataIntent(adHocActionInput.getObservable()) {
                it.map { input -> UpdateInputData(input) }
            }

            dataIntent(adHocActionInput.getObservable()) {
                it.map { input ->
                    when (adHocAction) {
                        AdHocAction.QuickScan.displayName, AdHocAction.RejectScan.displayName -> {
                            val enabled = input.project != null
                            StartEnabled(enabled)
                        }
                        AdHocAction.Dispatch.displayName -> {
                            /**
                             * For return dispatch the start button is active when the user has selected
                             * either a rig or well or both as the from location and a yard as the to location.
                             *
                             * For transfer dispatch the start button is active when the user has selected
                             * either a rig or well or both as the from location and either a rig or well
                             * or both as the to location.
                             *
                             * NOTE: If both a well and a rig are selected then the well will be used.
                             */
                            val fromLocationSelected =
                                input.fromRig != null || input.fromWell != null
                            val toLocationSelected = input.toRig != null || input.toWell != null
                            val yardSelected = input.toYard != null
                            val returnEnabled =
                                input.dispatchType == DispatchType.DISPATCH_TO_YARD && fromLocationSelected && yardSelected
                            val transferEnabled =
                                input.dispatchType == DispatchType.DISPATCH_TO_WELL && fromLocationSelected && toLocationSelected
                            val enabled =
                                input.project != null && (returnEnabled || transferEnabled)
                            StartEnabled(enabled)
                        }
                        AdHocAction.InboundFromMill.displayName, AdHocAction.InboundFromWellSite.displayName -> {
                            // the user must select a yard to start
                            val enabled = input.project != null && input.yard != null
                            StartEnabled(enabled)
                        }
                        AdHocAction.InboundToWell.displayName -> {
                            // the user must select either a rig or well to start
                            val enabled =
                                input.project != null && (input.rig != null || input.well != null)
                            StartEnabled(enabled)
                        }
                        AdHocAction.RackTransfer.displayName -> {
                            val enabled = input.project != null && input.yard != null
                            StartEnabled(enabled)
                        }
                        else -> StartEnabled(false)
                    }
                }
            }

            dataIntent(adHocActionInput.getObservable()) { it ->
                it.flatMapSingle { input ->
                    input.project?.let { project ->
                        Single.zip(
                            tasksRepository.getYardsForProject(project.id),
                            tasksRepository.getRigsForProject(project.id),
                            tasksRepository.getWellsForProject(project.id),
                            { yards, rigs, wells -> SetInputList(yards, rigs, wells) }
                        )
                    } ?: run {
                        Single.just(NoOp)
                    }
                }
            }

            dataIntent(adHocActionInput.getObservable()) {
                it.flatMapSingle { input ->
                    input.yard?.let { yard ->
                        tasksRepository.getRacksForYard(yard.id)
                            .map<Intent> { rackLocations -> LocationsData(rackLocations) }
                    } ?: run {
                        Single.just(NoOp)
                    }
                }
            }

            viewIntentCompletable<XClicked> {
                it.flatMapCompletable {
                    // we should clear out all of the inputs if the user leaves the screen
                    clearAdHocActionInput().andThen(navigator.popBackStack())
                }
            }

            viewIntentCompletable<StartClicked> {
                it.flatMapCompletable {
                    adHocActionInput.get().flatMapCompletable { input ->
                        settingsManager.setUnitType(input.project!!.unitOfMeasure).andThen(
                            when (adHocAction) {
                                AdHocAction.QuickScan.displayName -> {
                                    navigator.navigate(
                                        destination = Capture(
                                            projectId = input.project!!.id,
                                            taskId = null
                                        ),
                                        popUpTo = NavDestination.Tasks
                                    )
                                }
                                AdHocAction.RejectScan.displayName -> {
                                    navigator.navigate(
                                        destination = Capture(
                                            projectId = input.project!!.id,
                                            taskId = AD_HOC_REJECT_SCAN.id
                                        ),
                                        popUpTo = NavDestination.Tasks
                                    )
                                }
                                AdHocAction.Dispatch.displayName -> {
                                    when (input.dispatchType) {
                                        // we should be able to safely make these nullability assumptions based on the
                                        // input validation which dictates whether the start button is enabled. If these
                                        // bangs cause problems then the input validation has broken in some way.
                                        DispatchType.DISPATCH_TO_YARD -> {
                                            val fromLocation = (input.fromWell ?: input.fromRig)!!
                                            val toLocation = (input.toYard)!!
                                            tasksRepository.createAdHocTask(
                                                taskType = AD_HOC_DISPATCH_TO_YARD,
                                                project = input.project!!,
                                                description = "${fromLocation.name} to ${toLocation.name}",
                                                toLocationId = toLocation.id,
                                                toLocationName = toLocation.name,
                                                fromLocationName = fromLocation.name,
                                                fromLocationId = fromLocation.id,
                                                dispatchDate = input.date,
                                            ).andThen(
                                                navigator.navigate(
                                                    destination = TaskSummary(
                                                        taskId = AD_HOC_DISPATCH_TO_YARD.id,
                                                        orderId = ""
                                                    ),
                                                    popUpTo = NavDestination.Tasks
                                                )
                                            )
                                        }
                                        DispatchType.DISPATCH_TO_WELL -> {
                                            val fromLocation = (input.fromWell ?: input.fromRig)!!
                                            val toLocation = (input.toWell ?: input.toRig)!!
                                            tasksRepository.createAdHocTask(
                                                taskType = AD_HOC_DISPATCH_TO_WELL,
                                                project = input.project!!,
                                                description = "${fromLocation.name} to ${toLocation.name}",
                                                toLocationId = toLocation.id,
                                                toLocationName = toLocation.name,
                                                fromLocationId = fromLocation.id,
                                                fromLocationName = fromLocation.name,
                                                dispatchDate = input.date
                                            ).andThen(
                                                navigator.navigate(
                                                    destination = TaskSummary(
                                                        taskId = AD_HOC_DISPATCH_TO_WELL.id,
                                                        orderId = ""
                                                    ),
                                                    popUpTo = NavDestination.Tasks
                                                )
                                            )
                                        }
                                        else -> Completable.complete() // our input validation shouldn't let this happen
                                    }
                                }
                                AdHocAction.InboundFromMill.displayName -> {
                                    val yard = (input.yard)!!
                                    tasksRepository.createAdHocTask(
                                        taskType = AD_HOC_INBOUND_FROM_MILL,
                                        project = input.project!!,
                                        description = "Contract unidentified",
                                        toLocationId = yard.id,
                                        toLocationName = yard.name,
                                        orderType = OrderType.INBOUND,
                                        arrivalDate = input.date,
                                        defaultRackLocationId = input.rack?.id
                                    ).andThen(
                                        navigator.navigate(
                                            destination = TaskSummary(
                                                taskId = AD_HOC_INBOUND_FROM_MILL.id,
                                                orderId = ""
                                            ),
                                            popUpTo = NavDestination.Tasks
                                        )
                                    )
                                }
                                AdHocAction.InboundFromWellSite.displayName -> {
                                    val yard = (input.yard)!!
                                    tasksRepository.createAdHocTask(
                                        taskType = AD_HOC_INBOUND_FROM_WELL_SITE,
                                        project = input.project!!,
                                        description = yard.name,
                                        toLocationId = yard.id,
                                        toLocationName = yard.name,
                                        arrivalDate = input.date,
                                        defaultRackLocationId = input.rack?.id
                                    ).andThen(
                                        navigator.navigate(
                                            destination = TaskSummary(
                                                taskId = AD_HOC_INBOUND_FROM_WELL_SITE.id,
                                                orderId = ""
                                            ),
                                            popUpTo = NavDestination.Tasks
                                        )
                                    )
                                }
                                AdHocAction.InboundToWell.displayName -> {
                                    // If both a well and rig are selected we use the well
                                    val toLocation = (input.well ?: input.rig)!!
                                    tasksRepository.createAdHocTask(
                                        taskType = AD_HOC_INBOUND_TO_WELL,
                                        project = input.project!!,
                                        description = toLocation.name,
                                        toLocationId = toLocation.id,
                                        toLocationName = toLocation.name,
                                        arrivalDate = input.date,
                                    ).andThen(
                                        navigator.navigate(
                                            destination = TaskSummary(
                                                taskId = AD_HOC_INBOUND_TO_WELL.id,
                                                orderId = ""
                                            ),
                                            popUpTo = NavDestination.Tasks
                                        )
                                    )
                                }
                                AdHocAction.RackTransfer.displayName -> {
                                    val yard = (input.yard)!!
                                    tasksRepository.createAdHocTask(
                                        taskType = AD_HOC_RACK_TRANSFER,
                                        project = input.project!!,
                                        description = yard.name,
                                        toLocationId = yard.id,
                                        toLocationName = yard.name,
                                        arrivalDate = input.date,
                                        defaultRackLocationId = input.rack?.id
                                    ).andThen(
                                        navigator.navigate(
                                            destination = RackTransferTaskSummary(
                                                taskId = AD_HOC_RACK_TRANSFER.id,
                                                orderId = ""
                                            ),
                                            popUpTo = NavDestination.Tasks
                                        )
                                    )
                                }
                                else -> Completable.complete()
                            }
                        ).andThen(clearAdHocActionInput())
                    }
                }
            }

            viewIntentCompletable<DateSet> {
                it.flatMapCompletable {
                    adHocActionInput.edit {
                        date = it.date
                        this
                    }
                }
            }

            viewIntentCompletable<DispatchTransferSelected> {
                it.flatMapCompletable {
                    adHocActionInput.edit {
                        dispatchType = DispatchType.DISPATCH_TO_WELL
                        this
                    }
                }
            }

            viewIntentCompletable<DispatchReturnSelected> {
                it.flatMapCompletable {
                    adHocActionInput.edit {
                        dispatchType = DispatchType.DISPATCH_TO_YARD
                        this
                    }
                }
            }

            viewIntentCompletable<OnSelectOptionDropDown> {
                it.flatMapCompletable { intent ->
                    when (intent.adHocDropDown) {
                        is AdHocDropdownInputType.TypeProject -> {
                            val input = adHocActionInput.get().blockingGet()
                            if (intent.adHocDropDown.project != input.project) {
                                adHocActionInput.edit {
                                    project = intent.adHocDropDown.project
                                    yard = null
                                    rack = null
                                    rig = null
                                    well = null
                                    fromRig = null
                                    fromWell = null
                                    toRig = null
                                    toWell = null
                                    toYard = null
                                    this
                                }
                            } else Completable.complete()
                        }
                        is AdHocDropdownInputType.DispatchFromRig -> {
                            adHocActionInput.edit {
                                fromRig = intent.adHocDropDown.fromRig
                                this
                            }
                        }
                        is AdHocDropdownInputType.DispatchFromWell -> {
                            adHocActionInput.edit {
                                fromWell = intent.adHocDropDown.fromWell
                                this
                            }
                        }
                        is AdHocDropdownInputType.DispatchToRig -> {
                            adHocActionInput.edit {
                                toRig = intent.adHocDropDown.toRig
                                this
                            }
                        }
                        is AdHocDropdownInputType.DispatchToWell -> {
                            adHocActionInput.edit {
                                toRig = intent.adHocDropDown.toWell
                                this
                            }
                        }
                        is AdHocDropdownInputType.DispatchToYard -> {
                            adHocActionInput.edit {
                                toYard = intent.adHocDropDown.toYard
                                this
                            }
                        }
                        is AdHocDropdownInputType.Yard -> {
                            val input = adHocActionInput.get().blockingGet()
                            adHocActionInput.edit {
                                if (intent.adHocDropDown.yard != input.yard) {
                                    yard = intent.adHocDropDown.yard
                                    rack = null
                                }
                                this
                            }
                        }
                        is AdHocDropdownInputType.Location -> {
                            adHocActionInput.edit {
                                rack = intent.adHocDropDown.location
                                this
                            }
                        }
                        is AdHocDropdownInputType.Rig -> {
                            adHocActionInput.edit {
                                rig = intent.adHocDropDown.rig
                                this
                            }
                        }
                        is AdHocDropdownInputType.Well -> {
                            adHocActionInput.edit {
                                well = intent.adHocDropDown.well
                                this
                            }
                        }
                    }
                }
            }

            viewIntentPassThroughs(
                DispatchTransferSelected::class,
                DispatchReturnSelected::class,
                DateSet::class
            )
        }

    private fun clearAdHocActionInput(): Completable {
        return adHocActionInput.edit {
            project = null
            yard = null
            rack = null
            rig = null
            well = null
            dispatchType = DispatchType.AWAITING_SELECTION
            fromRig = null
            fromWell = null
            toRig = null
            toWell = null
            toYard = null
            this
        }
    }
}
