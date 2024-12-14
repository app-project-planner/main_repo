package com.example.mobile_pj.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mobile_pj.repository.PlanRepository

/**
 * ViewModel: UI와 Repository(Firebase) 간의 데이터 처리 및 상태 관리를 담당
 */
class SharedViewModel(private val repository: PlanRepository = PlanRepository()) : ViewModel() {

    // 오늘 목표 리스트 (UI에서 실시간으로 업데이트됨)
    val goals = mutableStateListOf<String>()

    // 성취율: 대시보드에서 "오늘 목표" 체크 비율에 따라 계산
    val achievementRate = mutableStateOf(0)

    // 학습률: Q&A 페이지에서 문제를 푼 결과에 따라 계산
    val learningRate = mutableStateOf(0)

    // 사용자 ID (Firebase Authentication 연동 시 실제 ID로 대체 필요)
    private val userId = "currentUserId" // TODO: Firebase Auth에서 실제 사용자 ID 가져오기

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
        // Firebase에 저장된 goal을 찾아 해당 planId를 사용
        repository.fetchPlans(userId) { plans ->
            // plans에서 해당 goal과 매칭되는 planId 찾기
            val planId = plans.keys.find { plans[it] == goal }
            if (planId != null) {
                // planId를 기반으로 Firebase에서 삭제
                repository.deletePlan(userId, planId) { success ->
                    if (success) {
                        goals.remove(goal) // 로컬 상태에서 goal 제거
                    }
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
            goals.clear() // 기존 목표 초기화
            goals.addAll(plans.values) // Firebase에서 가져온 goal만 추가
        }
    }


    /**
     * ChatGPT를 통한 질문 처리 (AI 학습 지원 기능)
     * @param question 사용자 질문 내용
     * @param onResponse API 응답 콜백
     */
    fun askAI(question: String, onResponse: (String) -> Unit) {
            // TODO: ChatGPT API 호출 로직 추가
            onResponse("더미 응답") // 현재는 더미 데이터로 반환
        }

            /**
             * ChatGPT를 통한 문제 생성 처리
             * @param request 문제 생성 요청 내용
             * @param onGenerated 문제 리스트 콜백
             */
            fun generateProblems(request: String, onGenerated: (List<String>) -> Unit) {
                // TODO: ChatGPT API 호출 로직 추가
                onGenerated(listOf("문제 1", "문제 2", "문제 3")) // 현재는 더미 데이터로 반환
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
}

