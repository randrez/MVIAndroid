package com.scgts.sctrace.capture

import com.scgts.sctrace.base.model.Asset
import com.scgts.sctrace.base.model.CurrentTask
import com.scgts.sctrace.base.model.OrderType.*
import com.scgts.sctrace.base.model.Task
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.base.model.TypeWarnings
import com.scgts.sctrace.base.model.TypeWarnings.*
import com.scgts.sctrace.capture.CaptureUseCase.AssetResult.*
import com.scgts.sctrace.capture.CaptureUseCase.EndResult.*
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.*
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType.ConsumedToRejected
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.RejectAsset.ConsumptionSwitchType.RejectedToConsumed
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import util.CaptureMode.Consumption.Consume
import util.CaptureMode.Consumption.Reject
import util.CaptureModeManager
import util.RackTransferCache
import util.TopToastManager

class CaptureUseCase(
    private val currentTask: CurrentTask,
    private val tasksRepository: TasksRepository,
    private val navigator: AppNavigator,
    private val captureModeManager: CaptureModeManager,
    private val topToastManager: TopToastManager,
    private val rackTransferCache: RackTransferCache,
) {
    sealed class EndResult {
        object Continue : EndResult()
        object ConsumptionDuplicate : EndResult()
        object RejectDuplicate : EndResult()
    }

    sealed class AssetResult {
        data class OneAsset(val asset: Asset) : AssetResult()
        data class MultipleAssets(val assets: List<Asset>) : AssetResult()
        data class WrongProjectAsset(
            val currentProjectName: String,
            val otherProjectNames: List<String>,
        ) : AssetResult()

        object NoAsset : AssetResult()
    }

    private val taskId: String? = currentTask.id

    fun getAssetsByTag(tag: String): Single<AssetResult> {
        return tasksRepository.getAssetsByTag(tag).map { assets ->
            val currentProjectAssets =
                assets.filter { asset -> asset.projectId == currentTask.projectId }
            when {
                assets.isEmpty() -> NoAsset
                currentProjectAssets.size == 1 -> OneAsset(currentProjectAssets[0])
                currentProjectAssets.size > 1 -> MultipleAssets(currentProjectAssets)
                else -> handleAssetsFromWrongProject(assets)
            }
        }.onErrorReturnItem(NoAsset)
    }

    private fun handleAssetsFromWrongProject(assets: List<Asset>): AssetResult {
        val projectIds: MutableList<String> = mutableListOf()
        assets.forEach { asset ->
            if (!projectIds.contains(asset.projectId) && asset.projectId != currentTask.projectId) {
                projectIds.add(asset.projectId)
            }
        }
        val currentProject = tasksRepository.getProjectById(currentTask.projectId).blockingGet()
        val otherProjectNames = tasksRepository.getProjectsName(projectIds).blockingGet()
        return WrongProjectAsset(currentProject.name, otherProjectNames)
    }

    /**
     *          Supported [OrderType] - [TaskType] Combinations:
     *
     *                      [INBOUND] - [INBOUND_FROM_MILL]
     *                      [INBOUND] - [AD_HOC_INBOUND_FROM_MILL]
     *
     *                     [OUTBOUND] - [BUILD_ORDER]
     *                     [OUTBOUND] - [DISPATCH]
     *                     [OUTBOUND] - [INBOUND_TO_WELL]
     *
     *                  [CONSUMPTION] - [CONSUME]
     *
     *              [RETURN_TRANSFER] - [DISPATCH_TO_YARD]
     *              [RETURN_TRANSFER] - [AD_HOC_DISPATCH_TO_YARD]
     *              [RETURN_TRANSFER] - [DISPATCH_TO_WELL]
     *              [RETURN_TRANSFER] - [AD_HOC_DISPATCH_TO_WELL]
     *              [RETURN_TRANSFER] - [INBOUND_TO_WELL]
     *              [RETURN_TRANSFER] - [AD_HOC_INBOUND_TO_WELL]
     *              [RETURN_TRANSFER] - [INBOUND_FROM_WELL_SITE]
     *              [RETURN_TRANSFER] - [AD_HOC_INBOUND_FROM_WELL_SITE]
     *              [RETURN_TRANSFER] - [RACK_TRANSFER]
     *
     *                      [NO_TYPE] - [AD_HOC_QUICK_SCAN]
     *                      [NO_TYPE] - [AD_HOC_REJECT_SCAN]
     *
     *  NOTE: Ad Hoc tasks are created locally from the quick actions menu and
     *  will never come from server. Ad Hoc Scan and Ad Hoc Quick Reject doesn't have
     *  a task so they'll give an error if we try to do getTask using their ids.
     *
     *  Note that readability and ease of modification was consciously chosen over optimization here!
     */
    fun handleCapture(assetId: String, tag: String? = null): Observable<EndResult> {
        return when (taskId) {
            //TODO: Change Ad Hoc Scan Task ID from null to ad_hoc_scan to make this more clear
            null -> showAssetConfirmationDialog(assetId, tag)
            AD_HOC_REJECT_SCAN.id -> showConsumptionDialog(assetId)
            else -> Single.zip(
                tasksRepository.getAsset(assetId),
                tasksRepository.getTask(taskId),
                { asset, task -> Pair(asset, task) }
            ).flatMapObservable { (asset, task) ->
                when (task.orderType) {
                        INBOUND -> {
                        when (task.type) {
                            INBOUND_FROM_MILL, AD_HOC_INBOUND_FROM_MILL -> showAssetConfirmationDialog(assetId, tag)
                            else -> doNothing()
                        }
                    }
                    OUTBOUND -> {
                        when (task.type) {
                            BUILD_ORDER, DISPATCH -> {
                                val warning = validateAsset(task.orderId, asset)
                                /**
                                 *  For BUILD_ORDER or DISPATCH task we need to check if the
                                 *  asset is expected in the order. If it is expected then add it
                                 *  with no confirmation. If it is not expected then have the user
                                 *  confirm that they want to add the unexpected asset/product.
                                 */
                                if (warning == NO_WARNING) createTraceEvent(asset, task)
                                else showAssetConfirmationDialog(assetId, tag, warning)
                            }
                            INBOUND_TO_WELL -> createTraceEvent(asset, task)
                            else -> doNothing()
                        }
                    }
                    CONSUMPTION -> {
                        when (task.type) {
                            CONSUME -> handleConsumptionTask(asset, task)
                            else -> doNothing()
                        }
                    }
                    RETURN_TRANSFER -> {
                        when (task.type) {
                            DISPATCH_TO_WELL, DISPATCH_TO_YARD, AD_HOC_DISPATCH_TO_YARD, AD_HOC_DISPATCH_TO_WELL ->
                                createTraceEvent(asset, task)
                            INBOUND_TO_WELL, AD_HOC_INBOUND_TO_WELL -> createTraceEvent(asset, task)
                            INBOUND_FROM_WELL_SITE, AD_HOC_INBOUND_FROM_WELL_SITE ->
                                showAssetConfirmationDialog(assetId, tag)
                            RACK_TRANSFER, AD_HOC_RACK_TRANSFER -> addToRackTransferAssetCache(asset)
                            else -> doNothing()
                        }
                    }
                    else -> doNothing()
                }
            }
        }
    }

    private fun addToRackTransferAssetCache(asset: Asset): Observable<EndResult> {
        return rackTransferCache.edit {
            assetIds.add(asset.id)
            this
        }
            .andThen(topToastManager.showAssetAddedToast())
            .andThen(doNothing())
    }

    private fun createTraceEvent(
        asset: Asset,
        task: Task,
        consumed: Boolean? = null,
        rackLocationId: String? = null,
    ): Observable<EndResult> {
        /**
         * Build Order task uses fromLocationId for facilityId because the assets for that task
         * are staying at the same location, whereas all the other tasks involves the asset moving
         * from one location to another.
         */
        val facilityId = if (task.type == BUILD_ORDER) task.fromLocationId else task.toLocationId
        return tasksRepository.addTraceEvent(
            taskId = task.id,
            assetId = asset.id,
            facilityId = facilityId!!,
            rackLocationId = rackLocationId,
            fromLocationId = task.fromLocationId,
            toLocationId = task.toLocationId,
            consumed = consumed,
            adHocActionTaskType = if (task.isAdHocAction()) task.type.serverName else null,
        )
            .andThen(topToastManager.showAssetAddedToast())
            .andThen(doNothing())
    }

    private fun handleConsumptionTask(asset: Asset, task: Task): Observable<EndResult> {
        return Single.zip(
            captureModeManager.state(),
            tasksRepository.traceEventExists(task.id, asset.id),
            { captureMode, traceEventExist -> Pair(captureMode, traceEventExist) }
        ).flatMapObservable { (captureMode, traceEventExist) ->
            if (traceEventExist) {
                tasksRepository.getTraceEvent(task.id, asset.id).flatMapObservable { traceEvent ->
                    val consumed = traceEvent.consumed!!
                    when {
                        consumed && captureMode is Consume -> Observable.just(ConsumptionDuplicate)
                        !consumed && captureMode is Reject -> Observable.just(RejectDuplicate)
                        consumed && captureMode is Reject ->
                            showConsumptionDialog(asset.id, ConsumedToRejected)
                        !consumed && captureMode is Consume ->
                            showConsumptionDialog(asset.id, RejectedToConsumed)
                        else -> doNothing()
                    }
                }
            } else {
                if (captureMode is Consume) createTraceEvent(asset, task, true)
                else showConsumptionDialog(asset.id)
            }
        }
    }

    private fun validateAsset(orderId: String, asset: Asset): TypeWarnings {
        return when {
            !tasksRepository.productVariantExpectedInOrder(
                orderId = orderId,
                productVariant = asset.productId
            ).blockingGet() -> WARNING_PRODUCT
            !tasksRepository.hasExpectedAmountContractNumber(
                orderId = orderId,
                productId = asset.productId,
                contractNumber = asset.contractNumber
            ).blockingGet() -> WARNING_CONTRACT
            asset.shipmentNumber == null || !tasksRepository.haxExpectedAmountShipmentNumber(
                orderId = orderId,
                productId = asset.productId,
                shipmentNumber = asset.shipmentNumber!!
            ).blockingGet() -> WARNING_SHIPMENT
            asset.conditionId == null && asset.rackLocationId == null || !tasksRepository.differentExpectedAmountConditionAndRackLocation(
                orderId = orderId,
                productId = asset.productId,
                conditionId = asset.conditionId!!,
                rackLocationId = asset.rackLocationId!!
            ).blockingGet() -> WARNING_CONDITION_RACK_LOCATION
            asset.conditionId == null || !tasksRepository.hasExpectedAmountCondition(
                orderId = orderId,
                productId = asset.productId,
                conditionId = asset.conditionId!!
            ).blockingGet() -> WARNING_CONDITION
            asset.rackLocationId == null || !tasksRepository.hasExpectedAmountRackLocation(
                orderId = orderId,
                productId = asset.productId,
                rackLocationId = asset.rackLocationId!!
            ).blockingGet() -> WARNING_RACK_LOCATION
            else -> NO_WARNING
        }
    }

    private fun showAssetConfirmationDialog(
        assetId: String,
        tag: String?,
        warning: TypeWarnings = NO_WARNING,
    ): Observable<EndResult> {
        return navigator.navigate(
            AssetDetails(
                AssetDataForNavigation(
                    assetId = assetId,
                    taskId = taskId,
                    newAsset = true,
                    scannedTag = tag,
                    unexpectedWarning = warning,
                    originPage = R.id.captureFragment
                )
            )
        ).toObservable()
    }

    private fun showConsumptionDialog(
        assetId: String,
        statusChange: ConsumptionSwitchType? = null,
    ): Observable<EndResult> {
        return navigator.navigate(
            RejectAsset(
                assetId = assetId,
                taskId = taskId!!,
                quickReject = currentTask.quickReject,
                statusChange = statusChange,
            )
        ).toObservable()
    }

    private fun doNothing(): Observable<EndResult> = Observable.just(Continue)
}