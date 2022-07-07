package util

import org.koin.dsl.module

object ComponentsModules {

    private val captureMethodManagerModule = module {
        single<CaptureMethodManager> { CaptureMethodManagerImpl(get()) }
    }

    private val captureModeManagerModule = module {
        single<CaptureModeManager> { CaptureModeManagerImpl() }
    }

    private val scanStateManagerModule = module {
        single<ScanStateManager> { ScanStateManagerImpl() }
    }

    private val tasksManagerModule = module {
        single<TasksManager> { TasksManagerImpl() }
    }

    private val topToastModule = module {
        single<TopToastManager> { TopToastManagerImpl() }
    }

    private val rackTransferAssetCache = module {
        single { RackTransferCache() }
    }

    val modules = listOf(
        captureMethodManagerModule,
        captureModeManagerModule,
        scanStateManagerModule,
        tasksManagerModule,
        topToastModule,
        rackTransferAssetCache
    )
}