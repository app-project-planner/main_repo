package com.example.mobile_pj.login


import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Exception

class LoginViewModel: ViewModel() {
    private val _isLogin = MutableStateFlow<Boolean?>(null)
    val isLogin = _isLogin.asStateFlow()

    fun login(
        activityResult: ActivityResult,
        onSuccess: () -> Unit,
        onFailure: (Exception?) -> Unit = {}
    ) {
        try {
            _isLogin.value = false

            val account = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
                .getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _isLogin.value = true
                        onSuccess()
                    }
                    else onFailure(task.exception)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}