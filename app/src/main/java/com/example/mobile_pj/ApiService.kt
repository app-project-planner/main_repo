package com.example.mobile_pj

import com.example.mobile_pj.data.models.StudyPlanRequest
import com.example.mobile_pj.data.models.StudyPlanResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class UserCredentials(val username: String, val password: String)
data class RegisterRequest(val email: String, val username: String, val password: String)
data class AuthResponse(val token: String, val message: String)

interface ApiService {
    @POST("login")
    fun login(@Body credentials: UserCredentials): Call<AuthResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<AuthResponse>

    @POST("generate-study-plan")
    fun generateStudyPlan(@Body request: StudyPlanRequest): Call<List<StudyPlanResponse>>
}
