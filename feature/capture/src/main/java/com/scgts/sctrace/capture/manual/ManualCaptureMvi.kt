package com.scgts.sctrace.capture.manual

import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.base.model.DialogType
import com.scgts.sctrace.base.model.AssetAttribute
import com.scgts.sctrace.base.model.AssetCardUiModel
import util.CaptureMode

interface ManualCaptureMvi {
    sealed class Intent : MviIntent {
        //view intents
        data class AttributeExpandClicked(val attribute: AssetAttribute) : Intent()
        data class AttributeEdited(val attribute: AssetAttribute, val selected: String) : Intent()
        object CloseSelector : Intent()
        data class FindAsset(val consumed: Boolean? = null) : Intent()
        data class AssetClicked(val assetId: String) : Intent()
        data class FindAssetTag(val tag: String, val consumed: Boolean? = null) : Intent()
        data class SearchForAttribute(val query: String) : Intent()
        object DismissDialog : Intent()
        object TabSelected : Intent()

        //data intents
        data class Assets(val assets: List<AssetCardUiModel>) : Intent()
        data class SetCaptureMode(val captureMode: CaptureMode) : Intent()
        data class ShowDialog(val dialogType: DialogType) : Intent()
        data class AttributeData(val attributes: List<Pair<AssetAttribute, String?>>) : Intent()
        data class ExpandAttribute(
            val attribute: AssetAttribute,
            val options: List<String>,
        ) : Intent()

        object NoOp : Intent()
    }

    data class ViewState(
        val attributes: List<Pair<AssetAttribute, String?>> = emptyList(),
        val dialogType: DialogType? = null,
        val showAttributeSelector: Boolean = false,
        val optionsForSelectedAttribute: SelectorData? = null,
        val findButtonEnabled: Boolean = false,
        val assets: List<AssetCardUiModel> = emptyList(),
        val searchQuery: String? = null,
        val captureMode: CaptureMode? = null,
        val noOpToggle: Boolean = false,
        override val error: Throwable? = null,
        override val loading: Boolean = false,
    ) : MviViewState

    data class SelectorData(
        val attribute: AssetAttribute,
        val selections: List<String>,
        val selected: String?,
    )
}
