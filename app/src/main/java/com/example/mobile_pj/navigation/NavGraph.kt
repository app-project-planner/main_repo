package com.example.mobile_pj.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobile_pj.ui.screens.DashboardScreen
import com.example.mobile_pj.ui.screens.LoginScreen
import com.example.mobile_pj.ui.screens.SignUpScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onSignUpClick = { navController.navigate("signup") },
                onLoginClick = { navController.navigate("dashboard") },
                onGoogleClick = {}
            )
        }
        composable("signup") {
            SignUpScreen(
                onRegisterClick = { navController.navigate("login") }
            )
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}
