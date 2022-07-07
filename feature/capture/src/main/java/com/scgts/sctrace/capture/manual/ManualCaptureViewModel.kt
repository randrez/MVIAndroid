package com.scgts.sctrace.capture.manual

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.model.AssetAttribute
import com.scgts.sctrace.base.model.AssetAttribute.*
import com.scgts.sctrace.base.model.CaptureMethod.Manual
import com.scgts.sctrace.base.model.CurrentTask
import com.scgts.sctrace.base.model.DialogType.*
import com.scgts.sctrace.base.model.TaskType.AD_HOC_RACK_TRANSFER
import com.scgts.sctrace.base.model.TaskType.RACK_TRANSFER
import com.scgts.sctrace.base.util.toUiModels
import com.scgts.sctrace.capture.CaptureUseCase
import com.scgts.sctrace.capture.CaptureUseCase.AssetResult.*
import com.scgts.sctrace.capture.CaptureUseCase.EndResult
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.*
import com.scgts.sctrace.capture.manual.ManualCaptureMvi.Intent.*
import com.scgts.sctrace.capture.tag_conflict.ConflictIdsCache
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination.ConflictHandler
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.*
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import com.scgts.sctrace.tasks.TasksRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.*
import util.CaptureMode.Consumption.Consume
import util.CaptureMode.Consumption.Unknown

