package com.scgts.sctrace.di

import androidx.navigation.NavController
import com.scgts.sctrace.NavigatorImpl
import com.scgts.sctrace.framework.navigation.AppNavigator
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

class NavigationModule(
    navController: NavController,
) {

    companion object {
        private const val GlobalNavController = "global nav"
    }

    private val navControllerModule = module {
        single(named(GlobalNavController)) { navController }
    }

    private val navigatorModule = module {
        single<AppNavigator> { NavigatorImpl(get(named(GlobalNavController)), get()) }
    }

    val modules: List<Module> = listOf(navControllerModule, navigatorModule)
}
