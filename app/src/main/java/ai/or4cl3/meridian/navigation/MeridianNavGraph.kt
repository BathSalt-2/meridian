package ai.or4cl3.meridian.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import ai.or4cl3.meridian.data.preferences.CommunityPreferences
import ai.or4cl3.meridian.ui.dashboard.DashboardScreen
import ai.or4cl3.meridian.ui.iris.IrisScreen
import ai.or4cl3.meridian.ui.locus.LocusScreen
import ai.or4cl3.meridian.ui.praxis.PraxisScreen
import ai.or4cl3.meridian.ui.onboarding.OnboardingScreen
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ai.or4cl3.meridian.data.preferences.dataStore

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Iris : Screen("iris")
    object Locus : Screen("locus")
    object Praxis : Screen("praxis/{alertId}") {
        fun withAlert(alertId: String = "none") = "praxis/$alertId"
    }
}

@Composable
fun MeridianNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val isOnboardingComplete by context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[booleanPreferencesKey("onboarding_complete")] ?: false }
        .collectAsState(initial = null)

    val startDestination = when (isOnboardingComplete) {
        true -> Screen.Dashboard.route
        false -> Screen.Onboarding.route
        null -> return // Still loading — show nothing until determined
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn() + slideInHorizontally { it / 4 } },
        exitTransition = { fadeOut() + slideOutHorizontally { -it / 4 } },
        popEnterTransition = { fadeIn() + slideInHorizontally { -it / 4 } },
        popExitTransition = { fadeOut() + slideOutHorizontally { it / 4 } }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToIris = { navController.navigate(Screen.Iris.route) },
                onNavigateToLocus = { navController.navigate(Screen.Locus.route) },
                onNavigateToPraxis = { alertId ->
                    navController.navigate(Screen.Praxis.withAlert(alertId))
                }
            )
        }
        composable(Screen.Iris.route) {
            IrisScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Locus.route) {
            LocusScreen(
                onBack = { navController.popBackStack() },
                onOpenPraxis = { alertId -> navController.navigate(Screen.Praxis.withAlert(alertId)) }
            )
        }
        composable(
            route = Screen.Praxis.route,
            arguments = listOf(navArgument("alertId") { defaultValue = "none" })
        ) { backStack ->
            val alertId = backStack.arguments?.getString("alertId") ?: "none"
            PraxisScreen(
                initialAlertId = alertId.takeIf { it != "none" },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun navArgument(
    name: String,
    builder: androidx.navigation.NavArgumentBuilder.() -> Unit
) = androidx.navigation.navArgument(name, builder)
