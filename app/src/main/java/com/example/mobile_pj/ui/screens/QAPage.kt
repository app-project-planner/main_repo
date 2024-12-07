package com.example.mobile_pj.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_pj.viewmodel.SharedViewModel

@Composable
fun QAPage(viewModel: SharedViewModel) {
    var userInput by remember { mutableStateOf("") } // 사용자 입력 상태 관리
    val chatHistory = remember { viewModel.plans } // 질문-답변 기록 (SharedViewModel 사용)

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
                text = "Loop Learn Q&A",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
                color = Color(0xFF6BAE75),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Q&A 메시지 리스트
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // 화면의 대부분 차지
                    .fillMaxWidth()
            ) {
                items(chatHistory) { message ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                            color = Color.Gray,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(12.dp)
                        )
                    }
                }
            }

            // 메시지 입력 및 전송 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text(text = "Ask something...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    if (userInput.isNotBlank()) {
                        if (userInput.contains("문제")) {
                            viewModel.generateProblems(userInput) { problems ->
                                chatHistory.addAll(problems)
                            }
                        } else {
                            viewModel.askAI(userInput) { answer ->
                                chatHistory.add("Q: $userInput")
                                chatHistory.add("A: $answer")
                            }
                        }
                        userInput = "" // 입력 초기화
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send Icon", tint = Color(0xFF6BAE75))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQAPage() {
    val dummyViewModel = SharedViewModel().apply {
        addPlan("Sample Q&A 1")
        addPlan("Sample Q&A 2")
    }
    QAPage(dummyViewModel)
}
