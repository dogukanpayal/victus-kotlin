package com.dogukanpayal.victus_frontend.ui.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DietScreen(viewModel: DietViewModel) {
    val primaryGreen = Color(0xFF22C55E)
    val lightGreenBg = Color(0xFFF0FDF4)
    val surfaceWhite = Color.White
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Calorie Summary Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummarySmallCard(modifier = Modifier.weight(1f), title = "HEDEF", value = "2200", unit = "kcal", containerColor = Color(0xFFF0FDF4), contentColor = primaryGreen)
            SummarySmallCard(modifier = Modifier.weight(1f), title = "ALINAN", value = "1450", unit = "kcal", containerColor = primaryGreen, contentColor = Color.White)
            SummarySmallCard(modifier = Modifier.weight(1f), title = "KALAN", value = "750", unit = "kcal", containerColor = Color(0xFFF1F5F9), contentColor = textDark)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Macros Section
        SectionHeader(title = "Makro Besinler", icon = Icons.Default.List, iconColor = primaryGreen)
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = surfaceWhite),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                MacroProgressBar(label = "Protein", value = "95g", target = "146g", progress = 0.65f, color = primaryGreen)
                MacroProgressBar(label = "Karbonhidrat", value = "180g", target = "275g", progress = 0.65f, color = Color(0xFF3B82F6))
                MacroProgressBar(label = "Yağ", value = "42g", target = "73g", progress = 0.57f, color = Color(0xFFF97316))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Meal List Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Öğün Listesi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textDark)
            Row(
                modifier = Modifier.clickable { /* Add Meal */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = primaryGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Öğün Ekle", color = primaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Meals
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MealCard(title = "Kahvaltı", description = "Yulaf Ezmesi, Yumurta, Meyve", calories = "420", icon = Icons.Default.Star, iconBg = Color(0xFFFEF9C3), iconTint = Color(0xFFEAB308))
            MealCard(title = "Öğle Yemeği", description = "Izgara Tavuk, Kinoa Salata", calories = "580", icon = Icons.Default.Favorite, iconBg = Color(0xFFDCFCE7), iconTint = primaryGreen)
            MealCard(title = "Akşam Yemeği", description = "Somon Füme, Kuşkonmaz", calories = "350", icon = Icons.Default.Notifications, iconBg = Color(0xFFDBEAFE), iconTint = Color(0xFF3B82F6))
            MealCard(title = "Atıştırmalık", description = "Çiğ Badem, Yoğurt", calories = "100", icon = Icons.Default.CheckCircle, iconBg = Color(0xFFFFEDD5), iconTint = Color(0xFFF97316))
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SummarySmallCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = contentColor.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = contentColor)
            Text(text = unit, fontSize = 10.sp, color = contentColor.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector, iconColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    }
}

@Composable
fun MacroProgressBar(label: String, value: String, target: String, progress: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
            Row {
                Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(text = " / $target", fontSize = 12.sp, color = Color(0xFF94A3B8))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

@Composable
fun MealCard(title: String, description: String, calories: String, icon: ImageVector, iconBg: Color, iconTint: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text(text = description, fontSize = 12.sp, color = Color(0xFF64748B))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = calories, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                Text(text = "KCAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            }
        }
    }
}
