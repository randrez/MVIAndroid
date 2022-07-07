package com.scgts.sctrace.framework.navigation

import androidx.annotation.IdRes
import io.reactivex.rxjava3.core.Completable

interface AppNavigator {
    fun navigateWeb(destination: WebDestination): Completable

    fun navigate(
        destination: NavDestination,
        animation: ScreenAnimation? = null,
        popUpTo: NavDestination? = null
    ): Completable

    fun popBackStack(): Completable

    fun clearStack(): Completable

    fun popBackStackDestination(
        @IdRes destinationId: Int,
        inclusive: Boolean,
        saveState: Boolean = false
    ): Completable
}
