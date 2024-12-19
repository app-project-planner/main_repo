package com.example.mobile_pj.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mobile_pj.repository.PlanRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
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
    //private val userId: String
        //get() = FirebaseAuth.getInstance().currentUser?.uid ?: "defaultUserId"

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
     * AI에게 질문을 전달하고 응답을 가져옵니다.
     * @param question 사용자 질문 내용
     * @param onResponse API 응답 콜백
     */
    fun askAI(question: String, onResponse: (String) -> Unit) {
        questionHistory.add(question) // 질문 기록 저장
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isAwaitingAnswer) {
                    // 답변을 채점
                    val userAnswers = listOf(question) // 단일 답변으로 처리
                    checkAnswers(userAnswers, currentProblems) { correct, incorrect ->
                        val total = correct + incorrect
                        val resultMessage = "채점 결과: 맞은 개수 = $correct, 틀린 개수 = $incorrect"
                        learningRate.value = if (total > 0) (correct * 100) / total else 0 // 학습률 업데이트

                        // MainDispatcher로 결과 반환
                        launch(Dispatchers.Main) {
                            onResponse(resultMessage)
                        }
                        isAwaitingAnswer = false
                    }
                } else if (question == "문제") {
                    // 문제 생성
                    val prompt = buildProblemPrompt()
                    val response = generativeModel.generateContent(prompt)
                    currentProblems = parseProblemsWithAnswers(response.text ?: "") // 문제 저장
                    isAwaitingAnswer = true // 채점 대기 상태로 설정

                    launch(Dispatchers.Main) {
                        val problemsText = currentProblems.joinToString("\n") { (q, _) -> "문제: $q" }
                        onResponse("문제가 생성되었습니다: ${currentProblems.size}개\n$problemsText")
                    }
                } else {
                    // 일반 질문 처리
                    val prompt = buildPrompt(question)
                    val response = generativeModel.generateContent(prompt)
                    launch(Dispatchers.Main) {
                        onResponse(response.text ?: "No response received")
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    onResponse("Error: ${e.message}")
                }
            }
        }
    }




    /**
     * 문제 채점 및 학습률 업데이트
     * @param userAnswers 사용자가 입력한 답변 리스트
     * @param problems 생성된 문제 리스트
     * @param onResult 채점 결과 콜백
     */
    fun checkAnswers(
        userAnswers: List<String>,
        problems: List<Pair<String, String>>,
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

                        if (result == "맞음") correct++ else incorrect++
                    }
                }
            } catch (e: Exception) {
                incorrect += userAnswers.size - correct
            }

            // 학습률 업데이트
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
     * @param json AI 응답 JSON
     * @return 문제와 정답 리스트
     */
    private fun parseProblemsWithAnswers(json: String): List<Pair<String, String>> {
        val problems = mutableListOf<Pair<String, String>>()

        try {
            // 불필요한 텍스트 제거: "```json" 또는 "```"와 같은 구문 제거
            val cleanedJson = json
                .replace("```json", "") // "```json" 제거
                .replace("```", "") // 남아있는 "```" 제거
                .trim() // 공백 제거

            Log.d("SharedViewModel", "Cleaned JSON: $cleanedJson") // 전처리된 JSON 확인

            // JSON 배열로 변환
            val jsonArray = JSONArray(cleanedJson)

            // JSON 배열에서 문제와 정답 추출
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val question = jsonObject.getString("question")
                val additionalInfo = """
                [Type: ${jsonObject.optString("type", "unknown")}, 
                 Difficulty: ${jsonObject.optString("difficulty", "unknown")}]
            """.trimIndent()
                problems.add(question to additionalInfo)
            }

            Log.d("SharedViewModel", "Parsed problems: $problems")
        } catch (e: JSONException) {
            Log.e("SharedViewModel", "JSON parsing error: ${e.message}", e)
        }

        return problems
    }
}
