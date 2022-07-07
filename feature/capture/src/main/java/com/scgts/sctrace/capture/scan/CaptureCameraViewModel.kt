package com.scgts.sctrace.capture.scan

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.assets.confirmation.AssetDataCache
import com.scgts.sctrace.assets.tags.AssetTagCache
import com.scgts.sctrace.base.*
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.CaptureMethod.Camera
import com.scgts.sctrace.base.model.DialogType.*
import com.scgts.sctrace.base.model.OrderType.*
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.base.util.formatTally
import com.scgts.sctrace.base.util.toUiModels
import com.scgts.sctrace.capture.CaptureUseCase
import com.scgts.sctrace.capture.CaptureUseCase.AssetResult.*
import com.scgts.sctrace.capture.CaptureUseCase.EndResult
import com.scgts.sctrace.capture.R
import com.scgts.sctrace.capture.scan.CaptureCameraMvi.Intent
import com.scgts.sctrace.capture.scan.CaptureCameraMvi.Intent.*
import com.scgts.sctrace.capture.scan.CaptureCameraMvi.ViewState
import com.scgts.sctrace.capture.tag_conflict.ConflictIdsCache
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination
import com.scgts.sctrace.framework.navigation.NavDestination.ConflictHandler
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.*
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import util.*
import util.CaptureMode.Consumption.Consume
import util.CaptureMode.Consumption.Unknown
import java.util.concurrent.TimeUnit

