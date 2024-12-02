package com.example.mobile_pj.login

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_pj.R
import com.example.mobile_pj.login.component.GoogleSignButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    auth : FirebaseAuth,
    loginViewModel: LoginViewModel = viewModel(),
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit,
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
        // 제목
        Text(
            text = "Loop Learn",
            style = MaterialTheme.typography.displayLarge,
            color = Color(0xFF8AAE92), // Figma의 색상 반영
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Email 입력 필드
        var email by remember { mutableStateOf("") }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF8AAE92),
                focusedBorderColor = Color(0xFF8AAE92),
                unfocusedBorderColor = Color(0xFFC4C4C4),
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password 입력 필드
        var password by remember { mutableStateOf("") }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF8AAE92),
                unfocusedBorderColor = Color(0xFFC4C4C4),
                focusedTextColor = Color(0xFF8AAE92)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login 버튼
        Button(
            onClick = {
                signIn(auth, email, password, context, onLoginClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8AAE92)
            )
        ) {
            Text("Log in", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // "or" 텍스트
        Text("or", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(20.dp))

        // 소셜 로그인 버튼 (Figma 아이콘 반영)


        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 소셜 로그인 아이콘 (예시)
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


        }

        Spacer(modifier = Modifier.height(32.dp))

        // 회원가입 버튼
        TextButton(onClick = { onSignUpClick()}) {
            Text("Sign up", color = Color(0xFF8AAE92))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        auth= Firebase.auth,
        onSignUpClick = { }, // 기본값 설정
        onLoginClick = { },   // 기본값 설정
        onGoogleClick = {}
    )
}


fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    context: android.content.Context,
    onLoginSuccess: () -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onLoginSuccess()
            } else {
                Toast.makeText(context, task.exception?.message ?: "Login failed", Toast.LENGTH_LONG).show()
            }
        }
}

