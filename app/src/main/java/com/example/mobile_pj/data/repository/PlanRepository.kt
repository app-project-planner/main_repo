package com.example.mobile_pj.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

/**
 * Repository: Firebase와 통신하여 데이터 저장, 삭제, 조회를 담당
 */
class PlanRepository {

    // Firebase Realtime Database의 참조 경로
    private val databaseReference = FirebaseDatabase.getInstance().reference

    /**
     * 목표 저장
     * Firebase의 특정 사용자 경로에 목표 데이터를 저장
     * @param userId 사용자 ID
     * @param plan 저장할 목표 내용
     * @param onComplete 저장 성공 여부 콜백
     */
    fun savePlan(userId: String, plan: String, onComplete: (Boolean) -> Unit) {
        val planId = databaseReference.child("plans").push().key ?: return // Firebase에서 자동 생성된 고유 ID
        val planData = mapOf("id" to planId, "content" to plan)
        databaseReference.child("users").child(userId).child("plans").child(planId)
            .setValue(planData)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful) // 저장 성공 여부 반환
            }
    }

    /**
     * 목표 삭제
     * Firebase의 특정 사용자 경로에서 목표 데이터를 삭제
     * @param userId 사용자 ID
     * @param planId 삭제할 목표의 고유 ID
     * @param onComplete 삭제 성공 여부 콜백
     */
    fun deletePlan(userId: String, planId: String, onComplete: (Boolean) -> Unit) {
        databaseReference.child("users").child(userId).child("plans").child(planId)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseDelete", "Successfully deleted plan with ID: $planId")
                } else {
                    Log.e("FirebaseDelete", "Failed to delete plan with ID: $planId")
                }
                onComplete(task.isSuccessful) // 삭제 성공 여부 반환
            }
    }


    /**
     * 목표 조회
     * Firebase의 특정 사용자 경로에서 목표 데이터를 조회
     * @param userId 사용자 ID
     * @param onPlansFetched 목표 리스트 콜백
     */
    fun fetchPlans(userId: String, onPlansFetched: (Map<String, String>) -> Unit) {
        databaseReference.child("users").child(userId).child("plans")
            .get()
            .addOnSuccessListener { snapshot ->
                // Firebase 데이터에서 planId와 goal(content)를 매핑
                val plans = snapshot.children.associate { it.key!! to (it.child("content").value as? String ?: "") }
                Log.d("FirebaseFetch", "Fetched plans: $plans")
                onPlansFetched(plans)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseFetch", "Failed to fetch plans: ${exception.message}")
            }
    }

}
