package com.example.mobile_pj.ui.screens

import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobile_pj.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onSignUpClick: () -> Unit,
                onLoginClick: () -> Unit,
                onGoogleClick: ()-> Unit) {
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

        // Username 입력 필드
        var username by remember { mutableStateOf("") }
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("username") },
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
            onClick = { onLoginClick() },
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
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 소셜 로그인 아이콘 (예시)

        }

        Spacer(modifier = Modifier.height(32.dp))

        // 회원가입 버튼
        TextButton(onClick = { onSignUpClick() }) {
            Text("Sign up", color = Color(0xFF8AAE92))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        onSignUpClick = {}, // 기본값 설정
        onLoginClick = {},   // 기본값 설정
        onGoogleClick = {}
    )
}

@Composable
fun ImageButton(
    icon: Painter,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        content = {
            Image(
                painter = icon,
                contentDescription = "Google",
                modifier = Modifier.size(40.dp)
            )
        }
    )
}
