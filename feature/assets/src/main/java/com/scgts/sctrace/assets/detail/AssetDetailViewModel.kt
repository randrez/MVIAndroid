package com.scgts.sctrace.assets.detail

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.assets.detail.AssetDetailMvi.Intent
import com.scgts.sctrace.assets.detail.AssetDetailMvi.Intent.AssetData
import com.scgts.sctrace.assets.detail.AssetDetailMvi.Intent.BackClick
import com.scgts.sctrace.assets.detail.AssetDetailMvi.ViewState
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.toAssetDetailList

class AssetDetailViewModel(
    private val assetId: String,
    private val taskId: String?,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
    private val settingsManager: SettingsManager,
) : ViewModel(),
    MviViewModel<Intent, ViewState> {

    private val initialState = Supplier { ViewState() }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
    }

    private val reducer =
        BiFunction<ViewState, Intent, ViewState> { prev, intent ->
            when (intent) {
                is AssetData -> prev.copy(
                    assetDetailList = intent.assetDetailList,
                    assetDescription = intent.assetDescription
                )
                else -> prev
            }
        }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {
        val assetDetailData = Single.zip(
            tasksRepository.getAssetWithTraceEventData(assetId, taskId),
            settingsManager.unitType().firstOrError(),
            tasksRepository.validateTaskIsOutboundDispatchOrBuildOrder(taskId),
            { asset, unitType, show ->
                val condition = asset.conditionId?.let { conditionId ->
                    tasksRepository.getConditionByIdAndProjectId(conditionId, asset.projectId)
                        .blockingGet()
                }
                val rackLocation = asset.rackLocationId?.let { rackLocationId ->
                    tasksRepository.getRackLocationById(rackLocationId).blockingGet()
                }
                val assetDetailList = asset.toAssetDetailList(
                    condition = condition?.name,
                    rackLocation = rackLocation?.name,
                    unitType = unitType,
                    showShipmentContract = show
                )
                Pair(assetDetailList, asset.productDescription())
            }
        )

        dataIntent(assetDetailData) {
            it.map<Intent> { (assetDetailList, assetDescription) ->
                AssetData(assetDetailList = assetDetailList, assetDescription = assetDescription)
            }.toObservable()
        }

        viewIntentCompletable<BackClick> {
            it.flatMapCompletable { navigator.popBackStack() }
        }
    }
}