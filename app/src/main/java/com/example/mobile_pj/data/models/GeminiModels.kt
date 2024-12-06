package com.example.mobile_pj.data.models

data class StudyPlanRequest(val syllabus: String)
data class StudyPlanResponse(
    val week: Int,
    val title: String,
    val description: String
)
