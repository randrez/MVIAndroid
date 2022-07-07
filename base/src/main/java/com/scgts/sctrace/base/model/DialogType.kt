package com.scgts.sctrace.base.model

sealed class DialogType(val tag: String) {
    data class ConsumptionDuplicate(val consumed: Boolean) : DialogType(CONSUMPTION_DUPLICATE_TAG)
    object AssetNotFound : DialogType(ASSET_NOT_FOUND_TAG)
    data class AssetFromWrongProject(
        val currentProjectName: String,
        val otherProjectNames: List<String>
    ) : DialogType(ASSET_FROM_WRONG_PROJECT_TAG)

    companion object {
        const val CONSUMPTION_DUPLICATE_TAG = "consumption duplicate tag"
        const val ASSET_NOT_FOUND_TAG = "asset not found tag"
        const val ASSET_FROM_WRONG_PROJECT_TAG = "asset from wrong project tag"
    }
}