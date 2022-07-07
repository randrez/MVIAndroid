package com.scgts.sctrace

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.scgts.sctrace.auth.di.ActivityContext
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.framework.navigation.NavDestination
import com.scgts.sctrace.framework.navigation.ScreenAnimation
import com.scgts.sctrace.framework.navigation.WebDestination
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable

class NavigatorImpl(
    private val navController: NavController,
    private val activityContext: ActivityContext,
) : AppNavigator {
    override fun navigateWeb(destination: WebDestination): Completable {
        return Completable.fromAction {
            activityContext.context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = destination.uri
                }
            )
        }
    }

    override fun navigate(
        destination: NavDestination,
        animation: ScreenAnimation?,
        popUpTo: NavDestination?,
    ): Completable {
        return Completable.fromAction {

            val options = NavOptions.Builder()
                .addEnterPopAnim(animation ?: ScreenAnimation.FADE_IN)
                .addPopUpTo(popUpTo)
                .build()
            val isTablet = activityContext.context.resources.getBoolean(R.bool.isTablet)
            if (destination is NavDestination.NavDestinationArgs) {
                val newDestination = when {
                    destination is NavDestination.NavDestinationArgs.AdHocAction && isTablet ->
                        NavDestination.NavDestinationArgs.AdHocActionDialog(destination.action)
                    destination is NavDestination.NavDestinationArgs.Capture && isTablet ->
                        NavDestination.NavDestinationArgs.TabletCapture(
                            destination.projectId,
                            destination.taskId
                        )
                    else -> destination
                }
                navController.navigate(newDestination.navDirections, options)
            } else {
                val newDestination = when {
                    destination is NavDestination.Tasks && isTablet -> NavDestination.TabletTasks
                    else -> destination
                }
                navController.navigate(newDestination.id, null, options)
            }
        }.subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun popBackStack(): Completable {
        return Completable.fromAction { navController.popBackStack() }
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun clearStack(): Completable {
        return Completable.fromAction {
            while (navController.popBackStack()) {
                // will exit while loop when back stack empty; returns false
            }
        }.subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun popBackStackDestination(
        destinationId: Int,
        inclusive: Boolean,
        saveState: Boolean
    ): Completable {
        return Completable.fromAction {
            navController.popBackStack(
                destinationId = destinationId,
                inclusive = inclusive,
                saveState = saveState
            )
        }.subscribeOn(AndroidSchedulers.mainThread())
    }

    private fun NavOptions.Builder.addEnterPopAnim(animate: ScreenAnimation): NavOptions.Builder {
        return when (animate) {
            ScreenAnimation.SLIDE_IN_FROM_RIGHT -> {
                this.setEnterAnim(R.anim.fragment_slide_in_right)
                    .setExitAnim(R.anim.fragment_slide_out_left)
                    .setPopEnterAnim(R.anim.fragment_slide_in_left)
                    .setPopExitAnim(R.anim.fragment_slide_out_right)
            }
            ScreenAnimation.SLIDE_UP_FROM_BOTTOM -> {
                this.setEnterAnim(R.anim.fragment_slide_in_bottom)
                    .setExitAnim(R.anim.fragment_slide_out_bottom)
                    .setPopEnterAnim(R.anim.fragment_slide_in_bottom)
                    .setPopExitAnim(R.anim.fragment_slide_out_bottom)
            }
            ScreenAnimation.FADE_IN -> {
                this.setEnterAnim(R.anim.fragment_fade_enter)
                    .setExitAnim(R.anim.fragment_fade_exit)
                    .setPopEnterAnim(R.anim.fragment_fade_enter)
                    .setPopExitAnim(R.anim.fragment_fade_exit)
            }
        }
    }

    private fun NavOptions.Builder.addPopUpTo(popUpTo: NavDestination?): NavOptions.Builder {
        return if (popUpTo == null) {
            this
        } else {
            this.setPopUpTo(popUpTo.id, false)
        }
    }
}
