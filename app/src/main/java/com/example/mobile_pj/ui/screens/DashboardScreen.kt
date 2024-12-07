package com.example.mobile_pj.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_pj.viewmodel.SharedViewModel

@Composable
fun DashboardScreen(
    viewModel: SharedViewModel, // ViewModel 연결
    onQAClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    var newGoal by remember { mutableStateOf("") } // 입력 필드 상태 관리

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5FFF5))
            .padding(16.dp)
    ) {
        // 제목
        Text(
            text = "Loop Learn",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
            color = Color(0xFF6BAE75),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 사용자 정보 카드
        UserInfoCard()

        Spacer(modifier = Modifier.height(24.dp))

        // 오늘 목표 추가 및 리스트
        TodayGoalsCard(
            viewModel = viewModel,
            newGoal = newGoal,
            onNewGoalChange = { newGoal = it },
            onAddGoal = {
                if (newGoal.isNotBlank()) {
                    viewModel.addGoal(newGoal)
                    newGoal = "" // 입력 필드 초기화
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 버튼 영역
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
                text = "Welcome: User",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = Color(0xFF6BAE75)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Attendance: 12 days",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TodayGoalsCard(
    viewModel: SharedViewModel,
    newGoal: String,
    onNewGoalChange: (String) -> Unit,
    onAddGoal: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFAF0)), // 연한 녹색 배경
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 제목
            Text(
                text = "Today's Goals",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = Color(0xFF6BAE75),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 입력 필드 및 추가 버튼
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                TextField(
                    value = newGoal,
                    onValueChange = onNewGoalChange,
                    placeholder = { Text(text = "Add a new goal") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color(0xFFF0FFF0)
                    )
                )
                Button(onClick = onAddGoal, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6BAE75))) {
                    Text("Add", color = Color.White)
                }
            }

            // 목표 리스트
            LazyColumn {
                items(viewModel.goals) { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = goal,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.removeGoal(goal) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Goal", tint = Color.Red)
                        }
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

        Button(
            onClick = onStatisticsClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6BAE75))
        ) {
            Text(
                "Statistics",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboardScreen() {
    val dummyViewModel = SharedViewModel().apply {
        addGoal("Study Math")
        addGoal("Complete Assignment")
    }
    DashboardScreen(
        viewModel = dummyViewModel,
        onQAClick = { println("Navigate to QA") },
        onStatisticsClick = { println("Navigate to Statistics") }
    )
}









