@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.dogukanpayal.victus_frontend.ui.diet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info

import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import kotlin.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dogukanpayal.victus_frontend.data.model.FoodLogItem
import com.dogukanpayal.victus_frontend.data.model.MealType
import java.time.ZoneId
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietScreen(
    viewModel: DietViewModel,
    dietPlanViewModel: DietPlanViewModel,
    token: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    var mealToDelete by remember { mutableStateOf<FoodLogItem?>(null) }

    // Token geldiğinde veriyi yükle
    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadDailySummary(token)
            dietPlanViewModel.loadRemotePlan(token)
        }
    }

    val primaryGreen = Color(0xFF22C55E)
    val lightGreenBg = Color(0xFFF0FDF4)
    val surfaceWhite = Color.White
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)

    // Animate calorie progress
    val calorieProgress = if (uiState.dailyCalorieGoal > 0) {
        uiState.consumedCalories.toFloat() / uiState.dailyCalorieGoal.toFloat()
    } else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = calorieProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "calorieProgress"
    )

    // Negatif kalori kontrolü
    val isLimitExceeded = uiState.remainingCalories < 0
    val limitExceededColor = Color(0xFFDC2626)

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = {
            if (token.isNotEmpty()) {
                viewModel.loadDailySummary(token)
            }
        },
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // ═══════════════════════════════════════════
        // Circular Calorie Progress
        // ═══════════════════════════════════════════
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = surfaceWhite),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Günlük Kalori",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bugünkü beslenme özetin",
                    fontSize = 13.sp,
                    color = textGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Circular Progress
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 16.dp.toPx()
                        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                        // Background track
                        drawArc(
                            color = Color(0xFFF1F5F9),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Progress arc
                        drawArc(
                            color = Color(0xFF22C55E),
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Center text
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${uiState.consumedCalories}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textDark
                        )
                        Text(
                            text = "/ ${uiState.dailyCalorieGoal} kcal",
                            fontSize = 14.sp,
                            color = textGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Summary stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CalorieStat(
                        label = "HEDEF",
                        value = "${uiState.dailyCalorieGoal}",
                        color = primaryGreen
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0xFFE2E8F0))
                    )
                    CalorieStat(
                        label = "ALINAN",
                        value = "${uiState.consumedCalories}",
                        color = Color(0xFF3B82F6)
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0xFFE2E8F0))
                    )
                    CalorieStat(
                        label = "KALAN",
                        value = if (isLimitExceeded) "-${kotlin.math.abs(uiState.remainingCalories)}" else "${uiState.remainingCalories}",
                        color = if (isLimitExceeded) limitExceededColor else Color(0xFFF97316)
                    )
                }

                // Limit aşıldı uyarısı
                if (isLimitExceeded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 18.sp
                            )
                            Column {
                                Text(
                                    text = "Kalori Limiti Aşıldı",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = limitExceededColor
                                )
                                Text(
                                    text = "Günlük hedefini ${kotlin.math.abs(uiState.remainingCalories)} kalori kadar aştın",
                                    fontSize = 11.sp,
                                    color = Color(0xFF991B1B)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════════════════════════════════
        // Macros Section
        // ═══════════════════════════════════════════
        SectionHeader(title = "Makro Besinler", icon = Icons.AutoMirrored.Filled.List, iconColor = primaryGreen)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = surfaceWhite),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MacroProgressBar(
                    label = "Protein",
                    value = String.format(Locale.ROOT, "%.1f g", uiState.proteinConsumed),
                    target = String.format(Locale.ROOT, "%.1f g", uiState.proteinGoal),
                    progress = if (uiState.proteinGoal > 0) (uiState.proteinConsumed / uiState.proteinGoal).coerceIn(0f, 1f) else 0f,
                    color = primaryGreen
                )
                MacroProgressBar(
                    label = "Karbonhidrat",
                    value = String.format(Locale.ROOT, "%.1f g", uiState.carbsConsumed),
                    target = String.format(Locale.ROOT, "%.1f g", uiState.carbsGoal),
                    progress = if (uiState.carbsGoal > 0) (uiState.carbsConsumed / uiState.carbsGoal).coerceIn(0f, 1f) else 0f,
                    color = Color(0xFF3B82F6)
                )
                MacroProgressBar(
                    label = "Yağ",
                    value = String.format(Locale.ROOT, "%.1f g", uiState.fatConsumed),
                    target = String.format(Locale.ROOT, "%.1f g", uiState.fatGoal),
                    progress = if (uiState.fatGoal > 0) (uiState.fatConsumed / uiState.fatGoal).coerceIn(0f, 1f) else 0f,
                    color = Color(0xFFF97316)
                )
            }
        }



        // ═══════════════════════════════════════════
        // AI 7-Day Diet Plan Section
        // ═══════════════════════════════════════════
        DietPlanSection(
            viewModel = dietPlanViewModel,
            token = token,
            meals = uiState.meals,
            onParseDiet = { rawText ->
                val today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
                viewModel.parseAndSaveDiet(token, rawText, today)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════════════════════════════════
        // Visual Food Log Section
        // ═══════════════════════════════════════════
        SectionHeader(
            title = "Yemek Günlüğü",
            icon = Icons.Default.Restaurant,
            iconColor = Color(0xFF8B5CF6)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.foodLog.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📸", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Henüz fotoğraf kaydedilmedi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Yemek fotoğrafı çektiğinde burada görünür",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            FoodLogTimeline(
                items = uiState.foodLog,
                onDelete = { mealToDelete = it }
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
        }

        // ═══════════════════════════════════════════
        // Loading Overlay for Parsing
        // ═══════════════════════════════════════════
        if (uiState.isParsingDiet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = primaryGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Diyetin Çözümleniyor...",
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                        Text(
                            text = "Bu işlem birkaç saniye sürebilir.",
                            fontSize = 12.sp,
                            color = textGray
                        )
                    }
                }
            }
        }

        // ═══════════════════════════════════════════
        // Error Dialog
        // ═══════════════════════════════════════════
        uiState.dietParsingError?.let { error ->
            AlertDialog(
                onDismissRequest = { viewModel.clearDietParsingError() },
                title = { Text("Diyet Çözümlenemedi", fontWeight = FontWeight.Bold) },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearDietParsingError() }) {
                        Text("Tamam", color = primaryGreen, fontWeight = FontWeight.Bold)
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(32.dp)
                    )
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White
            )
        }

        // ═══════════════════════════════════════════
        // Delete Confirmation Dialog
        // ═══════════════════════════════════════════
        mealToDelete?.let { meal ->
            AlertDialog(
                onDismissRequest = { mealToDelete = null },
                title = { Text("Öğünü Sil", fontWeight = FontWeight.Bold) },
                text = { Text("'${meal.foodName}' günlüğünüzden silinecek. Emin misiniz?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (token.isNotEmpty()) {
                                viewModel.deleteMealLog(token, meal.id)
                            }
                            mealToDelete = null
                        }
                    ) {
                        Text("Sil", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mealToDelete = null }) {
                        Text("Vazgeç", color = textGray)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White
            )
        }
    }
}

