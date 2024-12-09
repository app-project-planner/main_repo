package com.example.mobile_pj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobile_pj.login.LoginViewModel
import com.example.mobile_pj.navigation.NavGraph
import com.example.mobile_pj.navigation.Routes
import com.example.mobile_pj.ui.theme.AppTheme
import com.example.mobile_pj.viewmodel.SplashViewModel

class MainActivity : ComponentActivity() {
    private val splashViewModel by viewModels<SplashViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private val loginState: Boolean? by lazy { splashViewModel.loginState.value }
    private val splashState: Boolean by lazy { splashViewModel.splashState.value }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel.checkLogin()
        installSplashScreen().setKeepOnScreenCondition{ splashState }
        setContent {
            AppTheme {
                val navHostController = rememberNavController()
                val currentRoute by navHostController.currentBackStackEntryAsState()
                val isLogin = loginViewModel.isLogin.collectAsState()

                Surface(color = MaterialTheme.colorScheme.background) {
                    NavGraph(navController = navHostController,
                        startDestination = if (loginState!!) Routes.DASHBOARD else Routes.LOGIN) // 네비게이션 그래프 실행
                }
            }
        }
    }
}