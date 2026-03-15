package com.dogukanpayal.victus_frontend.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel) {
    val primaryGreen = Color(0xFF22C55E)
    val lightGreenBg = Color(0xFFF0FDF4)
    val lightBlueBg = Color(0xFFEFF6FF)
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)

    var selectedCategory by remember { mutableStateOf("Hepsi") }
    val categories = listOf("Hepsi", "Yağ Yakımı", "Kas Kütlesi", "Esneklik")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(text = "Programını Seç", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textDark)
            Text(text = "Hedefine en uygun antrenmanı bul", fontSize = 14.sp, color = textGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory),
            containerColor = Color.White,
            contentColor = primaryGreen,
            edgePadding = 24.dp,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[categories.indexOf(selectedCategory)]),
                    color = primaryGreen
                )
            }
        ) {
            categories.forEach { category ->
                Tab(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    text = {
                        Text(
                            text = category,
                            fontSize = 14.sp,
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedCategory == category) primaryGreen else textGray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Small Highlight Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProgramTypeCard(
                modifier = Modifier.weight(1f),
                title = "Yağ Yakımı",
                containerColor = lightGreenBg,
                iconColor = primaryGreen,
                icon = Icons.Default.Favorite
            )
            ProgramTypeCard(
                modifier = Modifier.weight(1f),
                title = "Kas Kütlesi",
                containerColor = lightBlueBg,
                iconColor = Color(0xFF3B82F6),
                icon = Icons.Default.Build
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Popular Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Popüler Antrenmanlar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textDark)
            TextButton(onClick = { /* View All */ }) {
                Text(text = "Tümünü Gör", color = primaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Workout List
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            WorkoutLargeCard(
                title = "Full Body HIIT Patlaması",
                description = "Metabolizmanızı hızlandırın ve yağ yakın.",
                duration = "45 dk",
                calories = "420 kcal",
                level = "İleri Seviye",
                difficulty = "Zor",
                difficultyColor = Color(0xFFFEE2E2),
                difficultyTextColor = Color(0xFFEF4444),
                imagePlaceholderColor = Color(0xFF1E293B)
            )
            
            WorkoutLargeCard(
                title = "Sabah Esnemesi & Mobilite",
                description = "Güne taze ve esnek bir başlangıç yapın.",
                duration = "20 dk",
                calories = "120 kcal",
                level = "Başlangıç",
                difficulty = "Kolay",
                difficultyColor = Color(0xFFDCFCE7),
                difficultyTextColor = primaryGreen,
                imagePlaceholderColor = Color(0xFF134E4A)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun ProgramTypeCard(
    modifier: Modifier = Modifier,
    title: String,
    containerColor: Color,
    iconColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun WorkoutLargeCard(
    title: String,
    description: String,
    duration: String,
    calories: String,
    level: String,
    difficulty: String,
    difficultyColor: Color,
    difficultyTextColor: Color,
    imagePlaceholderColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Image Placeholder area with Difficulty Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(imagePlaceholderColor)
            ) {
                // Difficulty Badge
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(text = difficulty, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = difficultyTextColor)
                }
                
                // Overlay text or image would go here
                Text(
                    text = if (difficulty == "Zor") "🏋️‍♂️" else "🧘‍♀️",
                    fontSize = 64.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Info Content
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, fontSize = 14.sp, color = Color(0xFF64748B))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkoutStatInfo(icon = Icons.Default.Info, text = duration)
                    WorkoutStatInfo(icon = Icons.Default.Star, text = calories)
                    WorkoutStatInfo(icon = Icons.Default.Place, text = level)
                }
            }
        }
    }
}

@Composable
fun WorkoutStatInfo(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}