class ManualCaptureViewModel(
    private val currentTask: CurrentTask,
    private val navigator: AppNavigator,
    private val tasksRepository: TasksRepository,
    private val captureModeManager: CaptureModeManager,
    private val conflictsIdsCache: ConflictIdsCache,
    private val captureUseCase: CaptureUseCase,
    private val captureMethodManager: CaptureMethodManager,
    private val rackTransferCache: RackTransferCache,
) : ViewModel(), MviViewModel<Intent, ViewState> {

    private val taskId: String? = currentTask.id

    private val initialState = Supplier { ViewState() }

    private val selectionsCache = InMemoryObjectCache(
        linkedMapOf<AssetAttribute, String?>(
            *AssetAttribute.values()
                .map { it to null }
                .toTypedArray()
        )
    )

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is AttributeData -> prev.copy(
                attributes = intent.attributes,
                showAttributeSelector = false,
                findButtonEnabled = intent.attributes.find { it.second == null } == null
            )
            is ExpandAttribute -> prev.copy(
                showAttributeSelector = true,
                optionsForSelectedAttribute = SelectorData(
                    intent.attribute,
                    intent.options,
                    prev.attributes.find { it.first == intent.attribute }?.second
                ),
                searchQuery = null
            )
            is CloseSelector -> prev.copy(
                showAttributeSelector = false,
                searchQuery = null
            )
            is Assets -> prev.copy(assets = intent.assets)
            is SearchForAttribute -> prev.copy(searchQuery = intent.query)
            is SetCaptureMode -> prev.copy(captureMode = intent.captureMode)
            is ShowDialog -> prev.copy(dialogType = intent.dialogType)
            is DismissDialog -> prev.copy(dialogType = null)
            is TabSelected -> prev.copy(noOpToggle = !prev.noOpToggle) // to trigger render on tabs that may have been created after viewState was emitted last
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
            .distinctUntilChanged()
    }

    private fun bindIntents(intents: Observable<Intent>) = intentsBuild(intents) {

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
                        Observable.just(Assets(assets.toUiModels()))
                    }
                }
            }
        }

        dataIntent(captureModeManager.stateObs()) {
            it.map { captureMode -> SetCaptureMode(captureMode) }
        }

        dataIntent(captureModeManager.showConsumptionDuplicateObs()) {
            it.flatMap { consumptionType ->
                captureMethodManager.captureMethod().map { captureMethod ->
                    val consumed = consumptionType is Consume
                    if (captureMethod == Manual && consumptionType != Unknown) {
                        ShowDialog(ConsumptionDuplicate(consumed))
                    } else NoOp
                }.toObservable()
            }
        }

        dataIntent(selectionsCache.getObservable()) {
            it.map { attributeMap ->
                AttributeData(
                    attributeMap.toList().map { entry ->
                        val selectionFormatted =
                            if (entry.first == ExMillDate && !entry.second.isNullOrEmpty()) {
                                convertDateFormat(value = entry.second!!, format = "yyyy-MM-dd")
                            } else entry.second
                        Pair(entry.first, selectionFormatted)
                    }
                )
            }
        }

        viewIntentObservable<AttributeExpandClicked> {
            it.flatMapSingle { intent ->
                selectionsCache.get()
                    .flatMap { tasksRepository.getAttributes(it, currentTask.projectId) }
                    .map<Intent> { attributeMap ->
                        var options =
                            attributeMap.getOrDefault(intent.attribute, emptyList()).toList()
                        options = if (intent.attribute == PipeNumber) {
                            options.map { it.toInt() }.sorted().map { it.toString() }
                        } else {
                            options.sorted()
                        }
                        ExpandAttribute(
                            attribute = intent.attribute,
                            options = options
                        )
                    }
            }
        }

        viewIntentCompletable<AttributeEdited> {
            it.flatMapCompletable { intent ->
                selectionsCache.edit {
                    this[intent.attribute] = intent.selected
                    this
                }
            }
        }

        viewIntentObservable<FindAsset> {
            it.flatMap { intent ->
                selectionsCache.get().flatMap { selections ->
                    tasksRepository.captureByAttributes(
                        manufacturer = selections[Manufacturer]!!,
                        millWorkNumber = selections[MillWorkNumber]!!,
                        heatNumber = selections[HeatNumber]!!,
                        pipeNumber = selections[PipeNumber]!!,
                        exMillDate = selections[ExMillDate]!!
                    )
                }.flatMapObservable { asset ->
                    selectionsCache.edit {
                        this[PipeNumber] = null
                        this[ExMillDate] = null
                        this
                    }.andThen(handleCaptureAndSetCaptureMode(asset.id, intent.consumed))
                }
            }
        }

        viewIntentObservable<FindAssetTag> {
            it.flatMap { intent ->
                captureUseCase.getAssetsByTag(intent.tag).flatMapObservable GetAsset@{ result ->
                    when (result) {
                        is NoAsset -> return@GetAsset Observable.just(ShowDialog(AssetNotFound))
                        is OneAsset -> handleCaptureAndSetCaptureMode(
                            result.asset.id,
                            intent.consumed
                        )
                        is MultipleAssets ->  // multiple assets found
                            conflictsIdsCache.reset()
                                .andThen(conflictsIdsCache.setData(result.assets, intent.tag))
                                .andThen(navigator.navigate(ConflictHandler))
                                .andThen(
                                    when (intent.consumed) {
                                        true -> captureModeManager.setConsume()
                                        false -> captureModeManager.setReject()
                                        else -> captureModeManager.setAsset()
                                    }
                                ).toObservable()
                        is WrongProjectAsset -> return@GetAsset Observable.just(
                            ShowDialog(
                                AssetFromWrongProject(
                                    result.currentProjectName,
                                    result.otherProjectNames
                                )
                            )
                        )
                    }
                }
            }
        }

        viewIntentCompletable<AssetClicked> {
            it.flatMapCompletable { intent ->
                navigator.navigate(ShowAssetDetails(assetId = intent.assetId, taskId = taskId))
            }
        }

        viewIntentPassThroughs(
            CloseSelector::class,
            SearchForAttribute::class,
            DismissDialog::class,
            TabSelected::class
        )
    }

    private fun handleCaptureAndSetCaptureMode(
        assetId: String,
        consumed: Boolean?,
    ): Observable<Intent> =
        captureUseCase.handleCapture(assetId = assetId).map { endResult ->
            when (endResult) {
                EndResult.Continue -> NoOp
                EndResult.ConsumptionDuplicate -> ShowDialog(ConsumptionDuplicate(true))
                EndResult.RejectDuplicate -> ShowDialog(ConsumptionDuplicate(false))
            }
        }.startWith(
            when (consumed) {
                true -> captureModeManager.setConsume()
                false -> captureModeManager.setReject()
                else -> captureModeManager.setAsset()
            }
        )
}
