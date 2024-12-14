package com.example.mobile_pj.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_pj.viewmodel.SharedViewModel
import com.example.mobile_pj.ui.theme.AppTheme

@Composable
fun PlanListScreen(
    viewModel: SharedViewModel,
    onBackClick: () -> Unit
) {
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadGoals()
        isLoading.value = false
    }

    val goals = viewModel.goals

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // 메인 화면과 동일한 배경색
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 타이틀
        Text(
            text = "Your Goals",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 28.sp, // 메인 화면과 동일한 크기
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary // MaterialTheme 색상 적용
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 로딩 상태 표시
        if (isLoading.value) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) // MaterialTheme 색상
        } else if (goals.isEmpty()) {
            // 목표가 없을 때
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "No goals icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, // 서브 색상
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No goals found. Add new goals!",
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface // 기본 텍스트 색상
                    )
                )
            }
        } else {
            // 목표 리스트
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goals) { goal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium), // MaterialTheme 모양
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface // 카드 배경색
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface // 텍스트 색상
                                )
                            )
                            IconButton(onClick = { viewModel.removeGoal(goal) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Goal",
                                    tint = MaterialTheme.colorScheme.error // 오류(삭제) 색상
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to Dashboard 버튼
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(MaterialTheme.shapes.medium), // MaterialTheme 모양
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Back to Dashboard",
                color = MaterialTheme.colorScheme.onPrimary, // 버튼 텍스트 색상
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPlanListScreen() {
    // SharedViewModel의 더미 데이터를 생성
    val dummyViewModel = SharedViewModel().apply {
        addGoal("Complete Math Assignment")
        addGoal("Study for Physics Exam")
        addGoal("Read 10 pages of a book")
    }

    AppTheme {
        PlanListScreen(
            viewModel = dummyViewModel,
            onBackClick = { println("Navigate back to Dashboard") }
        )
    }
}
