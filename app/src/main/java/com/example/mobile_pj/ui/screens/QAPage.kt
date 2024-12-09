package com.example.mobile_pj.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_pj.viewmodel.SharedViewModel

/**
 * Q&A 화면: 사용자 입력과 AI 질문-답변 기록을 표시하는 화면
 * @param viewModel SharedViewModel: 상태 관리를 담당
 */
@Composable
fun QAPage(viewModel: SharedViewModel) {
    // 사용자 입력 상태 관리
    var userInput by remember { mutableStateOf("") }

    // 질문-답변 기록 가져오기
    val chatHistory = viewModel.goals // ViewModel의 goals를 사용하여 기록 관리

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5FFF5)) // 연한 초록 배경
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 화면 제목
            Text(
                text = "Loop Learn Q&A",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF6BAE75),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 질문-답변 기록 리스트
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(chatHistory) { message ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = if (chatHistory.indexOf(message) % 2 == 0) Arrangement.Start else Arrangement.End
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

            // 입력 필드 및 전송 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text(text = "Ask something...") },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(16.dp)),
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
                        // ViewModel에 새로운 질문 추가
                        viewModel.addGoal(userInput)
                        userInput = "" // 입력 필드 초기화
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color(0xFF6BAE75)
                    )
                }
            }
        }
    }
}

/**
 * QAPage 미리보기: 더미 데이터를 사용하여 UI를 테스트
 */
@Preview(showBackground = true)
@Composable
fun PreviewQAPage() {
    val dummyViewModel = SharedViewModel().apply {
        addGoal("Sample Q&A 1")
        addGoal("Sample Q&A 2")
    }
    QAPage(viewModel = dummyViewModel)
}

