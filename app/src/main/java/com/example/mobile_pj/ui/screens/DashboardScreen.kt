package com.example.mobile_pj.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview(showBackground = true)
@Composable
fun DashboardScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF5)) // 화면 배경색 설정
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 제목
            Text(
                text = "Loop Learn",
                style = MaterialTheme.typography.displayLarge,
                color = Color(0xFF8AAE92),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 사용자 정보 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF5EC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("welcome: name", style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Attendance: 12", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("today:", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = "",
                            onValueChange = {},
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFFFF5F5),
                                focusedContainerColor = Color(0xFFFFEDED),
                                unfocusedTextColor = Color.Gray,
                                focusedTextColor = Color.Black
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F5F5))
                                .height(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progression Rate 및 "오늘 목표" 섹션
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Progression Rate
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(180.dp) // 크기를 조금 늘림
                        .clip(CircleShape)
                        .background(Color(0xFFF2FFF5))
                        .padding(16.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val sweepAngle = 360 * 0.7f // 진행률 70%로 예제 설정
                        drawArc(
                            color = Color(0xFF8AAE92),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Icon",
                            tint = Color(0xFF8AAE92),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "progression rate",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF8AAE92)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 오늘 목표 체크리스트
                Column(
                    modifier = Modifier
                        .weight(1f) // 남은 공간 차지
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF2FFF5))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "오늘 목표",
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                        color = Color(0xFF8AAE92),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    repeat(3) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = index < 2,
                                onCheckedChange = {},
                                modifier = Modifier.size(24.dp),
                                colors = CheckboxDefaults.colors(
                                    checkmarkColor = Color(0xFF8AAE92),
                                    uncheckedColor = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "achievement ${index + 1}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Q&A 버튼
            Button(
                onClick = { /* Q&A 기능 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8AAE92))
            ) {
                Text(
                    "Q&A",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 하단 버튼 섹션
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Button(
                    onClick = { /* 알림 기능 */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .padding(horizontal = 8.dp)
                        .shadow(6.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8AAE92))
                ) {
                    Text("Notifications", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
                Button(
                    onClick = { /* 다른 기능 */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .padding(horizontal = 8.dp)
                        .shadow(6.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8AAE92))
                ) {
                    Text("Other Action", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}





