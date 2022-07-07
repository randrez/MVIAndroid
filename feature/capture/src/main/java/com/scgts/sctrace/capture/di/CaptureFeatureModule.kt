package com.scgts.sctrace.capture.di

import com.scgts.sctrace.capture.CaptureViewModel
import com.scgts.sctrace.capture.manual.ManualCaptureViewModel
import com.scgts.sctrace.capture.scan.CaptureCameraViewModel
import com.scgts.sctrace.capture.tag_conflict.ConflictHandlerViewModel
import com.scgts.sctrace.framework.view.BaseCaptureCameraFragment.Companion.SCANDIT_LICENSE_KEY
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

class CaptureFeatureModule(
    private val scanditKey : String
) {
    private val viewModelModule = module {
        viewModel { CaptureViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
        viewModel { CaptureCameraViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
        viewModel { ManualCaptureViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
        viewModel { ConflictHandlerViewModel( get(), get(), get(), get()) }

        single (named(SCANDIT_LICENSE_KEY)) { scanditKey }
    }

    val modules: List<Module> = listOf(viewModelModule)
}
