package com.example.mobile_pj.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_pj.R
import com.example.mobile_pj.login.component.GoogleSignButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions



@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onGoogleClick: ()-> Unit) {

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        loginViewModel.login(
            activityResult = it,
            onSuccess = {
                Toast.makeText(context, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                onGoogleClick()
            },
            onFailure = {
                Toast.makeText(context, "로그인이 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(3f))
        // 제목
        Text(
            text = "Loop Learn",
            style = MaterialTheme.typography.displayLarge,
            color = Color(0xFF8AAE92), // Figma의 색상 반영
            modifier = Modifier
                .background(Color(0xCCFFF6A7))
                .padding(5.dp),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            modifier = Modifier
                .background(Color(0xCCFFF6A7))
                .padding(5.dp),
            text = "학습 계획 관리",
            fontSize = 19.sp
        )
        Spacer(modifier = Modifier.weight(3f))

        val token = stringResource(id = R.string.default_web_client_id)
        GoogleSignButton {
            val googleSignInOptions = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
            launcher.launch(googleSignInClient.signInIntent)
        }

        Spacer(modifier = Modifier.weight(2f))

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        onGoogleClick = {}
    )
}




