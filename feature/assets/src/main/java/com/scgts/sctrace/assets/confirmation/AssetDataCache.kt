package com.scgts.sctrace.assets.confirmation

import com.scgts.sctrace.framework.navigation.NavDestination.NavDestinationArgs.AssetDataForNavigation
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache

class AssetDataCache : InMemoryObjectCache<AssetDataForNavigation>(AssetDataForNavigation("", originPage = 0))