class CaptureCameraViewModel(
    private val currentTask: CurrentTask,
    private val navigator: AppNavigator,
    private val scanStateManager: ScanStateManager,
    private val tasksRepository: TasksRepository,
    private val captureModeManager: CaptureModeManager,
    private val conflictIdsCache: ConflictIdsCache,
    private val captureUseCase: CaptureUseCase,
    private val assetTagCache: AssetTagCache,
    private val captureMethodManager: CaptureMethodManager,
    private val topToastManager: TopToastManager,
    private val assetDataCache: AssetDataCache,
    private val rackTransferCache: RackTransferCache,
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val taskId: String? = currentTask.id

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents).scanWith(
            { ViewState() }, { prev, intent ->
                when (intent) {
                    is CapturedAssets -> prev.copy(assets = intent.assets)
                    is SetDialog -> prev.copy(dialogType = intent.dialogType)
                    is SetCaptureMode -> prev.copy(captureMode = intent.captureMode)
                    is SwipeToEditEnabled -> prev.copy(swipeToEditEnabled = intent.enabled)
                    is SetShowAssetSummary -> prev.copy(showAssetSummary = intent.show)
                    is UpdateAssetAddedToast -> prev.copy(showAssetAddedToast = intent.show)
                    is UpdateTagList -> prev.copy(tags = intent.tags)
                    is ScannedSummaryData -> prev.copy(summaryList = intent.summaryList)
                    is SetAutoScan -> prev.copy(
                        autoScanEnabled = intent.autoScanIsEnabled,
                        autoScanNotificationMessage = if (intent.autoScanIsEnabled) R.string.auto_scan_on else R.string.auto_scan_off,
                    )
                    is SetAutoScanNotificationMessage -> prev.copy(autoScanNotificationMessage = intent.message)
                    is SetScanState -> prev.copy(scanState = intent.scanState)
                    else -> prev.copy()
                }
            }
        )
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> =
        intentsBuild(intents) {

            // null when in Ad Hoc capture mode
            if (taskId != null && !currentTask.quickReject) {
                dataIntent(tasksRepository.getTask(taskId)) {
                    it.flatMapObservable { task ->
                        if (task.type == RACK_TRANSFER || task.type == AD_HOC_RACK_TRANSFER) {
                            rackTransferCache.getObservable().flatMap { cache ->
                                tasksRepository.getAssets(cache.assetIds.toList()).toObservable()
                            }
                        } else {
                            tasksRepository.getUnsubmittedAssets(taskId)
                        }.flatMap { assets ->
                            Observable.just(CapturedAssets(assets.toUiModels()))
                        }
                    }
                }

                dataIntent(tasksRepository.getTask(taskId)) {
                    it.flatMapObservable { task ->
                        val decimalPlaces = 2
                        when (task.type) {
                            CONSUME -> tasksRepository.getTalliesForTask(task.id, true)
                            RACK_TRANSFER -> rackTransferCache.getObservable().flatMap { cache ->
                                tasksRepository.getTalliesForAssets(cache.assetIds.toList())
                            }
                            else -> tasksRepository.getTalliesForTask(task.id)
                        }.flatMap { tallies ->
                            Observable.just(
                                ScannedSummaryData(
                                    if (task.type == CONSUME) {
                                        listOf(
                                            TextEntry(
                                                label = R.string.session_consumed,
                                                body = formatTally(
                                                    expectedLength = tallies.totalConsumed,
                                                    numJoints = tallies.consumedJoints,
                                                    unitType = task.unitOfMeasure,
                                                    decimalPlaces = decimalPlaces
                                                ),
                                            ),
                                            TextEntry(
                                                label = R.string.session_running_length,
                                                body = Length(
                                                    value = tallies.totalConsumedRunningLength,
                                                    unitType = task.unitOfMeasure
                                                ).getFormattedLengthString(),
                                            ),
                                            TextEntry(
                                                label = R.string.session_rejected,
                                                body = formatTally(
                                                    expectedLength = tallies.totalRejected,
                                                    numJoints = tallies.rejectedJoints,
                                                    unitType = task.unitOfMeasure,
                                                    decimalPlaces = decimalPlaces
                                                ),
                                            )
                                        )
                                    } else {
                                        listOf(
                                            TextEntry(
                                                label = R.string.total_tally,
                                                body = formatTally(
                                                    expectedLength = tallies.total,
                                                    numJoints = tallies.totalJoints,
                                                    unitType = task.unitOfMeasure,
                                                    decimalPlaces = decimalPlaces
                                                ),
                                            )
                                        )
                                    }
                                )
                            )
                        }
                    }
                }

                // Swipe to edit should be disabled for all outbound and consumption tasks as well as
                // return & transfer dispatch and inbound to well tasks
                dataIntent(tasksRepository.getTask(taskId)) {
                    it.map<Intent> { task ->
                        SwipeToEditEnabled(task.swipeToEditEnabled())
                    }.toObservable()
                }
            }

            dataIntent(captureModeManager.stateObs()) {
                it.map { captureMode ->
                    if ((taskId == null && captureMode !is CaptureMode.Tags) || currentTask.quickReject) {
                        SetShowAssetSummary(false)
                    } else SetShowAssetSummary(true)
                }
            }

            dataIntent(captureModeManager.stateObs()) {
                it.map { captureMode -> SetCaptureMode(captureMode) }
            }

            dataIntent(captureModeManager.stateObs()) {
                it.flatMapCompletable { captureMode ->
                    if (captureMode is CaptureMode.Tags) tasksRepository.getAssetTags(captureMode.assetId)
                        .flatMapCompletable { tags ->
                            assetTagCache.put(tags.reversed().toMutableSet())
                        }
                        .andThen(scanStateManager.setScanning())
                    else Completable.complete()
                }.toObservable()
            }

            dataIntent(assetTagCache.getObservable()) {
                it.map { tags -> UpdateTagList(tags.reversed()) }
            }

            dataIntent(scanStateManager.scanStateObs()) {
                it.map { scanState -> SetScanState(scanState) }
            }

            dataIntent(scanStateManager.autoScanStateObs()) {
                it.map { autoScanState -> SetAutoScan(autoScanState) }
            }

            dataIntent(topToastManager.getShowAssetAddedToastObs().distinctUntilChanged()) {
                it.flatMap { show ->
                    Completable.timer(2, TimeUnit.SECONDS)
                        .andThen(topToastManager.hideAssetAddedToast())
                        .startWith(Observable.just(UpdateAssetAddedToast(show)))
                }
            }

            dataIntent(captureModeManager.showConsumptionDuplicateObs()) {
                it.flatMap { consumptionType ->
                    captureMethodManager.captureMethod().flatMapObservable { captureMethod ->
                        val consumed = consumptionType is Consume
                        if (captureMethod == Camera && consumptionType != Unknown) {
                            showConsumptionDuplicate(consumed)
                        } else Observable.just(NoOp)
                    }
                }
            }

            viewIntentObservable<BarCodeScanned> {
                it.flatMap { barcode ->
                    scanStateManager.setPause().andThen(
                        if (captureModeManager.state().blockingGet() is CaptureMode.Tags) {
                            assetTagCache.edit {
                                add(barcode.tag)
                                this
                            }.andThen(continueScanning()).toObservable()
                        } else captureUseCase.getAssetsByTag(barcode.tag)
                            .flatMapObservable GetAsset@{ result ->
                                handleFoundAssets(result, barcode.tag)
                            }
                    )
                }
            }

            viewIntentObservable<DismissDialog> {
                it.flatMap {
                    scanStateManager.setScanning()
                        .andThen(Observable.just(SetDialog(null)))
                }
            }

            viewIntentCompletable<OnCameraPermissionGranted> {
                it.flatMapCompletable { scanStateManager.setScanning() }
            }

            viewIntentCompletable<OnPause> {
                it.flatMapCompletable { scanStateManager.turnCameraOff() }
            }

            viewIntentCompletable<EditAssetClick> {
                it.flatMapCompletable { intent ->
                    navigator.navigate(
                        AssetDetails(
                            AssetDataForNavigation(
                                assetId = intent.assetId,
                                taskId = taskId,
                                newAsset = false,
                                unexpectedWarning = TypeWarnings.NO_WARNING,
                                originPage = R.id.captureFragment
                            )
                        )
                    )
                }
            }

            viewIntentCompletable<DeleteCapturedAsset> {
                it.flatMapCompletable { intent ->
                    navigator.navigate(DeleteAsset(intent.assetId, taskId!!))
                }
            }

            viewIntentCompletable<AssetClicked> {
                it.flatMapCompletable { intent ->
                    navigator.navigate(ShowAssetDetails(assetId = intent.assetId, taskId = taskId))
                }
            }

            viewIntentCompletable<CaptureButtonClicked> {
                it.flatMapCompletable { scanStateManager.toggleCapture() }
            }

            viewIntentCompletable<ToggleAutoScan> {
                it.flatMapCompletable { scanStateManager.toggleAutoScan() }
            }

            viewIntentCompletable<ConsumeClicked> {
                it.flatMapCompletable { captureModeManager.setConsume() }
            }

            viewIntentCompletable<RejectClicked> {
                it.flatMapCompletable { captureModeManager.setReject() }
            }

            viewIntentCompletable<ExitFlow> {
                it.flatMapCompletable { navigator.popBackStack() }
            }

            viewIntentCompletable<AddTag> {
                it.flatMapCompletable { intent ->
                    assetTagCache.edit {
                        add(intent.tag)
                        this
                    }
                }
            }

            viewIntentCompletable<Intent.DeleteTag> {
                it.flatMapCompletable { intent ->
                    captureModeManager.state().flatMapCompletable { captureMode ->
                        if (captureMode is CaptureMode.Tags) {
                            navigator.navigate(
                                NavDestination.NavDestinationArgs.DeleteTag(
                                    assetId = captureMode.assetId,
                                    tag = intent.tag
                                )
                            )
                        } else Completable.complete()
                    }
                }
            }

            viewIntentCompletable<DoneClicked> {
                it.flatMapCompletable {
                    Single.zip(
                        assetTagCache.get(),
                        assetDataCache.get(),
                        { tags, data -> Pair(tags, data) }
                    ).flatMapCompletable { (tags, dataCache) ->
                        tasksRepository.updateAssetTagList(dataCache.assetId, tags.toList())
                            .andThen(assetTagCache.clear())
                            .andThen(captureMethodManager.leaveTagMode())
                            .andThen(captureModeManager.setAsset())
                            .andThen(navigator.navigate(AssetDetails(dataCache)))
                    }
                }
            }

            viewIntentPassThroughs(DismissDialog::class, SetAutoScanNotificationMessage::class)
        }

    private fun showConsumptionDuplicate(consume: Boolean): Observable<Intent> {
        return Completable.complete()
            .delay(3, TimeUnit.SECONDS)
            .andThen(scanStateManager.setScanning())
            .andThen(Observable.just<Intent>(SetDialog(null)))
            .startWith(Observable.just(SetDialog(ConsumptionDuplicate(consume))))
    }

    private fun handleFoundAssets(
        result: CaptureUseCase.AssetResult,
        tag: String,
    ): Observable<Intent> {
        return when (result) {
            is NoAsset -> Observable.just(SetDialog(AssetNotFound))
            is OneAsset -> {
                captureUseCase.handleCapture(result.asset.id, tag).flatMap { endResult ->
                    when (endResult) {
                        EndResult.Continue -> continueScanning().toObservable()
                        EndResult.ConsumptionDuplicate -> showConsumptionDuplicate(true)
                        EndResult.RejectDuplicate -> showConsumptionDuplicate(false)
                    }
                }
            }
            is MultipleAssets -> { // multiple assets found
                conflictIdsCache.reset()
                    .andThen(conflictIdsCache.setData(result.assets, tag))
                    .andThen(navigator.navigate(ConflictHandler))
                    .toObservable()
            }
            is WrongProjectAsset -> Observable.just(
                SetDialog(
                    AssetFromWrongProject(
                        currentProjectName = result.currentProjectName,
                        otherProjectNames = result.otherProjectNames
                    )
                )
            )
        }
    }

    private fun continueScanning(): Completable {
        return Completable.complete()
            .delay(1000, TimeUnit.MILLISECONDS)
            .andThen(scanStateManager.setScanning())
    }
}