// ═════════════════════════════════════════════════
// Helper Composables
// ═════════════════════════════════════════════════

@Composable
private fun CalorieStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color.copy(alpha = 0.7f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "kcal",
            fontSize = 10.sp,
            color = Color(0xFF94A3B8)
        )
    }
}

private fun getMealTypeVisuals(type: MealType): Triple<ImageVector, Color, Color> {
    return when (type) {
        MealType.BREAKFAST -> Triple(
            Icons.Default.Star,
            Color(0xFFFEF9C3),
            Color(0xFFEAB308)
        )
        MealType.LUNCH -> Triple(
            Icons.Default.Favorite,
            Color(0xFFDCFCE7),
            Color(0xFF22C55E)
        )
        MealType.DINNER -> Triple(
            Icons.Default.Notifications,
            Color(0xFFDBEAFE),
            Color(0xFF3B82F6)
        )
        MealType.SNACK -> Triple(
            Icons.Default.CheckCircle,
            Color(0xFFFFEDD5),
            Color(0xFFF97316)
        )
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector, iconColor: Color) {
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
fun MacroProgressBar(
    label: String,
    value: String,
    target: String,
    progress: Float,
    color: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "macroProgress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569)
            )
            Row {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = " / $target",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

@Composable
fun MealCard(
    title: String,
    description: String,
    calories: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = calories,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "KCAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════
// Visual Food Log Composables
// ═════════════════════════════════════════════════

private fun formatTimeLabel(isoTimestamp: String): String {
    return try {
        val odt = OffsetDateTime.parse(isoTimestamp)
        val istanbul = odt.atZoneSameInstant(ZoneId.of("Europe/Istanbul"))
        istanbul.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        "--:--"
    }
}

@Composable
fun FoodLogTimeline(items: List<FoodLogItem>, onDelete: (FoodLogItem) -> Unit) {
    // Kronolojik sıralama (en eski önce)
    val sorted = remember(items) { items.sortedBy { it.createdAt } }

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        sorted.forEachIndexed { index, item ->
            FoodLogCard(
                item = item,
                isLast = index == sorted.lastIndex,
                onDelete = { onDelete(item) }
            )
        }
    }
}

@Composable
private fun FoodLogCard(item: FoodLogItem, isLast: Boolean, onDelete: () -> Unit) {
    val timeLabel = remember(item.createdAt) { formatTimeLabel(item.createdAt) }
    val context = LocalPlatformContext.current

    Row(modifier = Modifier.fillMaxWidth()) {
        // ── Timeline sol sütun ───────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(52.dp)
        ) {
            // Zaman balonu
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE9FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = timeLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C3AED),
                    textAlign = TextAlign.Center
                )
            }
            // Bağlantı çizgisi (son eleman değilse)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(12.dp)
                        .background(Color(0xFFDDD6FE))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // ── Kart sağ içerik ──────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Yemek fotoğrafı / placeholder
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!item.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.foodName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Yemek bilgileri (Orta kısım - Geniş)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.foodName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        MacroChip(label = "P", value = String.format(Locale.ROOT, "%.0fg", item.protein), bgColor = Color(0xFFDCFCE7), textColor = Color(0xFF16A34A))
                        MacroChip(label = "K", value = String.format(Locale.ROOT, "%.0fg", item.carbs), bgColor = Color(0xFFDBEAFE), textColor = Color(0xFF2563EB))
                        MacroChip(label = "Y", value = String.format(Locale.ROOT, "%.0fg", item.fat), bgColor = Color(0xFFFFEDD5), textColor = Color(0xFFEA580C))
                    }
                }

                // Sağ sütun (Silme Butonu Üstte, Kalori Altta)
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .offset(x = 8.dp, y = (-12).dp) // Köşeye tam oturması için
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Sil",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Text(
                        text = "${item.calories}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "kcal",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroChip(label: String, value: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$label $value",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1,
            softWrap = false
        )
    }
}
