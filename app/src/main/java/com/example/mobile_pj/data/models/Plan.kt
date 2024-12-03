package com.example.mobile_pj.data.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PlanData(
    val time: Long? = null,
    val title: String = "",
    val description: String = "",
    complete: Boolean = false
) {
    var complete by mutableStateOf(complete)
}