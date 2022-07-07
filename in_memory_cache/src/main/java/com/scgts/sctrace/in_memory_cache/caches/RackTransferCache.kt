package util

import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache

class RackTransferCache : InMemoryObjectCache<RackTransfer>(RackTransfer())

data class RackTransfer(
    var rackLocationId: String? = null,
    var assetIds: MutableSet<String> = mutableSetOf(),
    var toDeleteAssets: MutableSet<String> = mutableSetOf()
)