package com.example.mobile_pj

data class Plan(
    val title: String,
    val duration: String,
    val phases: List<Phase>
)

data class Phase(
    val title: String,
    val duration: String,
    val topics: List<String>
)