package com.example.mobile_pj

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

class GeminiApiClient(private val apiKeyProvider: ApiKeyProvider) {

    private val client = OkHttpClient()
    private val gson = Gson()

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
            .addHeader("Authorization", "Bearer ${apiKeyProvider.getApiKey()}")
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

    suspend fun requestLearningPlan(syllabus: String, userComments: String): Result<Plan> = withContext(Dispatchers.IO) {
        val prompt = """
            ## 강의 계획서: 
            $syllabus

            ## 사용자 코멘트:
            $userComments

            ## 위 정보를 바탕으로 학습 계획을 JSON 형태로 생성해줘. 
            ## 다음과 같은 구조로:json { "title": "학습 계획 제목", "duration": "학습 기간", "phases": [ { "title": "단계 제목", "duration": "단계 기간", "topics": [ "주제1", "주제2", "주제3" ] } ] }
            """.trimIndent()

        try {
            val response = generateContent(prompt)
            if (response.isSuccess) {
                val json = response.getOrNull() ?: ""
                val learningPlan = gson.fromJson(json, Plan::class.java)
                Result.success(learningPlan)
            } else {
                Result.failure(response.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: JsonSyntaxException) {
            Result.failure(e)
        }
    }
}

interface ApiKeyProvider {
    fun getApiKey(): String
}