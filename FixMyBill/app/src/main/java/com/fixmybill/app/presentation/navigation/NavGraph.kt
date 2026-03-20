package com.fixmybill.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private const val TRANSITION_DURATION = 300

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION))
        }
    ) {
        composable(route = Screen.Onboarding.route) {
            // TODO: OnboardingScreen(navController = navController)
        }

        composable(route = Screen.Auth.route) {
            // TODO: AuthScreen(navController = navController)
        }

        composable(route = Screen.Home.route) {
            // TODO: HomeScreen(navController = navController)
        }

        composable(route = Screen.Scan.route) {
            // TODO: ScanScreen(navController = navController)
        }

        composable(
            route = Screen.Analysis.route,
            arguments = listOf(
                navArgument("billId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val billId = backStackEntry.arguments?.getLong("billId") ?: return@composable
            // TODO: AnalysisScreen(billId = billId, navController = navController)
        }

        composable(route = Screen.History.route) {
            // TODO: HistoryScreen(navController = navController)
        }

        composable(
            route = Screen.Complaint.route,
            arguments = listOf(
                navArgument("billId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val billId = backStackEntry.arguments?.getLong("billId") ?: return@composable
            // TODO: ComplaintScreen(billId = billId, navController = navController)
        }

        composable(route = Screen.Settings.route) {
            // TODO: SettingsScreen(navController = navController)
        }

        composable(route = Screen.Premium.route) {
            // TODO: PremiumScreen(navController = navController)
        }
    }
}
