package com.example.mobile_pj.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel: ViewModel() {
    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState = _loginState.asStateFlow()

    private val _splashState = MutableStateFlow(true)
    val splashState = _splashState.asStateFlow()

    fun checkLogin() {
        viewModelScope.launch {
            val auth = FirebaseAuth.getInstance()
            _loginState.value = auth.currentUser != null
            if (_loginState.value != null) _splashState.value = false
        }
    }
}