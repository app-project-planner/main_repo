package com.example.mobile_pj.data.repository

import com.example.mobile_pj.ApiService
import com.example.mobile_pj.data.models.StudyPlanRequest
import com.example.mobile_pj.data.models.StudyPlanResponse
import retrofit2.Call

class GeminiRepository(private val apiService: ApiService) {

    fun generateStudyPlan(syllabus: String): Call<List<StudyPlanResponse>> {
        val request = StudyPlanRequest(syllabus)
        return apiService.generateStudyPlan(request)
    }
}