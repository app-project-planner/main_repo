package com.example.mobile_pj.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_pj.viewmodel.SharedViewModel

@Composable
fun StatisticsPage(viewModel: SharedViewModel) {
    val achievementRate = viewModel.achievementRate.value // 성취도
    val learningRate = viewModel.learningRate.value // 학습률

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5FFF5)) // 연한 초록 배경
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 제목
            Text(
                text = "Statistics / Analysis",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
                color = Color(0xFF6BAE75),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 성취도 및 학습률 그래프
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 학습률 그래프
                CircularProgress(
                    label = "Learning",
                    rate = learningRate,
                    color = Color(0xFF50C878)
                )
            }
        }
    }
}

@Composable
fun CircularProgress(label: String, rate: Int, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(Color(0xFFE0F7FF))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepAngle = (rate / 100f) * 360f
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$rate%",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp),
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStatisticsPage() {
    val dummyViewModel = SharedViewModel().apply {
        achievementRate.value = 80
        learningRate.value = 60
    }
    StatisticsPage(dummyViewModel)
}
