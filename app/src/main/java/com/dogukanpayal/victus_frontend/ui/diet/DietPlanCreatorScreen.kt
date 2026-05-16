package com.dogukanpayal.victus_frontend.ui.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogukanpayal.victus_frontend.data.model.DayFormState
import com.dogukanpayal.victus_frontend.data.model.MealFormEntry
import kotlinx.coroutines.launch

@Composable
fun dietTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedPlaceholderColor = Color.Gray,
    unfocusedPlaceholderColor = Color.Gray,
    focusedLabelColor = Color(0xFF7C3AED),
    unfocusedLabelColor = Color.Gray
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlanCreatorScreen(
    viewModel: DietPlanCreatorViewModel,
    token: String,
    onNavigateBack: () -> Unit,
    onPlanCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) {
            onPlanCreated()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetForm()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Beslenme Planı Oluştur", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Plan Bilgileri Kartı
            PlanInfoCard(
                title = uiState.planTitle,
                onTitleChange = { viewModel.updateTitle(it) },
                startDate = uiState.startDate,
                onDateChange = { viewModel.updateStartDate(it) },
                durationDays = uiState.durationDays,
                onDurationChange = { viewModel.updateDurationDays(it) }
            )

            // Gün Seçici
            DayTabSelector(
                days = uiState.days,
                selectedDayIndex = uiState.selectedDayIndex,
                onDaySelected = { viewModel.selectDay(it) }
            )

            // Seçili Günün Öğünleri
            val currentDay = uiState.days[uiState.selectedDayIndex]
            currentDay.meals.forEachIndexed { index, meal ->
                MealFormCard(
                    meal = meal,
                    isFirst = index == 0,
                    isLast = index == currentDay.meals.size - 1,
                    onUpdate = { updated -> viewModel.updateMeal(uiState.selectedDayIndex, meal.id, updated) },
                    onDelete = { viewModel.removeMeal(uiState.selectedDayIndex, meal.id) },
                    onMoveUp = { viewModel.moveMealUp(uiState.selectedDayIndex, meal.id) },
                    onMoveDown = { viewModel.moveMealDown(uiState.selectedDayIndex, meal.id) },
                    onTypeChange = { viewModel.updateMealType(uiState.selectedDayIndex, meal.id, it) }
                )
            }

            // Öğün Ekle Butonu
            OutlinedButton(
                onClick = { viewModel.addMeal(uiState.selectedDayIndex) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Öğün Ekle", fontWeight = FontWeight.Bold)
            }

            // Tüm Günlere Uygula
            TextButton(
                onClick = { viewModel.applyToAllDays(uiState.selectedDayIndex) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bu Günü Tüm Günlere Uygula", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Özet ve Submit
            PlanSummaryCard(uiState = uiState)

            Button(
                onClick = { viewModel.submitPlan(token) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                enabled = !uiState.isSubmitting
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Planı Kaydet ve Aktif Et", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (uiState.submitError != null) {
                Text(
                    text = uiState.submitError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PlanInfoCard(
    title: String,
    onTitleChange: (String) -> Unit,
    startDate: String,
    onDateChange: (String) -> Unit,
    durationDays: Int,
    onDurationChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Assignment, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("PLAN BİLGİLERİ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED), letterSpacing = 1.sp)
            }

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Plan Başlığı (Örn: Definasyon Diyeti)") },
                shape = RoundedCornerShape(12.dp),
                colors = dietTextFieldColors()
            )

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Başlangıç Tarihi", fontSize = 12.sp, color = Color(0xFF64748B))
                    Text(startDate, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
                
                // Gün Sayısı Stepper
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F5F9))) {
                    IconButton(onClick = { if (durationDays > 1) onDurationChange(durationDays - 1) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Azalt", modifier = Modifier.size(18.dp))
                    }
                    Text("$durationDays gün", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { if (durationDays < 90) onDurationChange(durationDays + 1) }) {
                        Icon(Icons.Default.Add, contentDescription = "Artır", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DayTabSelector(
    days: List<DayFormState>,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedDayIndex,
        containerColor = Color.Transparent,
        contentColor = Color(0xFF7C3AED),
        edgePadding = 0.dp,
        divider = {}
    ) {
        days.forEachIndexed { index, day ->
            val isSelected = selectedDayIndex == index
            Tab(
                selected = isSelected,
                onClick = { onDaySelected(index) },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFF7C3AED) else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = day.dayLabel,
                        color = if (isSelected) Color.White else Color(0xFF64748B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MealFormCard(
    meal: MealFormEntry,
    isFirst: Boolean,
    isLast: Boolean,
    onUpdate: (MealFormEntry) -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onTypeChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (icon, color) = when(meal.mealType) {
                        "Kahvaltı" -> Icons.Default.Star to Color(0xFFEAB308)
                        "Öğle" -> Icons.Default.Favorite to Color(0xFF22C55E)
                        "Akşam" -> Icons.Default.Notifications to Color(0xFF3B82F6)
                        else -> Icons.Default.CheckCircle to Color(0xFFF97316)
                    }
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Düzenlenebilir Öğün Başlığı
                    BasicTextField(
                        value = meal.mealType,
                        onValueChange = onTypeChange,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.width(IntrinsicSize.Min).padding(end = 8.dp)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Yukarı/Aşağı Taşıma Butonları
                    if (!isFirst) {
                        IconButton(onClick = onMoveUp, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.ArrowDropUp, contentDescription = "Yukarı", tint = Color(0xFF64748B))
                        }
                    }
                    if (!isLast) {
                        IconButton(onClick = onMoveDown, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Aşağı", tint = Color(0xFF64748B))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color.Red.copy(alpha = 0.7f))
                    }
                }
            }

            OutlinedTextField(
                value = meal.description,
                onValueChange = { onUpdate(meal.copy(description = it)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("Açıklama (Örn: 2 haşlanmış yumurta)") },
                shape = RoundedCornerShape(12.dp),
                colors = dietTextFieldColors()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MacroInput(label = "kcal", value = meal.targetCalories, onValueChange = { onUpdate(meal.copy(targetCalories = it)) }, modifier = Modifier.weight(1f))
                MacroInput(label = "Prot", value = meal.targetProtein, onValueChange = { onUpdate(meal.copy(targetProtein = it)) }, modifier = Modifier.weight(1f))
                MacroInput(label = "Karb", value = meal.targetCarbs, onValueChange = { onUpdate(meal.copy(targetCarbs = it)) }, modifier = Modifier.weight(1f))
                MacroInput(label = "Yağ", value = meal.targetFat, onValueChange = { onUpdate(meal.copy(targetFat = it)) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MacroInput(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) onValueChange(it) },
        modifier = modifier,
        label = { Text(label, fontSize = 10.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = dietTextFieldColors()
    )
}

@Composable
fun PlanSummaryCard(uiState: DietPlanCreatorUiState) {
    val totalMeals = uiState.days.sumOf { it.meals.size }
    val totalCals = uiState.days.sumOf { day -> day.meals.sumOf { it.targetCalories.toDoubleOrNull() ?: 0.0 } }
    val avgCals = if (uiState.durationDays > 0) totalCals / uiState.durationDays else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("PLAN ÖZETİ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryItem(label = "Süre", value = "${uiState.durationDays} Gün")
                SummaryItem(label = "Öğün", value = "$totalMeals")
                SummaryItem(label = "Ort. Kalori", value = String.format("%.0f kcal", avgCals))
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    }
}
