package com.scgts.sctrace.assets.tags

import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache

class AssetTagCache : InMemoryObjectCache<MutableSet<String>>(mutableSetOf())