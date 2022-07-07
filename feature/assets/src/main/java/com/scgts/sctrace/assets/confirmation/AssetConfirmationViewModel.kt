package com.scgts.sctrace.assets.confirmation

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.assets.confirmation.AssetConfirmationMvi.Intent
import com.scgts.sctrace.assets.confirmation.AssetConfirmationMvi.Intent.*
import com.scgts.sctrace.assets.confirmation.AssetConfirmationMvi.ViewState
import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.auth.UserPreferences
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination.EditAssetLength
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.tasks.TasksRepository
import com.scgts.sctrace.user.UserRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.*
import java.util.concurrent.TimeUnit

class AssetConfirmationViewModel(
    private val data: NavDestinationArgs.AssetDataForNavigation,
    private val scanStateManager: ScanStateManager,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
    private val userPreferences: UserPreferences,
    private val networkChangeListener: NetworkChangeListener,
    private val assetInputCache: AssetInputCache,
    private val settingsManager: SettingsManager,
    private val userRepository: UserRepository,
    private val captureModeManager: CaptureModeManager,
    private val topToastManager: TopToastManager,
    private val assetDataCache: AssetDataCache,
) : ViewModel(), MviViewModel<Intent, ViewState> {
    private val initialState = Supplier {
        ViewState(
            newAsset = data.newAsset,
            isAdHoc = data.taskId == null,
            scannedTag = data.scannedTag,
            typeOrderWarning = data.unexpectedWarning,
            unitType = settingsManager.unitType().blockingFirst(),
        )
    }

    private val assetId = data.assetId
    private val taskId = data.taskId
    private val originPage = data.originPage

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is Dismiss -> prev.copy(dismiss = true, isOfflineSubmitted = false)
            is ToggleClick -> prev.copy(isExpanded = !prev.isExpanded)
            is TaskTypeData -> prev.copy(taskType = intent.type)
            is AssetData -> prev.copy(
                pipeNumber = intent.asset.pipeNumber,
                heatNumber = intent.asset.heatNumber,
                name = intent.asset.productDescription(),
                millWorkNo = intent.asset.millWorkNumber,
                numTags = intent.asset.tags.size,
                assetDetailList = intent.asset.toAssetDetailList(
                    unitType = prev.unitType,
                    showShipmentContract = intent.show
                ),
                loading = false
            )
            is OfflineSubmission -> prev.copy(isOfflineSubmitted = true)
            is Conditions -> prev.copy(conditions = intent.conditions)
            is RackLocationData -> prev.copy(rackLocations = intent.rackLocations)
            is SetInputData -> {
                val detailList = prev.assetDetailList.toMutableList()
                if (intent.condition != null) detailList.find { it.label == R.string.condition }
                    ?.let {
                        val conditionData = it.copy(value = intent.condition.name)
                        detailList[detailList.indexOf(it)] = conditionData
                    }
                if (intent.rackLocation != null) detailList.find { it.label == R.string.rack_location }
                    ?.let { item ->
                        val locationData = item.copy(value = intent.rackLocation.name)
                        detailList[detailList.indexOf(item)] = locationData
                    }
                if (intent.length != null) detailList.find { it.label == R.string.length }
                    ?.let { item ->
                        val lengthData = item.copy(value = intent.length.getFormattedLengthString())
                        detailList[detailList.indexOf(item)] = lengthData
                    }
                prev.copy(
                    selectedCondition = intent.condition,
                    selectedLocation = intent.rackLocation,
                    laserLength = intent.length,
                    assetDetailList = detailList
                )
            }
            is SetRole -> prev.copy(userRole = intent.userRole)
            is UnitTypeData -> prev.copy(unitType = intent.unitType)
            is Submitted -> prev.copy(hasSubmitted = true)
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {
        dataIntent(scanStateManager.setPause())

        val assetAndTaskValidation = Single.zip(
            tasksRepository.getAsset(assetId),
            tasksRepository.validateTaskIsOutboundDispatchOrBuildOrder(taskId),
            { asset, show -> Pair(asset, show) }
        )
        dataIntent(assetAndTaskValidation) {
            it.map<Intent> { (asset, show) -> AssetData(asset, show) }.toObservable()
        }

        dataIntent(assetInputCache.getObservable()) {
            it.map { SetInputData(it.condition, it.rackLocation, it.length) }
        }

        dataIntent(tasksRepository.getAsset(assetId)) {
            it.flatMapObservable { asset ->
                tasksRepository.getProjectConditions(asset.projectId).map { conditions ->
                    Conditions(conditions)
                }.toObservable()
            }
        }

        dataIntent(tasksRepository.getAssetWithTraceEventData(assetId, taskId)) {
            it.flatMapCompletable { asset ->
                val selectedCondition = if (asset.conditionId != null) {
                    asset.conditionId?.let { conditionId ->
                        tasksRepository.getConditionByIdAndProjectId(conditionId, asset.projectId)
                            .blockingGet()
                    }
                } else tasksRepository.getDefaultCondition(asset.projectId).blockingGet()
                assetInputCache.edit {
                    if (condition == null) condition = selectedCondition
                    this
                }
            }.toObservable()
        }

        dataIntent(tasksRepository.getAsset(assetId)) {
            it.flatMapObservable { asset ->
                userRepository.getUserRolesByProject(asset.projectId).map { userRole ->
                    SetRole(userRole)
                }.toObservable()
            }
        }

        if (taskId != null) {
            dataIntent(tasksRepository.getTask(taskId)) {
                it.map<Intent> { task -> TaskTypeData(task.type) }.toObservable()
            }

            dataIntent(tasksRepository.getTask(taskId)) {
                it.flatMapObservable { task ->
                    tasksRepository.getRacksForYard(task.toLocationId!!)
                        .map<Intent> { RackLocationData(it) }.toObservable()
                }
            }
        } else {
            // fetch all available facility locations for ad hoc
            dataIntent(tasksRepository.getAsset(assetId)) {
                it.flatMapObservable { asset ->
                    tasksRepository.getProjectRackLocations(asset.projectId)
                        .map<Intent> { RackLocationData(it) }.toObservable()
                }
            }
        }

        dataIntent(tasksRepository.getAssetWithTraceEventData(assetId, taskId)) {
            it.flatMapObservable { asset ->
                var selectedRackLocation: RackLocation? = null
                if (!asset.rackLocationId.isNullOrEmpty() && tasksRepository.hasRackLocation(asset.rackLocationId!!)
                        .blockingGet()
                ) {
                    selectedRackLocation =
                        tasksRepository.getRackLocationById(asset.rackLocationId!!).blockingGet()
                } else if (taskId != null) {
                    val task = tasksRepository.getTask(taskId).blockingGet()
                    if (!task.defaultRackLocationId.isNullOrEmpty() && tasksRepository.hasRackLocation(
                            task.defaultRackLocationId!!
                        ).blockingGet()
                    ) {
                        selectedRackLocation =
                            tasksRepository.getRackLocationById(task.defaultRackLocationId!!)
                                .blockingGet()
                    }
                }
                assetInputCache.edit {
                    if (rackLocation == null) rackLocation = selectedRackLocation
                    this
                }.toObservable()
            }
        }

        dataIntent(tasksRepository.getAssetLength(assetId, taskId)) {
            it.flatMapObservable { assetLength ->
                settingsManager.unitType().flatMap { unitType ->
                    assetInputCache.edit {
                        if (length == null) length = Length(assetLength, unitType)
                        this
                    }.toObservable()
                }
            }
        }

        dataIntent(settingsManager.unitType()) {
            it.map { unitType -> UnitTypeData(unitType) }
        }

        viewIntentObservable<Save> {
            it.flatMap {
                assetInputCache.get().flatMapObservable { assetInput ->
                    val networkObs = if (networkChangeListener.isConnected() || taskId != null) {
                        scanStateManager.setScanning()
                            .andThen(topToastManager.showAssetAddedToast())
                            .andThen(
                                navigator.popBackStackDestination(
                                    originPage,
                                    inclusive = false
                                )
                            )
                            .andThen(
                                Single.just<Intent>(Dismiss)
                                    .delay(250, TimeUnit.MILLISECONDS)
                                    .toObservable()
                                    .startWith(Observable.just(Submitted))
                            )
                    } else {
                        navigator.popBackStackDestination(originPage, inclusive = false)
                            .andThen(
                                Single.just<Intent>(OfflineSubmission)
                                    .delay(250, TimeUnit.MILLISECONDS)
                                    .toObservable()
                                    .startWith(Observable.just(Submitted))
                            )
                    }
                    if (taskId != null) {
                        tasksRepository.getTask(taskId).flatMapCompletable { task ->
                            tasksRepository.addTraceEvent(
                                taskId = taskId,
                                assetId = assetId,
                                facilityId = task.toLocationId!!,
                                toLocationId = task.toLocationId,
                                rackLocationId = assetInput.rackLocation?.id,
                                conditionId = assetInput.condition?.id,
                                laserLength = assetInput.length?.value,
                                adHocActionTaskType = if (task.isAdHocAction()) task.type.serverName else null,
                            )
                        }
                    } else {
                        tasksRepository.addTraceEvent(
                            taskId = TaskType.AD_HOC_QUICK_SCAN.id,
                            assetId = assetId,
                            facilityId = "", // ad hoc should be the only time we do not have a facility id
                            rackLocationId = assetInput.rackLocation?.id,
                            conditionId = assetInput.condition?.id,
                            laserLength = assetInput.length?.value,
                            adHocActionTaskType = TaskType.AD_HOC_QUICK_SCAN.serverName,
                        )
                    }
                        .andThen(
                            updateDefaultRackLocationForTask(
                                defaultRackLocationId = assetInput.rackLocation?.id,
                                taskId = taskId,
                                tasksRepository = tasksRepository
                            )
                        )
                        .andThen(clearAssetInputCache())
                        .andThen(networkObs)
                }
            }
        }

        viewIntentObservable<OfflineAcknowledged> {
            it.flatMap {
                scanStateManager.setScanning().andThen(Observable.just(Dismiss))
            }
        }

        viewIntentObservable<DiscardClick> {
            it.flatMap {
                scanStateManager.setScanning()
                    .andThen(clearAssetInputCache())
                    .andThen(topToastManager.setShowAssetAddedToastFlag(false))
                    .andThen(navigator.popBackStackDestination(originPage, inclusive = false))
                    .andThen(Observable.just(Dismiss))
            }
        }

        viewIntentObservable<LengthClick> { intent ->
            intent.flatMap {
                navigator.navigate(EditAssetLength).toObservable()
            }
        }

        viewIntentCompletable<TagClick> { intent ->
            intent.flatMapCompletable {
                assetDataCache.put(data)
                    .andThen(navigator.popBackStack())
                    .andThen(captureModeManager.setTag(assetId))
            }
        }

        viewIntentCompletable<ConditionSelected> { obs ->
            obs.flatMapCompletable {
                assetInputCache.edit {
                    condition = it.condition
                    this
                }
            }
        }

        viewIntentCompletable<LocationSelected> { obs ->
            obs.flatMapCompletable {
                assetInputCache.edit {
                    rackLocation = it.rackLocation
                    this
                }
            }
        }

        viewIntentPassThroughs(ToggleClick::class)
    }

    fun ignoreTagWarnings() = userPreferences.putString(UserKey.IgnoreTagWarnings, "true")

    fun shouldIgnoreTagWarnings() = userPreferences.getString(UserKey.IgnoreTagWarnings) == "true"


    /**
     * Default facility location (rack) rules:
     *
     * - If this is the first asset scanned then it should be the task's default location (for
     *   tasks from the server this will be null and that is fine, for quick actions the user has
     *   the option to specify one).
     *
     * - If this is not the first asset scanned then it should be whatever the facility location
     *   specified in the last confirmed asset was.
     */
    private fun updateDefaultRackLocationForTask(
        defaultRackLocationId: String?,
        taskId: String?,
        tasksRepository: TasksRepository,
    ): Completable {
        return if (taskId != null && defaultRackLocationId != null) {
            tasksRepository.updateDefaultRackLocationForTask(
                taskId,
                defaultRackLocationId
            )
        } else Completable.complete()
    }

    private fun clearAssetInputCache(): Completable {
        return assetInputCache.edit {
            condition = null
            rackLocation = null
            length = null
            this
        }
    }
}
