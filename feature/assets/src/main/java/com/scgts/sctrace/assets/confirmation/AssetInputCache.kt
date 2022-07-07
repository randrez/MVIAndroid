package com.scgts.sctrace.assets.confirmation

import com.scgts.sctrace.base.model.Condition
import com.scgts.sctrace.base.model.Length
import com.scgts.sctrace.base.model.RackLocation
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache

class AssetInputCache : InMemoryObjectCache<AssetInputs>(AssetInputs())

data class AssetInputs(
    var condition: Condition? = null,
    var rackLocation: RackLocation? = null,
    var length: Length? = null,
)