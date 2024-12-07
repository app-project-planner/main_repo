package com.example.mobile_pj.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel // ViewModel을 생성하기 위한 함수
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobile_pj.ui.screens.DashboardScreen
import com.example.mobile_pj.ui.screens.LoginScreen
import com.example.mobile_pj.ui.screens.QAPage
import com.example.mobile_pj.ui.screens.SignUpScreen
import com.example.mobile_pj.ui.screens.StatisticsPage
import com.example.mobile_pj.viewmodel.SharedViewModel // ViewModel import

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    // SharedViewModel 생성 (모든 화면에서 공유)
    val sharedViewModel: SharedViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN // 초기 화면: Login
    ) {
        composable(Routes.LOGIN) { // 로그인 화면
            LoginScreen(
                onSignUpClick = { navController.navigate(Routes.SIGNUP) },
                onLoginClick = { navController.navigate(Routes.DASHBOARD) }
            )
        }
        composable(Routes.SIGNUP) { // 회원가입 화면
            SignUpScreen(
                onRegisterClick = { navController.popBackStack(Routes.LOGIN, false) }
            )
        }
        composable(Routes.DASHBOARD) { // 대시보드 화면
            DashboardScreen(
                viewModel = sharedViewModel, // ViewModel 전달
                onQAClick = { navController.navigate(Routes.QA) },
                onStatisticsClick = { navController.navigate(Routes.STATISTICS) }
            )
        }
        composable(Routes.QA) { // Q&A 화면
            QAPage(sharedViewModel) // SharedViewModel을 전달
        }
        composable(Routes.STATISTICS) { // 통계 화면
            StatisticsPage(sharedViewModel) // SharedViewModel을 전달
        }
    }
}
