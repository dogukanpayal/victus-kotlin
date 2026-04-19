package com.dogukanpayal.victus_frontend.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(viewModel: HomeViewModel, token: String = "") {
    val uiState by viewModel.uiState.collectAsState()

    // Token geldiğinde veriyi yükle
    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadDailySummary(token)
        }
    }

    val primaryGreen = Color(0xFF22C55E)
    val lightGreenBg = Color(0xFFF0FDF4)
    val surfaceWhite = Color.White
    val borderGray = Color(0xFFF1F5F9)
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceWhite)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Daily Summary Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Günlük Özet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textDark
            )
            TextButton(onClick = { /* Navigate to details */ }) {
                Text(
                    text = if (uiState.dailyCalorieGoal == 0) "Hedef Belirle" else "Detayları Gör",
                    color = primaryGreen,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        if (uiState.dailyCalorieGoal == 0) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFDC2626)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aktif Diyet Planı Yok",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bugün için bir hedefin bulunmuyor. Diyet sayfasından yeni bir plan oluşturarak takip edebilirsin!",
                        fontSize = 14.sp,
                        color = Color(0xFF991B1B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Steps and Calories Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "ADIM",
                value = "8,432",
                trend = "+12%",
                icon = Icons.Default.Face, // Footsteps placeholder
                iconColor = primaryGreen,
                primaryGreen = primaryGreen
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "KALORİ",
                value = uiState.consumedCalories.toString(),
                trend = if (uiState.dailyCalorieGoal > 0) "%${(uiState.consumedCalories * 100 / uiState.dailyCalorieGoal)} hedef" else "Hedef yok",
                icon = Icons.Default.Star, // Fire placeholder
                iconColor = Color(0xFFF97316),
                primaryGreen = primaryGreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Water Consumption Card
        WaterConsumptionCard(
            value = "1.5 L",
            target = "2.5 L",
            primaryGreen = primaryGreen,
            lightGreenBg = Color(0xFFF0FDF4)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Daily Step Goal Card
        StepGoalCard(
            progress = 0.84f,
            remainingSteps = "1,568",
            primaryGreen = primaryGreen,
            lightGreenBg = lightGreenBg
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly Activity
        Text(
            text = "Haftalık Aktivite",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeeklyActivityChart(primaryGreen = primaryGreen)
        
        Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    trend: String,
    icon: ImageVector,
    iconColor: Color,
    primaryGreen: Color
) {
    Card(
        modifier = modifier
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Small trend arrow placeholder
                Icon(
                    imageVector = Icons.Default.Info, // Trend icon placeholder
                    contentDescription = null,
                    tint = primaryGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trend,
                    fontSize = 14.sp,
                    color = primaryGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun WaterConsumptionCard(
    value: String,
    target: String,
    primaryGreen: Color,
    lightGreenBg: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Call, // Water drop placeholder
                        contentDescription = null,
                        tint = Color(0xFF0EA5E9),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SU TÜKETİMİ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0EA5E9),
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value.split(" ")[0],
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = " L",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "/ $target",
                        fontSize = 16.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(lightGreenBg)
                    .clickable { /* Increase water */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Water",
                    tint = primaryGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun StepGoalCard(
    progress: Float,
    remainingSteps: String,
    primaryGreen: Color,
    lightGreenBg: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, lightGreenBg, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Günlük Adım Hedefi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Bitime $remainingSteps adım kaldı",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = primaryGreen,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(lightGreenBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star, // Sparkles placeholder
                        contentDescription = null,
                        tint = primaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HARİKA GİDİYORSUN!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyActivityChart(primaryGreen: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val barHeights = listOf(0.4f, 0.6f, 0.8f, 0.5f, 0.4f, 0.2f, 0.3f)
            barHeights.forEachIndexed { index, height ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(height)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (index == 2) primaryGreen else Color(0xFF64748B).copy(alpha = 0.2f))
                )
            }
        }
    }
}

