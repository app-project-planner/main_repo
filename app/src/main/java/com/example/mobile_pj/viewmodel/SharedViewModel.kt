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
        Log.d("SharedViewModel", "addGoal called with goal: $goal")
        repository.savePlan(userId, goal) { success ->
            if (success) {
                goals.add(goal)
                Log.d("SharedViewModel", "Goal added successfully: $goal")
            } else {
                Log.e("SharedViewModel", "Failed to add goal: $goal")
            }
        }
    }

    /**
     * 오늘 목표 삭제
     */
    fun removeGoal(goal: String) {
        Log.d("SharedViewModel", "removeGoal called with goal: $goal")
        repository.fetchPlans(userId) { plans ->
            val planId = plans.keys.find { plans[it] == goal }
            if (planId != null) {
                repository.deletePlan(userId, planId) { success ->
                    if (success) {
                        goals.remove(goal)
                        Log.d("SharedViewModel", "Goal removed successfully: $goal")
                    } else {
                        Log.e("SharedViewModel", "Failed to remove goal: $goal")
                    }
                }
            } else {
                Log.e("SharedViewModel", "Goal not found in plans: $goal")
            }
        }
    }

    /**
     * 오늘 목표 로드
     */
    fun loadGoals() {
        Log.d("SharedViewModel", "loadGoals called")
        repository.fetchPlans(userId) { plans ->
            goals.clear()
            goals.addAll(plans.values)
            Log.d("SharedViewModel", "Goals loaded: ${goals.joinToString(", ")}")
        }
    }

    /**
     * AI에게 질문을 전달하고 응답을 가져옵니다.
     */
    fun askAI(question: String, onResponse: (String) -> Unit) {
        Log.d("SharedViewModel", "askAI called with question: $question")
        questionHistory.add(question)
        viewModelScope.launch {
            try {
                if (isAwaitingAnswer) {
                    Log.d("SharedViewModel", "Checking answers for question: $question")
                    val userAnswers = question.split(".").map { it.trim() }
                    checkAnswers(userAnswers, currentProblems) { correct, incorrect ->
                        val total = correct + incorrect
                        val resultMessage = "채점 결과: 맞은 개수 = $correct, 틀린 개수 = $incorrect"
                        learningRate.value = if (total > 0) (correct * 100) / total else 0

                        Log.d("SharedViewModel", "Answer check completed: $resultMessage")
                        onResponse(resultMessage)
                        isAwaitingAnswer = false
                    }
                } else if (question == "문제" || question == "problem") {
                    Log.d("SharedViewModel", "Generating problems for question: $question")
                    val prompt = buildProblemPrompt()
                    val response = withContext(Dispatchers.IO) {
                        generativeModel.generateContent(prompt)
                    }
                    currentProblems = parseProblemsWithAnswers(response.text ?: "")
                    isAwaitingAnswer = true

                    val problemsText = currentProblems.joinToString("\n") { (q, _) -> "문제: $q" }
                    Log.d("SharedViewModel", "Problems generated: ${currentProblems.size}개")
                    onResponse("문제가 생성되었습니다: ${currentProblems.size}개\n$problemsText")
                } else {
                    Log.d("SharedViewModel", "Handling general question: $question")
                    val prompt = buildPrompt(question)
                    val response = withContext(Dispatchers.IO) {
                        generativeModel.generateContent(prompt)
                    }
                    onResponse(response.text ?: "No response received")
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error in askAI: ${e.message}", e)
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
        Log.d("SharedViewModel", "checkAnswers called with userAnswers: ${userAnswers.joinToString(", ")}")
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
                        Log.d("SharedViewModel", "Answer check: question=$question, userAnswer=$userAnswer, result=$result")
                    } else {
                        incorrect++
                    }
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error in checkAnswers: ${e.message}", e)
                incorrect += problems.size - correct
            }

            withContext(Dispatchers.Main) {
                Log.d("SharedViewModel", "checkAnswers result: correct=$correct, incorrect=$incorrect")
                onResult(correct, incorrect)
            }
        }
    }

    /**
     * 질문에 따른 프롬프트 생성
     */
    private fun buildPrompt(question: String): String {
        Log.d("SharedViewModel", "buildPrompt called with question: $question")
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
        Log.d("SharedViewModel", "buildProblemPrompt called")
        return """
            ## 오늘의 목표:
            ${goals.joinToString(", ")}

            ## 대화 내역:
            ${questionHistory.joinToString(", ")}

            ## 위 정보를 기반으로 5개의 문제를 JSON 형태로 만들어줘.
            {
                                                                                                        "question": "B+ 트리에서 노드의 차수가 삽입 및 삭제 연산의 성능에 미치는 영향을 설명하고, 적절한 차수를 선택하는 것이 중요한 이유는 무엇입니까?",
                                                                                                        "answer": "B+ 트리에서 노드의 차수는 트리의 높이에 직접적인 영향을 미칩니다. 차수가 클수록 한 노드에 더 많은 키 값을 저장할 수 있으므로 트리의 높이가 낮아지고, 검색, 삽입, 삭제 연산 시 디스크 접근 횟수가 줄어들어 성능이 향상됩니다. 반대로 차수가 작으면 트리의 높이가 높아져 성능이 저하됩니다.  적절한 차수 선택은 디스크 I/O 횟수를 최소화하여 전체 성능을 최적화하기 위해 중요합니다.  차수가 너무 크면 메모리 낭비가 발생할 수 있고, 너무 작으면 트리의 높이가 커져 성능이 저하될 수 있으므로,  데이터의 크기와 특성을 고려하여 적절한 차수를 선택해야 합니다."
                                                                                                      },
                                                                                                      {
                                                                                                        "question": "B+ 트리 삽입 연산에서 노드가 가득 차면 어떤 과정을 거치는지 설명해주세요.",
                                                                                                        "answer": "B+ 트리 삽입 연산에서 노드가 가득 차면, 노드를 분할하는 과정을 거칩니다.  가득 찬 노드의 중간 키 값을 상위 노드로 올리고, 원래 노드를 두 개의 노드로 분할합니다.  분할된 두 노드는 각각 중간 키 값보다 작은 키 값들과 큰 키 값들을 저장합니다.  만약 상위 노드도 가득 차 있다면, 이 과정을 재귀적으로 반복하여 트리의 높이를 증가시킵니다."
                                                                                                      },
                                                                                                      {
                                                                                                        "question": "B+ 트리 삭제 연산에서 노드가 과소하게 차 있을 때(underflow) 어떤 과정을 거치는지 설명해주세요.",
                                                                                                        "answer": "B+ 트리 삭제 연산에서 노드가 과소하게 차 있을 때(underflow),  해당 노드의 형제 노드로부터 키 값을 빌리거나, 해당 노드와 형제 노드를 병합하는 과정을 거칩니다.  형제 노드로부터 키 값을 빌릴 수 있다면, 상위 노드의 키 값과 함께 조정하여 underflow를 해결합니다.  형제 노드로부터 키 값을 빌릴 수 없다면, 해당 노드와 형제 노드를 병합하고, 상위 노드의 키 값을 조정합니다. 만약 상위 노드도 과소하게 차 있다면, 이 과정을 재귀적으로 반복합니다."
                                                                                                      },
                                                                                                      {
                                                                                                        "question": "B+ 트리의 삽입과 삭제 연산에서 노드 분할 및 병합의 목적은 무엇이며, 이 과정이 트리의 균형을 유지하는 데 어떻게 기여합니까?",
                                                                                                        "answer": "노드 분할과 병합의 목적은 B+ 트리의 균형을 유지하고, 모든 노드가 최소한의 키 값을 유지하도록 하는 것입니다.  삽입 시 노드가 가득 차면 분할을 통해 트리의 높이를 조절하고, 삭제 시 노드가 과소하게 차면 병합 또는 키 값 이동을 통해 트리의 균형을 유지합니다. 이러한 과정을 통해 모든 검색, 삽입, 삭제 연산의 시간 복잡도를 O(log n)으로 유지할 수 있습니다."
                                                                                                      },
                                                                                                      {
                                                                                                        "question": "B+ 트리와 일반적인 이진 검색 트리의 차이점을 설명하고, B+ 트리가 데이터베이스 인덱싱에 적합한 이유를 설명해주세요.",
                                                                                                        "answer": "B+ 트리는 이진 검색 트리와 달리, 모든 데이터를 리프 노드에 저장하고, 리프 노드들은 연결 리스트처럼 연결되어 있습니다.  B+ 트리는 높이가 낮고, 노드에 많은 키 값을 저장할 수 있기 때문에 디스크 I/O 횟수를 최소화할 수 있습니다. 이진 검색 트리는 키 값이 불균형하게 분포될 경우 높이가 높아져 성능이 저하될 수 있지만, B+ 트리는 노드 분할과 병합을 통해 균형을 유지하므로 성능 저하를 최소화합니다. 따라서 B+ 트리는 디스크 I/O가 중요한 데이터베이스 인덱싱에 적합합니다."
                                                                                                      }
                                                                                                      이런 형태로 만들면 돼.
        """.trimIndent()
    }

    /**
     * 문제와 정답을 포함한 JSON 파싱
     */
    private fun parseProblemsWithAnswers(json: String): List<Pair<String, String>> {
        Log.d("SharedViewModel", "parseProblemsWithAnswers called with JSON: $json")
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
            Log.d("SharedViewModel", "Parsed problems: ${problems.size} items")
        } catch (e: JSONException) {
            Log.e("SharedViewModel", "Error parsing JSON: ${e.message}", e)
        }

        return problems
    }
}