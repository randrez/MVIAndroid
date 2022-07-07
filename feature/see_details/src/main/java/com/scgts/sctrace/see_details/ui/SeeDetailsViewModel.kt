package com.scgts.sctrace.see_details.ui

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.OrderType
import com.scgts.sctrace.base.model.TaskType
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.see_details.ui.SeeDetailsMvi.Intent
import com.scgts.sctrace.see_details.ui.SeeDetailsMvi.Intent.*
import com.scgts.sctrace.see_details.ui.SeeDetailsMvi.ViewState
import com.scgts.sctrace.tasks.TasksRepository
import com.scgts.sctrace.ui.components.ExpandableRow
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier

class SeeDetailsViewModel(
    private val taskId: String,
    private val orderId: String,
    private val navigator: AppNavigator,
    private val tasksRepository: TasksRepository,
    private val settingsManager: SettingsManager,
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val initialState = Supplier { ViewState() }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is ToggleRowExpanded -> {
                when (intent.row) {
                    ExpandableRow.INSTRUCTIONS -> prev.copy(instructionsExpanded = !prev.instructionsExpanded)
                    ExpandableRow.ASSETS -> prev.copy(assetsExpanded = !prev.assetsExpanded)
                }
            }
            is UnitTypeUpdate -> prev.copy(unitType = intent.unitType)
            is TaskData -> prev.copy(task = intent.task)
            is AssetProductData -> prev.copy(assetProductDescription = intent.info)
            is HideInstructions -> prev.copy(hideInstructions = intent.hideInstructions)
            is TaskTypeValidation -> prev.copy(isDispatchOrBuildOrder = intent.isDispatchOrBuildOrder)
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {
            dataIntent(settingsManager.unitType()) {
                it.map { UnitTypeUpdate(it) }
            }

            dataIntent(settingsManager.captureMethod()) {
                it.map { CaptureMethodUpdate(it) }
            }

            viewIntentCompletable<XClicked> {
                it.flatMapCompletable { navigator.popBackStack() }
            }

            dataIntent(tasksRepository.getTask(taskId)) {
                it.map<Intent> { task -> TaskData(task) }.toObservable()
            }

            dataIntent(tasksRepository.getTask(taskId)) {
                it.map<Intent> { task ->
                    if (task.orderType == OrderType.RETURN_TRANSFER && task.type == TaskType.INBOUND_FROM_WELL_SITE) {
                        HideInstructions(false)
                    } else {
                        HideInstructions(true)
                    }
                }.toObservable()
            }

            dataIntent(tasksRepository.getAssetProductInformations(orderId, taskId)) {
                it.map { assetProductInformations -> AssetProductData(assetProductInformations) }
            }

            dataIntent(tasksRepository.validateTaskIsOutboundDispatchOrBuildOrder(taskId)) {
                it.map<Intent> { show -> TaskTypeValidation(show) }.toObservable()
            }

            viewIntentPassThroughs(ToggleRowExpanded::class)
        }
}
