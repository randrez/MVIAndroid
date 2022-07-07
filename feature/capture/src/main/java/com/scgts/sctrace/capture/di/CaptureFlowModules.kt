package com.scgts.sctrace.capture.di

import com.scgts.sctrace.base.model.CurrentTask
import com.scgts.sctrace.capture.CaptureUseCase
import com.scgts.sctrace.capture.tag_conflict.ConflictIdsCache
import org.koin.dsl.module

class CaptureFlowModules(
    projectId: String,
    taskId: String?,
    quickReject: Boolean = false
) {

    private val taskIdModule = module {
        single { CurrentTask(taskId, projectId, quickReject) }
    }

    private val captureUseCase = module {
        single { CaptureUseCase(get(), get(), get(), get(), get(), get()) }
    }

    private val assetConflictIdsModule = module {
        single { ConflictIdsCache() }
    }

    val modules = listOf(taskIdModule, captureUseCase, assetConflictIdsModule)
}
