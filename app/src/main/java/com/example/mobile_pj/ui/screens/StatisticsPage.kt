package com.example.mobile_pj.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_pj.R // 리소스 파일 추가

@Composable
fun StatisticsPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5FFF5)) // 연한 초록 배경
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 제목
            Text(
                text = "Statistics / Analysis",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
                color = Color(0xFF6BAE75), // 초록색 텍스트
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 진행률 및 도넛 차트
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 진행률 그래프
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F7FF)) // 연한 파란색 배경
                        .padding(8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val sweepAngle = 216f // 진행률 60%
                        drawArc(
                            color = Color(0xFF4A90E2), // 파란색 그래프
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "60%",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp),
                            color = Color(0xFF4A90E2)
                        )
                    }
                }

                // 도넛 차트 (예제)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDF7FF)) // 연한 분홍 배경
                        .padding(8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val sweepAngles = listOf(216f, 72f, 72f) // 60%, 20%, 20%
                        val colors = listOf(Color(0xFF4A90E2), Color(0xFF50C878), Color(0xFFFF6347))
                        var startAngle = -90f
                        sweepAngles.forEachIndexed { index, sweepAngle ->
                            drawArc(
                                color = colors[index],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Butt)
                            )
                            startAngle += sweepAngle
                        }
                    }
                }
            }

            // 계획 섹션
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFAF0)), // 연한 초록 배경
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Plan",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        color = Color(0xFF6BAE75),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    repeat(5) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = index < 3, // 처음 3개 체크됨
                                onCheckedChange = {},
                                modifier = Modifier.size(20.dp),
                                colors = CheckboxDefaults.colors(
                                    checkmarkColor = Color(0xFF6BAE75),
                                    uncheckedColor = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "achievement ${index + 1}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                painter = painterResource(R.drawable.bell_icon),
                                contentDescription = "Notification Icon",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStatisticsPage() {
    StatisticsPage()
}