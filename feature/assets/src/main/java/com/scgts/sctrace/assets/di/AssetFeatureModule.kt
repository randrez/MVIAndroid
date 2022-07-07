package com.scgts.sctrace.assets.di

import com.scgts.sctrace.assets.confirmation.AssetConfirmationFragmentArgs
import com.scgts.sctrace.assets.confirmation.AssetConfirmationViewModel
import com.scgts.sctrace.assets.confirmation.AssetDataCache
import com.scgts.sctrace.assets.confirmation.AssetInputCache
import com.scgts.sctrace.assets.confirmation.discard.AssetDiscardConfirmationFragmentArgs
import com.scgts.sctrace.assets.confirmation.discard.AssetDiscardConfirmationViewModel
import com.scgts.sctrace.assets.consumption.ConsumptionRejectFragmentArgs
import com.scgts.sctrace.assets.consumption.ConsumptionViewModel
import com.scgts.sctrace.assets.detail.AssetDetailFragmentArgs
import com.scgts.sctrace.assets.detail.AssetDetailViewModel
import com.scgts.sctrace.assets.length.EditAssetLengthViewModel
import com.scgts.sctrace.assets.tags.AssetTagCache
import com.scgts.sctrace.assets.tags.discard.TagDiscardConfirmationFragmentArgs
import com.scgts.sctrace.assets.tags.discard.TagDiscardConfirmationViewModel
import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.AssetDataForNavigation
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object AssetFeatureModule {

    private val assetTagCacheModule = module {
        single { AssetTagCache() }
    }

    private val assetDataCacheModule = module {
        single { AssetDataCache() }
    }

    private val assetInputCacheModule = module {
        single { AssetInputCache() }
    }

    private val viewModelModule = module {
        viewModel { (args: AssetConfirmationFragmentArgs) ->
            AssetConfirmationViewModel(
                AssetDataForNavigation(
                    args.assetId,
                    args.taskId,
                    args.new,
                    args.scannedTag,
                    args.unexpectedWarning,
                    args.originPage
                ),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel { (args: AssetDiscardConfirmationFragmentArgs) ->
            AssetDiscardConfirmationViewModel(args.assetId, args.taskId, get(), get(), get())
        }

        viewModel { (args: TagDiscardConfirmationFragmentArgs) ->
            TagDiscardConfirmationViewModel(args.assetId, args.tag, get(), get())
        }

        viewModel { EditAssetLengthViewModel(get(), get(), get()) }

        viewModel { (args: ConsumptionRejectFragmentArgs) ->
            ConsumptionViewModel(
                args.assetId,
                args.taskId, get(), get(),
                args.consumptionStatusChange,
                get(),
                get(),
                args.quickReject
            )
        }

        viewModel { (args: AssetDetailFragmentArgs) ->
            AssetDetailViewModel(args.assetId, args.taskId, get(), get(), get())
        }
    }
    val modules: List<Module> = listOf(
        viewModelModule,
        assetTagCacheModule,
        assetDataCacheModule,
        assetInputCacheModule,
    )
}
