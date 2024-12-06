package com.example.mobile_pj.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.mobile_pj.data.repository.GeminiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.internal.NopCollector.emit

class GeminiViewModel(private val geminiRepository: GeminiRepository) : ViewModel() {

    fun generateStudyPlan(syllabus: String) = liveData(Dispatchers.IO) {
        try {
            val response = geminiRepository.generateStudyPlan(syllabus).execute()
            if (response.isSuccessful) {
                emit(response.body())
            } else {
                emit(null) // Error handling
            }
        } catch (e: Exception) {
            emit(null) // Exception handling
        }
    }
}
