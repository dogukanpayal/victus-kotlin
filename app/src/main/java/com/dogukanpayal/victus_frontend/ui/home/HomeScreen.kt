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
import com.dogukanpayal.victus_frontend.data.model.FeedbackMessage

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ana Sayfa",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )
                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = primaryGreen,
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        if (uiState.dailyCalorieGoal == 0 && !uiState.isLoading) {
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

        // Calories Card
        SummaryCard(
            modifier = Modifier.fillMaxWidth(),
            title = "KALORİ",
            value = uiState.consumedCalories.toString(),
            trend = if (uiState.dailyCalorieGoal > 0) "%${(uiState.consumedCalories * 100 / uiState.dailyCalorieGoal)} hedef" else "Hedef yok",
            icon = Icons.Default.Star, // Fire placeholder
            iconColor = Color(0xFFF97316),
            primaryGreen = primaryGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Water Consumption Card
        WaterConsumptionCard(
            value = "${uiState.waterConsumedMl / 1000.0} L",
            target = "${uiState.waterTargetMl / 1000.0} L",
            primaryGreen = primaryGreen,
            lightGreenBg = Color(0xFFF0FDF4),
            isAdding = uiState.isAddingWater,
            onAddWater = { viewModel.addWater(token) }
        )

        if (uiState.feedbacks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            FeedbackSection(
                feedbacks = uiState.feedbacks,
                onFeedbackClick = { feedback ->
                    if (!feedback.isRead) {
                        viewModel.markFeedbackAsRead(token, feedback.id)
                    }
                }
            )
        }
        
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
    lightGreenBg: Color,
    isAdding: Boolean,
    onAddWater: () -> Unit
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
                    .clickable(enabled = !isAdding) { onAddWater() },
                contentAlignment = Alignment.Center
            ) {
                if (isAdding) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = primaryGreen,
                        strokeWidth = 2.dp
                    )
                } else {
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
}

@Composable
fun FeedbackSection(
    feedbacks: List<FeedbackMessage>,
    onFeedbackClick: (FeedbackMessage) -> Unit
) {
    Column {
        Text(
            text = "Gelen Kutusu",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(feedbacks.size) { index ->
                FeedbackCard(
                    feedback = feedbacks[index],
                    onClick = { onFeedbackClick(feedbacks[index]) }
                )
            }
        }
    }
}

@Composable
fun FeedbackCard(
    feedback: FeedbackMessage,
    onClick: () -> Unit
) {
    val isAI = feedback.senderType == "ai"
    val containerColor = if (isAI) Color(0xFFF0FDF4) else Color(0xFFF0F9FF)
    val contentColor = if (isAI) Color(0xFF166534) else Color(0xFF075985)
    val borderColor = if (isAI) Color(0xFFBBF7D0) else Color(0xFFBAE6FD)
    
    Card(
        modifier = Modifier
            .width(280.dp)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isAI) Icons.Default.Star else Icons.Default.Face,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAI) "YAPAY ZEKA" else "DİYETİSYEN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        letterSpacing = 1.sp
                    )
                }
                if (!feedback.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = feedback.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = feedback.message,
                fontSize = 14.sp,
                color = Color(0xFF475569),
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = feedback.createdAt.split("T")[0],
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
