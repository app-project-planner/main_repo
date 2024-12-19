package com.example.mobile_pj.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_pj.repository.PlanRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException

class SharedViewModel(
    private val repository: PlanRepository = PlanRepository()
) : ViewModel() {

    // 오늘 목표 리스트 (UI에서 실시간으로 업데이트됨)
    val goals = mutableStateListOf<String>()

    // 성취율: 대시보드에서 "오늘 목표" 체크 비율에 따라 계산
    val achievementRate = mutableStateOf(0)

    // 학습률: Q&A 페이지에서 문제를 푼 결과에 따라 계산
    val learningRate = mutableStateOf(0)

    // 질문 내역을 저장할 리스트
    private val questionHistory = mutableListOf<String>()

    // 사용자 ID (Firebase Authentication 연동 시 실제 ID로 대체 필요)
    private val userId = "currentUserId"

    // 생성된 문제 리스트와 상태
    private var currentProblems = listOf<Pair<String, String>>() // 문제와 정답 리스트
    private var isAwaitingAnswer = false // 채점 대기 상태

    // GenerativeModel 초기화
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyBvbaFaCC-ELiY3R9V7GYLhrmStd5Z9zLo" // BuildConfig.API_KEY를 사용하는 것이 권장됨
    )

    /**
     * 오늘 목표 추가
     */
    fun addGoal(goal: String) {
        repository.savePlan(userId, goal) { success ->
            if (success) goals.add(goal) // Firebase 저장 성공 시 로컬 상태 업데이트
        }
    }

    /**
     * 오늘 목표 삭제
     */
    fun removeGoal(goal: String) {
        repository.fetchPlans(userId) { plans ->
            val planId = plans.keys.find { plans[it] == goal }
            if (planId != null) {
                repository.deletePlan(userId, planId) { success ->
                    if (success) goals.remove(goal)
                }
            }
        }
    }

    /**
     * 오늘 목표 로드
     */
    fun loadGoals() {
        repository.fetchPlans(userId) { plans ->
            goals.clear()
            goals.addAll(plans.values)
        }
    }

    /**
     * AI에게 질문을 전달하고 응답을 가져옵니다.
     */
    fun askAI(question: String, onResponse: (String) -> Unit) {
        questionHistory.add(question) // 질문 기록 저장
        viewModelScope.launch {
            try {
                if (isAwaitingAnswer) {
                    // 답변을 채점
                    val userAnswers = question.split(".").map { it.trim() }
                    checkAnswers(userAnswers, currentProblems) { correct, incorrect ->
                        val total = correct + incorrect
                        val resultMessage = "채점 결과: 맞은 개수 = $correct, 틀린 개수 = $incorrect"
                        learningRate.value = if (total > 0) (correct * 100) / total else 0 // 학습률 업데이트

                        launch(Dispatchers.Main) {
                            onResponse(resultMessage)
                        }
                        isAwaitingAnswer = false
                    }
                } else if (question == "문제") {
                    // 문제 생성
                    val prompt = buildProblemPrompt()
                    val response = withContext(Dispatchers.IO) {
                        generativeModel.generateContent(prompt)
                    }
                    currentProblems = parseProblemsWithAnswers(response.text ?: "") // 문제 저장
                    isAwaitingAnswer = true // 채점 대기 상태로 설정

                    val problemsText = currentProblems.joinToString("\n") { (q, _) -> "문제: $q" }
                    onResponse("문제가 생성되었습니다: ${currentProblems.size}개\n$problemsText")
                } else {
                    // 일반 질문 처리
                    val prompt = buildPrompt(question)
                    val response = withContext(Dispatchers.IO) {
                        generativeModel.generateContent(prompt)
                    }
                    onResponse(response.text ?: "No response received")
                }
            } catch (e: Exception) {
                onResponse("Error: ${e.message}")
            }
        }
    }

    /**
     * 문제 채점 및 학습률 업데이트
     */
    suspend fun checkAnswers(
        userAnswers: List<String>,
        problems: List<Pair<String, String>>,
        onResult: (correct: Int, incorrect: Int) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            var correct = 0
            var incorrect = 0

            try {
                for ((index, userAnswer) in userAnswers.withIndex()) {
                    if (index < problems.size) {
                        val (question, answer) = problems[index]

                        val prompt = """
                        ## 문제:
                        $question

                        ## 사용자의 답변:
                        $userAnswer

                        ## 정답:
                        $answer

                        ## 위 정보를 기반으로 사용자의 답변이 정답인지 확인하고 "맞음" 또는 "틀림"으로 응답해줘.
                        """.trimIndent()

                        val response = generativeModel.generateContent(prompt)
                        val result = response.text?.trim() ?: ""

                        if (result == "맞음") {
                            correct++
                        } else {
                            incorrect++
                        }
                    } else {
                        incorrect++
                    }
                }
            } catch (e: Exception) {
                Log.e("checkAnswers", "Error during answer checking: ${e.message}", e)
                incorrect += problems.size - correct
            }

            withContext(Dispatchers.Main) {
                onResult(correct, incorrect)
            }
        }
    }

    /**
     * 질문에 따른 프롬프트 생성
     */
    private fun buildPrompt(question: String): String {
        return """
            ## 오늘의 목표:
            ${goals.joinToString(", ")}

            ## 대화 내역:
            ${questionHistory.joinToString(", ")}

            ## 사용자 질문:
            $question
        """.trimIndent()
    }

    /**
     * 문제 생성용 프롬프트 생성
     */
    private fun buildProblemPrompt(): String {
        return """
            ## 오늘의 목표:
            ${goals.joinToString(", ")}

            ## 대화 내역:
            ${questionHistory.joinToString(", ")}

            ## 위 정보를 기반으로 5개의 문제를 JSON 형태로 만들어줘.
        """.trimIndent()
    }

    /**
     * 문제와 정답을 포함한 JSON 파싱
     */
    private fun parseProblemsWithAnswers(json: String): List<Pair<String, String>> {
        val problems = mutableListOf<Pair<String, String>>()

        try {
            val cleanedJson = json.replace("```json", "").replace("```", "").trim()
            val jsonArray = JSONArray(cleanedJson)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val question = jsonObject.getString("question")
                val additionalInfo = "[Type: ${jsonObject.optString("type", "unknown")}, Difficulty: ${jsonObject.optString("difficulty", "unknown")}]"
                problems.add(question to additionalInfo)
            }
        } catch (e: JSONException) {
            Log.e("SharedViewModel", "JSON parsing error: ${e.message}", e)
        }

        return problems
    }
}
