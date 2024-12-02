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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_pj.R
import com.google.firebase.auth.FirebaseAuth


@Composable
fun DashboardScreen(
    onQAClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onLogOutClick:()->Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5FFF5))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Optional padding
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "Loop Learn",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
                color = Color(0xFF6BAE75),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogOutClick()},
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8AAE92)
                )
            ) {
                Text("Log out", color = Color.White)
            }
        }

        UserInfoCard()
        Spacer(modifier = Modifier.height(24.dp))
        ProgressAndGoalsCard()
        Spacer(modifier = Modifier.height(24.dp))
        ActionButtons(onQAClick = onQAClick, onStatisticsClick = onStatisticsClick)
    }
}

@Composable
fun UserInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFAF0)), // 연한 녹색 배경
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "welcome: name",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = Color(0xFF6BAE75)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Attendance: 12",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "today:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = "",
                    onValueChange = {},
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color(0xFFF0FFF0),
                        unfocusedTextColor = Color.Gray,
                        focusedTextColor = Color.Black
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .height(40.dp)
                )
            }
        }
    }
}

@Composable
fun ProgressAndGoalsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFAF0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Progression Rate
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F7FF))
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val sweepAngle = 270f // 진행률 75%
                    drawArc(
                        color = Color(0xFF4A90E2),
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
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "progression rate",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFF4A90E2)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 오늘 목표 체크리스트
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEFFAF0))
                    .padding(12.dp)
            ) {
                Text(
                    text = "오늘 목표",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = Color(0xFF6BAE75),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                repeat(7) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = index < 3,
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
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    onQAClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    Column {
        Button(
            onClick = onQAClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6BAE75))
        ) {
            Text(
                "Q&A",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    .height(50.dp)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6BAE75))
            ) {
                Icon(
                    painter = painterResource(R.drawable.bell_icon),
                    contentDescription = "Bell Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Notifications", color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = onStatisticsClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6BAE75))
            ) {
                Icon(
                    painter = painterResource(R.drawable.chart_icon),
                    contentDescription = "Chart Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Statistics", color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewDashboardScreen() {
    DashboardScreen(
        onQAClick = { println("Navigate to QA") },
        onStatisticsClick = { println("Navigate to Statistics")},
        onLogOutClick = {println("Navigate to Login")}
    )
}






