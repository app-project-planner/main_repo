package com.example.mobile_pj.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_pj.viewmodel.GeminiViewModel

@Composable
fun GeminiScreen(geminiViewModel: GeminiViewModel = viewModel()) {
    var syllabus by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<List<String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = syllabus,
            onValueChange = { syllabus = it },
            label = { Text("Enter Syllabus") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            geminiViewModel.generateStudyPlan(syllabus).observeForever { plans ->
                result = plans?.map { "Week ${it.week}: ${it.title}" }
            }
        }) {
            Text("Generate Study Plan")
        }

        Spacer(modifier = Modifier.height(16.dp))

        result?.let {
            it.forEach { plan -> Text(plan) }
        }
    }
}
