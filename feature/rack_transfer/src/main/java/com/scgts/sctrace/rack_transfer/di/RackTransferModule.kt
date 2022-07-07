package com.scgts.sctrace.rack_transfer.di

import com.scgts.sctrace.rack_transfer.edittransfer.EditRackTransferViewModel
import com.scgts.sctrace.rack_transfer.rackdetails.RackDetailsViewModel
import com.scgts.sctrace.rack_transfer.tasksummary.RackTransferTaskSummaryViewModel
import com.scgts.sctrace.rack_transfer.transferselection.RackTransferSelectionViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object RackTransferModule {

    private val viewModelModule = module {
        viewModel { (taskId: String) ->
            RackTransferSelectionViewModel(taskId, get(), get(), get(), get())
        }
        viewModel { (taskId: String, rackId: String, millWorkNum: String, productId: String) ->
            RackDetailsViewModel(taskId, rackId, productId, millWorkNum, get(), get(), get())
        }
        viewModel { (taskId: String, rackId: String, millWorkNum: String, productId: String) ->
            EditRackTransferViewModel(taskId, rackId, productId, millWorkNum, get(), get(), get())
        }
        viewModel { (taskId: String, orderId: String, isTablet: Boolean) ->
            RackTransferTaskSummaryViewModel(taskId, orderId, get(), get(), get(), isTablet, get())
        }
    }

    val modules: List<Module> = listOf(viewModelModule)
}