package com.example.mobile_pj.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobile_pj.ui.screens.DashboardScreen
import com.example.mobile_pj.login.LoginScreen
import com.example.mobile_pj.ui.screens.GeminiScreen
import com.example.mobile_pj.ui.screens.QAPage
import com.example.mobile_pj.ui.screens.SignUpScreen
import com.example.mobile_pj.ui.screens.StatisticsPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun NavGraph(
    loginState : Boolean?,
    navController: NavHostController = rememberNavController(),
) {
    val auth : FirebaseAuth = Firebase.auth
    NavHost(
        navController = navController,
        startDestination = if (loginState!!) Routes.DASHBOARD else Routes.LOGIN // Routes를 사용
    ) {
        composable(Routes.LOGIN) { // 로그인 화면
            LoginScreen(
                auth = auth,
                onSignUpClick = {navController.navigate(Routes.SIGNUP) },
                onLoginClick = {navController.navigate(Routes.DASHBOARD) },
                onGoogleClick = {navController.navigate(Routes.DASHBOARD)}
            )
        }
        composable(Routes.SIGNUP) { // 회원가입 화면
            SignUpScreen(
                auth = auth,
                onRegisterClick = { navController.popBackStack(Routes.LOGIN, false) }
            )
        }
        composable(Routes.DASHBOARD) { // 대시보드 화면
            DashboardScreen(
                onQAClick = { navController.navigate(Routes.QA) },
                onStatisticsClick = { navController.navigate(Routes.STATISTICS) },
                onLogOutClick = {navController.navigate(Routes.LOGIN) }
            )
        }
        composable(Routes.QA) {
            QAPage()
        }
        composable(Routes.STATISTICS) {
            StatisticsPage()
        }
        composable("gemini") {
            GeminiScreen()
        }
    }
}
