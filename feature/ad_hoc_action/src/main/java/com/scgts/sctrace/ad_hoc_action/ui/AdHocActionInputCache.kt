package com.scgts.sctrace.ad_hoc_action.ui

import com.scgts.sctrace.base.model.DispatchType
import com.scgts.sctrace.base.model.DispatchType.AWAITING_SELECTION
import com.scgts.sctrace.base.model.Facility
import com.scgts.sctrace.base.model.Project
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import org.threeten.bp.ZonedDateTime

class AdHocActionInputCache : InMemoryObjectCache<AdHocActionInput>(AdHocActionInput())

data class AdHocActionInput(
    var project: Project? = null,
    var date: ZonedDateTime = ZonedDateTime.now(),
    var yard: Facility? = null,
    var rack: RackLocation? = null,
    var rig: Facility? = null,
    var well: Facility? = null,
    // ad hoc dispatch inputs
    var dispatchType: DispatchType = AWAITING_SELECTION,
    var fromRig: Facility? = null,
    var fromWell: Facility? = null,
    var toRig: Facility? = null,
    var toWell: Facility? = null,
    var toYard: Facility? = null,
)