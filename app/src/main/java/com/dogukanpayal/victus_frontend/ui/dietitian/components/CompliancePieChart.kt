package com.dogukanpayal.victus_frontend.ui.dietitian.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogukanpayal.victus_frontend.data.model.ComplianceSummary

@Composable
fun CompliancePieChart(
    data: ComplianceSummary,
    modifier: Modifier = Modifier
) {
    val proteinColor = Color(0xFFEF4444) // Red
    val carbsColor = Color(0xFF3B82F6)   // Blue
    val fatColor = Color(0xFFF59E0B)     // Amber/Orange
    val backgroundColor = Color(0xFFF1F5F9)

    val totalMacros = data.actualProtein + data.actualCarbs + data.actualFat
    
    // Calculate angles
    val proteinAngle = if (totalMacros > 0) (data.actualProtein / totalMacros * 360f).toFloat() else 0f
    val carbsAngle = if (totalMacros > 0) (data.actualCarbs / totalMacros * 360f).toFloat() else 0f
    val fatAngle = if (totalMacros > 0) (data.actualFat / totalMacros * 360f).toFloat() else 0f

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Beslenme Uyumu",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donut Chart
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        val strokeWidth = 15.dp.toPx()
                        
                        // Background circle
                        drawCircle(
                            color = backgroundColor,
                            style = Stroke(width = strokeWidth)
                        )

                        if (totalMacros > 0) {
                            var startAngle = -90f

                            // Carbs
                            drawArc(
                                color = carbsColor,
                                startAngle = startAngle,
                                sweepAngle = carbsAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += carbsAngle

                            // Protein
                            drawArc(
                                color = proteinColor,
                                startAngle = startAngle,
                                sweepAngle = proteinAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += proteinAngle

                            // Fat
                            drawArc(
                                color = fatColor,
                                startAngle = startAngle,
                                sweepAngle = fatAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }
                    
                    // Center Text
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${data.actualCalories.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            "/ ${data.targetCalories.toInt()} kcal",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Legend
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    LegendItem(label = "Karbonhidrat", actual = data.actualCarbs, target = data.targetCarbs, color = carbsColor)
                    LegendItem(label = "Protein", actual = data.actualProtein, target = data.targetProtein, color = proteinColor)
                    LegendItem(label = "Yağ", actual = data.actualFat, target = data.targetFat, color = fatColor)
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, actual: Double, target: Double, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
            Text(
                "${actual.toInt()}g / ${target.toInt()}g",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }
    }
}
