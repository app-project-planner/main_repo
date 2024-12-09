package com.example.mobile_pj.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

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
            color = Color(0xFF8AAE92), // Figma 색상
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF8AAE92),
                focusedBorderColor = Color(0xFF8AAE92),
                unfocusedBorderColor = Color(0xFFC4C4C4),
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("User Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF8AAE92),
                focusedBorderColor = Color(0xFF8AAE92),
                unfocusedBorderColor = Color(0xFFC4C4C4),
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF8AAE92),
                focusedBorderColor = Color(0xFF8AAE92),
                unfocusedBorderColor = Color(0xFFC4C4C4),
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF8AAE92),
                focusedBorderColor = Color(0xFF8AAE92),
                unfocusedBorderColor = Color(0xFFC4C4C4),
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up 버튼
        Button(
            onClick = { //signUp(auth,email,password,context,onRegisterClick)
                },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8AAE92)
            )
        ) {
            Text("Sign Up", color = Color.White)
        }
    }
}

// Preview 전용 함수
@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    SignUpScreen(
        onRegisterClick = {})
}
//fun signUp(
//    auth: FirebaseAuth,
//    email: String,
//    password: String,
//    context: android.content.Context,
//    onSignUpSuccess: () -> Unit
//) {
//    auth.createUserWithEmailAndPassword(email, password)
//        .addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                onSignUpSuccess()
//            } else {
//                if (!task.exception?.message.isNullOrEmpty()) {
//                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_LONG).show()
//                } else {
//                    signIn(auth, email, password, context, onSignUpSuccess)
//                }
//            }
//        }
//}
