package com.example.mobile_pj.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val plans = mutableStateListOf<String>() // 질문-답변 기록 및 학습 계획
    val achievementRate = mutableStateOf(0) // 성취율
    val learningRate = mutableStateOf(0) // 학습률

    // 질문 추가
    fun addPlan(plan: String) {
        plans.add(plan)
    }

    // AI 질문 처리
    fun askAI(question: String, onResponse: (String) -> Unit) {
        // TODO: ChatGPT API 호출 로직 추가
        onResponse("더미 응답") // 현재는 더미 데이터
    }

    // 문제 생성 처리
    fun generateProblems(request: String, onGenerated: (List<String>) -> Unit) {
        // TODO: 문제 생성 API 호출 로직 추가
        onGenerated(listOf("문제 1", "문제 2", "문제 3")) // 현재는 더미 데이터
    }

    // 학습률 업데이트
    fun updateLearningRate(correctAnswers: Int, totalQuestions: Int) {
        if (totalQuestions > 0) {
            learningRate.value = (correctAnswers / totalQuestions.toFloat() * 100).toInt()
        }
    }

    // 성취율 업데이트
    fun updateAchievementRate(checkedGoals: Int, totalGoals: Int) {
        if (totalGoals > 0) {
            achievementRate.value = (checkedGoals / totalGoals.toFloat() * 100).toInt()
        }
    }
}
