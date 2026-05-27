package com.aureliajets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.aureliajets.ui.Routes
import com.aureliajets.ui.screens.*
import com.aureliajets.ui.theme.AureliaJetsTheme
import com.aureliajets.viewmodel.AureliaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AureliaJetsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    AureliaJetsApp()
                }
            }
        }
    }
}

@Composable
fun AureliaJetsApp() {
    val navController = rememberNavController()
    val viewModel: AureliaViewModel = viewModel()

    // Skip splash/login if session is active
    val startDest = if (viewModel.isSessionActive) Routes.BOOK_FLIGHT else Routes.SPLASH

    NavHost(navController = navController, startDestination = startDest) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onContinue = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.BOOK_FLIGHT) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onSignUp = { navController.navigate(Routes.SIGNUP) }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                viewModel = viewModel,
                onSignUpSuccess = {
                    navController.navigate(Routes.BOOK_FLIGHT) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.BOOK_FLIGHT) {
            BookFlightScreen(
                viewModel = viewModel,
                onContinue = { navController.navigate(Routes.JET_LIST) },
                onBack = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.BOOK_FLIGHT) { inclusive = true }
                    }
                },
                onProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.JET_LIST) {
            JetListScreen(
                viewModel = viewModel,
                onJetSelected = { index -> navController.navigate(Routes.jetDetail(index)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.JET_DETAIL,
            arguments = listOf(navArgument("jetIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val jetIndex = backStackEntry.arguments?.getInt("jetIndex") ?: 0
            JetDetailScreen(
                jetIndex = jetIndex,
                viewModel = viewModel,
                onBookClick = { navController.navigate(Routes.bookingDetails(jetIndex)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.BOOKING_DETAILS,
            arguments = listOf(navArgument("jetIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val jetIndex = backStackEntry.arguments?.getInt("jetIndex") ?: 0
            BookingDetailsScreen(
                jetIndex = jetIndex,
                viewModel = viewModel,
                onConfirm = {
                    navController.navigate(Routes.BOOKING_SUCCESS) {
                        popUpTo(Routes.JET_LIST) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.BOOKING_SUCCESS) {
            BookingSuccessScreen(
                viewModel = viewModel,
                onGenerateTicket = { navController.navigate(Routes.TICKET) },
                onContinue = { navController.navigate(Routes.RATING) }
            )
        }

        composable(Routes.TICKET) {
            TicketScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.RATING) {
            RatingScreen(
                viewModel = viewModel,
                onDone = {
                    navController.navigate(Routes.BOOK_FLIGHT) {
                        popUpTo(Routes.BOOK_FLIGHT) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
