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
import com.google.firebase.auth.FirebaseAuth

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
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "defaultUserId"

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
     * 사용자가 질문을 하면, 목표 및 대화 내역을 바탕으로 AI의 응답을 받아옴
     * @param question 사용자 질문 내용
     * @param onResponse API 응답 콜백
     */
    fun askAI(question: String, onResponse: (String) -> Unit) {
        questionHistory.add(question) // 질문 기록 저장
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 질문이 "문제"인지 확인
                if (question == "문제") {
                    // 문제 생성 로직 호출
                    val prompt = """
                    ## 오늘의 목표:
                    ${goals.joinToString(", ")}

                    ## 대화 내역:
                    ${questionHistory.joinToString(", ")}
                    
                    ## 위 정보를 기반으로 5개의 문제를 JSON 형태로 만들어줘.
                    """.trimIndent()

                    val response = generateContent(prompt)
                    val problems = response.getOrNull()?.let { parseProblemsWithAnswers(it) } ?: emptyList()

                    withContext(Dispatchers.Main) {
                        onResponse(problems.joinToString("\n"))
                    }
                } else {
                    // 일반적인 질문 처리
                    val prompt = """
                    ## 오늘의 목표:
                    ${goals.joinToString(", ")}

                    ## 대화 내역:
                    ${questionHistory.joinToString(", ")}

                    ## 사용자 질문:
                    $question
                    """.trimIndent()

                    val response = generateContent(prompt)
                    withContext(Dispatchers.Main) {
                        onResponse(response.getOrNull() ?: "응답을 가져올 수 없습니다.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResponse("오류 발생: ${e.message}")
                }
            }
        }
    }

    /**
     * 문제와 정답을 포함한 JSON 파싱
     * @param json Gemini 응답 JSON
     * @return 문제와 정답 리스트
     */
    private fun parseProblemsWithAnswers(json: String): List<Pair<String, String>> {
        return try {
            val responseMap: Map<String, List<Map<String, String>>> = gson.fromJson(json, Map::class.java) as Map<String, List<Map<String, String>>>
            responseMap["problems"]?.map {
                val question = it["question"] ?: throw IllegalArgumentException("Question is missing")
                val answer = it["answer"] ?: throw IllegalArgumentException("Answer is missing")
                question to answer
            } ?: emptyList()
        } catch (e: JsonSyntaxException) {
            emptyList()
        } catch (e: IllegalArgumentException) {
            // 예외 처리 로직
            emptyList()
        }
    }

    /**
     * 문제 풀이 후 사용자가 제공한 답변을 AI에 전달하고, 정답 여부 확인
     * @param userAnswers 사용자가 입력한 답변 리스트
     * @param problems 문제와 정답 리스트
     * @param onResult 정답/틀린 개수를 반환하는 콜백
     */
    fun checkAnswers(
        userAnswers: List<String>, // 사용자가 입력한 답변 리스트
        problems: List<Pair<String, String>>, // 문제와 정답 쌍
        onResult: (correct: Int, incorrect: Int) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            var correct = 0
            var incorrect = 0

            try {
                for ((index, userAnswer) in userAnswers.withIndex()) {
                    val problem = problems.getOrNull(index)
                    if (problem != null) {
                        val (question, answer) = problem

                        // AI에게 문제와 사용자의 답변 전달
                        val prompt = """
                        ## 문제:
                        $question

                        ## 사용자의 답변:
                        $userAnswer

                        ## 정답:
                        $answer

                        ## 위 정보를 기반으로 사용자의 답변이 정답인지 확인하고 "맞음" 또는 "틀림"으로 응답해줘.
                        """.trimIndent()

                        val response = generateContent(prompt)
                        val result = response.getOrNull()?.trim() ?: ""

                        // 정답 여부 판단
                        if (result == "맞음") correct++ else incorrect++
                    }
                }
            } catch (e: Exception) {
                // 오류 발생 시 틀린 문제로 처리
                incorrect += userAnswers.size - correct
            }

            withContext(Dispatchers.Main) {
                // 학습률 업데이트
                updateLearningRate(correct, correct + incorrect)
                onResult(correct, incorrect)
            }
        }
    }

    /**
     * 학습률 업데이트
     * @param correct 맞춘 문제 개수
     * @param total 총 문제 개수
     */
    fun updateLearningRate(correct: Int, total: Int) {
        if (total > 0) {
            learningRate.value = (correct / total.toFloat() * 100).toInt()
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