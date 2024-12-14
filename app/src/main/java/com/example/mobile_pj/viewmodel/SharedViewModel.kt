package com.example.mobile_pj.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mobile_pj.repository.PlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException

/**
 * ViewModel: UI와 Repository(Firebase) 간의 데이터 처리 및 상태 관리를 담당
 */
class SharedViewModel(
    private val repository: PlanRepository = PlanRepository()
) : ViewModel() {

    // 오늘 목표 리스트 (UI에서 실시간으로 업데이트됨)
    val goals = mutableStateListOf<String>()

    // 성취율: 대시보드에서 "오늘 목표" 체크 비율에 따라 계산
    val achievementRate = mutableStateOf(0)

    // 학습률: Q&A 페이지에서 문제를 푼 결과에 따라 계산
    val learningRate = mutableStateOf(0)

    // 사용자 ID (Firebase Authentication 연동 시 실제 ID로 대체 필요)
    private val userId = "currentUserId" // TODO: Firebase Auth에서 실제 사용자 ID 가져오기

    // 질문 내역을 저장할 리스트
    private val questionHistory = mutableListOf<String>()

    // HTTP 클라이언트 및 JSON 파서를 초기화
    private val client = OkHttpClient()
    private val gson = Gson()

    /**
     * 오늘 목표 추가
     * Firebase에 목표를 저장하고, 저장 성공 시 로컬 상태(goals)를 업데이트
     * @param goal 추가할 목표 내용
     */
    fun addGoal(goal: String) {
        repository.savePlan(userId, goal) { success ->
            if (success) goals.add(goal) // Firebase 저장 성공 시 로컬 상태 업데이트
        }
    }

    /**
     * 오늘 목표 삭제
     * Firebase에서 목표를 삭제하고, 삭제 성공 시 로컬 상태(goals)에서 제거
     * @param goal 삭제할 목표 내용
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
     * Firebase에서 목표 리스트를 가져와 로컬 상태(goals)로 업데이트
     */
    fun loadGoals() {
        repository.fetchPlans(userId) { plans ->
            goals.clear()
            goals.addAll(plans.values)
        }
    }

    /**
     * Gemeni를 통한 질문 처리 (AI 학습 지원 기능)
     * @param question 사용자 질문 내용
     * @param onResponse API 응답 콜백
     */
    fun askAI(question: String, onResponse: (String) -> Unit) {
        questionHistory.add(question) // 질문 기록 저장
        CoroutineScope(Dispatchers.IO).launch {
            val prompt = question
            try {
                val response = generateContent(prompt)
                withContext(Dispatchers.Main) {
                    onResponse(response.getOrNull() ?: "응답을 가져올 수 없습니다.")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResponse("오류 발생: ${e.message}")
                }
            }
        }
    }

    /**
     * Gemeni를 통한 문제 생성 처리
     * @param request 문제 생성 요청 내용
     * @param onGenerated 문제 리스트 콜백
     */
    fun generateProblems(request: String, onGenerated: (List<String>) -> Unit) {
        if (request != "문제") {
            onGenerated(emptyList())
            return
        }

        // 목표 데이터를 로드하고 처리
        loadGoals()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // goals 리스트를 사용하여 프롬프트 생성
                val prompt = """
                ## 오늘의 목표:
                ${goals.joinToString()}

                ## 질문 내역:
                ${questionHistory.joinToString()} 

                ## 위 정보를 기반으로 5개의 문제를 JSON 형태로 생성해줘.
            """.trimIndent()

                // Gemini API 호출 및 문제 생성
                val response = generateContent(prompt)
                val problems = response.getOrNull()?.let { parseProblems(it) } ?: emptyList()

                withContext(Dispatchers.Main) {
                    onGenerated(problems)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onGenerated(emptyList())
                }
            }
        }
    }


    /**
     * 학습률 업데이트
     * @param correctAnswers 맞춘 문제 개수
     * @param totalQuestions 총 문제 개수
     */
    fun updateLearningRate(correctAnswers: Int, totalQuestions: Int) {
        if (totalQuestions > 0) {
            learningRate.value = (correctAnswers / totalQuestions.toFloat() * 100).toInt()
        }
    }

    /**
     * 성취율 업데이트
     * @param checkedGoals 체크된 목표 개수
     * @param totalGoals 총 목표 개수
     */
    fun updateAchievementRate(checkedGoals: Int, totalGoals: Int) {
        if (totalGoals > 0) {
            achievementRate.value = (checkedGoals / totalGoals.toFloat() * 100).toInt()
        }
    }

    /**
     * JSON 응답을 문제 리스트로 변환
     * @param json Gemini 응답 JSON
     * @return 문제 리스트
     */
    private fun parseProblems(json: String): List<String> {
        return try {
            val responseMap: Map<String, List<String>> = gson.fromJson(json, Map::class.java) as Map<String, List<String>>
            responseMap["problems"] ?: emptyList()
        } catch (e: JsonSyntaxException) {
            emptyList()
        }
    }

    /**
     * Gemini API 호출 로직
     * @param prompt API에 전달할 프롬프트
     * @return API 응답 또는 실패 결과
     */
    private suspend fun generateContent(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        val url = "https://api.generativeai.googleapis.com/v1beta3/models/gemini-1.5-flash:generateContent"
        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            """
            {
                "prompt": "$prompt"
            }
            """.trimIndent()
        )

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "Bearer AIzaSyBsVPEvgMVA_FTTTHD9BA2uD8MW0s_c9vo")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                Result.success(responseBody ?: "")
            } else {
                Result.failure(IOException("Error: ${response.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}