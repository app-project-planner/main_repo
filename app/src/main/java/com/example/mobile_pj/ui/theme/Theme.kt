package com.example.mobile_pj.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Theme Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6BAE75), // 메인 화면의 주 색상 (녹색)
    secondary = Color(0xFF8AAE92), // 보조 색상 (버튼 등에서 사용)
    background = Color(0xFFF5FFF5), // 메인 화면 배경색
    surface = Color(0xFFEFFAF0), // 카드 및 입력 필드 배경색
    onPrimary = Color.White, // primary 버튼 텍스트 색상
    onSecondary = Color.White, // secondary 버튼 텍스트 색상
    onBackground = Color.Black, // 기본 텍스트 색상
    onSurface = Color(0xFF6BAE75), // 카드 내부 텍스트 색상
    error = Color.Red // 오류 색상 (삭제 버튼 등에서 사용)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(), // Material3 Typography 객체 사용
        content = content
    )
}


