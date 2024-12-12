package com.example.mobile_pj.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_pj.viewmodel.SharedViewModel

/**
 * PlanListScreen: 추가된 계획을 보여주는 화면
 * @param viewModel SharedViewModel: 계획 데이터를 관리하는 ViewModel
 */

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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Goals",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading.value) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else if (goals.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "No goals icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No goals found. Add new goals!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 여유 공간 채우기
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goals) { goal ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(6.dp)
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            IconButton(onClick = { viewModel.removeGoal(goal) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Goal",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to Dashboard 버튼 추가
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp) // 여백 추가
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Back to Dashboard",
                color = MaterialTheme.colorScheme.onPrimary,
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

    // PlanListScreen 호출
    PlanListScreen(
        viewModel = dummyViewModel,
        onBackClick = { println("Navigate back to Dashboard") }
    )
}
