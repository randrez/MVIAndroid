package com.scgts.sctrace.di

import com.scgts.sctrace.BuildConfig
import com.scgts.sctrace.assets.di.AssetFeatureModule
import com.scgts.sctrace.capture.di.CaptureFeatureModule
import com.scgts.sctrace.feature.landing.di.LandingFeatureModule
import com.scgts.sctrace.feature.login.di.LoginFeatureModule
import com.scgts.sctrace.feature.settings.di.SettingsFeatureModule
import com.scgts.sctrace.feature.tablet.BaseTabletModule
import com.scgts.sctrace.ad_hoc_action.di.AdHocActionFeatureModule
import com.scgts.sctrace.rack_transfer.di.RackTransferModule
import com.scgts.sctrace.see_details.di.SeeDetailsFeatureModule
import com.scgts.sctrace.task_summary.di.TaskSummaryFeatureModule
import org.koin.core.module.Module

object FeatureModuleProvider {
    val modules: List<Module>
        get() = ArrayList<Module>().apply {
            addAll(LoginFeatureModule.modules)
            addAll(SettingsFeatureModule.modules)
            addAll(LandingFeatureModule.modules)
            addAll(AdHocActionFeatureModule.modules)
            addAll(CaptureFeatureModule(BuildConfig.SCANDIT_LICENSE_KEY).modules)
            addAll(SeeDetailsFeatureModule.modules)
            addAll(BaseTabletModule.modules)
            addAll(TaskSummaryFeatureModule.modules)
            addAll(RackTransferModule.modules)
            addAll(AssetFeatureModule.modules)
        }
}
