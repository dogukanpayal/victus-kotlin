package com.dogukanpayal.victus_frontend.ui.workout

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogukanpayal.victus_frontend.data.model.Exercise
import com.dogukanpayal.victus_frontend.data.model.WorkoutPreset
import com.dogukanpayal.victus_frontend.data.model.WorkoutPresetExercise
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel, token: String) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            viewModel.loadExercises(token)
            viewModel.loadBurnedCalories(token)
        }
    }

    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showSelectDialog by remember { mutableStateOf(false) }
    var editingPreset by remember { mutableStateOf<WorkoutPreset?>(null) }

    val primaryGreen = Color(0xFF22C55E)
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSelectDialog = true },
                containerColor = primaryGreen,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 100.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Antrenman Seç veya Oluştur")
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            AnimatedVisibility(
                visible = uiState.burnedCaloriesForToday > 0
            ) {
                Column {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF2F2) // Light red background
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Yakılan Kalori",
                                fontSize = 16.sp,
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔥 ", fontSize = 24.sp)
                                Text(
                                    text = "${uiState.burnedCaloriesForToday.toInt()} kcal", 
                                    fontSize = 24.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = Color(0xFFEF4444)
                                )
                            }
                            
                            TextButton(
                                onClick = { viewModel.resetCalories(token) },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Sıfırla", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            Text(text = "Antrenman Günlüğün", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textDark)
            if (uiState.isLoading && uiState.presets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryGreen)
                }
            } else {
                val activePresets = uiState.activePresetIds.mapNotNull { id -> 
                    uiState.presets.find { it.id == id } 
                }
                
                if (activePresets.isNotEmpty()) {
                    Text(
                        text = "Bugünün antrenmanlarını tamamla ve hedefine bir adım daha yaklaş!",
                        fontSize = 14.sp,
                        color = textGray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(activePresets) { activePreset ->
                            ActiveWorkoutCard(
                                preset = activePreset,
                                completedIds = uiState.completedExerciseIds,
                                onCompleteExercise = { exercise ->
                                    viewModel.logWorkout(token, exercise)
                                },
                                onRemoveWorkout = {
                                    viewModel.removeWorkoutForToday(token, activePreset)
                                }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Bugün için henüz bir antrenman seçmedin.",
                                color = textDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Aşağıdaki + butonuna basarak kayıtlı antrenman listenden bir antrenman seçerek günün antrenmanını belirleyebilirsin.",
                                color = textGray,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSelectDialog) {
        SelectWorkoutBottomSheet(
            presets = uiState.presets,
            onDismiss = { showSelectDialog = false },
            onSelectPreset = { presetId -> 
                viewModel.addWorkoutForToday(presetId)
                showSelectDialog = false
            },
            onCreateNew = {
                showSelectDialog = false
                showCreateDialog = true
            },
            onDeletePreset = { presetId ->
                viewModel.deletePreset(token, presetId)
            },
            onEditPreset = { preset ->
                editingPreset = preset
                showSelectDialog = false
            }
        )
    }

    if (showCreateDialog || editingPreset != null) {
        CreateWorkoutDialog(
            exercises = uiState.exercises,
            initialPreset = editingPreset,
            onDismiss = { 
                showCreateDialog = false
                editingPreset = null
            },
            onSavePreset = { preset -> 
                viewModel.savePreset(preset)
                if (editingPreset == null) {
                    // Otomatik olarak yeni oluşturulanı günün antrenmanı yap (sadece yeni ise)
                    viewModel.addWorkoutForToday(preset.id)
                }
                editingPreset = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectWorkoutBottomSheet(
    presets: List<WorkoutPreset>,
    onDismiss: () -> Unit,
    onSelectPreset: (String) -> Unit,
    onCreateNew: () -> Unit,
    onDeletePreset: (String) -> Unit,
    onEditPreset: (WorkoutPreset) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Günün Antrenmanını Seç",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCreateNew,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF22C55E))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Yeni Antrenman Oluştur", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (presets.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Henüz kayıtlı bir antrenmanın yok.", color = Color(0xFF64748B))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(presets) { preset ->
                        PresetCard(
                            preset = preset,
                            onSelectForToday = { onSelectPreset(preset.id) },
                            onDelete = { onDeletePreset(preset.id) },
                            onEdit = { onEditPreset(preset) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveWorkoutCard(
    preset: WorkoutPreset,
    completedIds: Set<String>,
    onCompleteExercise: (WorkoutPresetExercise) -> Unit,
    onRemoveWorkout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preset.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${preset.exercises.size} Hareket",
                        fontSize = 14.sp,
                        color = Color(0xFF15803D)
                    )
                }

                TextButton(onClick = onRemoveWorkout) {
                    Text("Kaldır", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFBBF7D0))
            Spacer(modifier = Modifier.height(8.dp))
            
            preset.exercises.forEach { exercise ->
                val isCompleted = completedIds.contains(exercise.id)
                ExerciseRow(
                    exercise = exercise, 
                    isCompleted = isCompleted,
                    onComplete = { onCompleteExercise(exercise) }
                )
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: WorkoutPreset,
    onSelectForToday: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f, label = "rotation")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preset.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${preset.exercises.size} Hareket",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = Color(0xFF3B82F6))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color(0xFFEF4444))
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Genişlet",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    preset.exercises.forEach { exercise ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(exercise.exerciseName, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                            val desc = if (exercise.isDurationBased) "${exercise.durationMinutes} Dk" else "${exercise.sets}x${exercise.reps}"
                            Text(desc, color = Color(0xFF64748B))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onSelectForToday,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Günlüğe Ekle", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseRow(exercise: WorkoutPresetExercise, isCompleted: Boolean, onComplete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.exerciseName,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            val desc = if (exercise.isDurationBased) {
                "${exercise.durationMinutes} Dakika"
            } else {
                "${exercise.sets} Set x ${exercise.reps} Tekrar"
            }
            Text(
                text = desc,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isCompleted) Color(0xFF22C55E) else Color(0xFFE2E8F0))
                .clickable {
                    onComplete()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Tamamla",
                tint = if (isCompleted) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSavePreset: (WorkoutPreset) -> Unit,
    initialPreset: WorkoutPreset? = null
) {
    var presetName by remember { mutableStateOf(initialPreset?.name ?: "") }
    val addedExercises = remember { 
        mutableStateListOf<WorkoutPresetExercise>().apply {
            initialPreset?.exercises?.let { addAll(it) }
        }
    }
    
    // Form State
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    val isDurationBased = true
    var duration by remember { mutableStateOf("") }
    
    var expanded by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = if (initialPreset == null) "Yeni Antrenman Şablonu" else "Şablonu Düzenle",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = presetName,
                onValueChange = { presetName = it },
                label = { Text("Şablon Adı (Örn: Bacak Günü)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Hareket Ekle", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(12.dp))

            // Exercise Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedExercise?.name ?: "Egzersiz Seçin",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Egzersiz") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    exercises.forEach { exercise ->
                        DropdownMenuItem(
                            text = { Text(exercise.name) },
                            onClick = {
                                selectedExercise = exercise
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { if (it.all { char -> char.isDigit() }) duration = it },
                label = { Text("Süre (Dakika)") },
                placeholder = { Text("Örn: 30") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            val isAddEnabled = selectedExercise != null && duration.isNotBlank()
            
            Button(
                onClick = {
                    val ex = WorkoutPresetExercise(
                        id = UUID.randomUUID().toString(),
                        exerciseId = selectedExercise!!.id,
                        exerciseName = selectedExercise!!.name,
                        isDurationBased = isDurationBased,
                        durationMinutes = duration.toIntOrNull(),
                        sets = null,
                        reps = null
                    )
                    addedExercises.add(ex)
                    
                    // Reset form
                    selectedExercise = null
                    duration = ""
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                enabled = isAddEnabled
            ) {
                Text("Hareketi Ekle", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Added Exercises List
            if (addedExercises.isNotEmpty()) {
                Text("Eklenecek Hareketler (${addedExercises.size})", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    addedExercises.forEach { exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(exercise.exerciseName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                val desc = if (exercise.isDurationBased) "${exercise.durationMinutes} Dakika" else "${exercise.sets} Set x ${exercise.reps} Tekrar"
                                Text(desc, fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                            IconButton(onClick = { addedExercises.remove(exercise) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (presetName.isNotBlank() && addedExercises.isNotEmpty()) {
                        val preset = WorkoutPreset(
                            id = initialPreset?.id ?: UUID.randomUUID().toString(),
                            name = presetName,
                            exercises = addedExercises.toList()
                        )
                        onSavePreset(preset)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                enabled = presetName.isNotBlank() && addedExercises.isNotEmpty()
            ) {
                Text(if (initialPreset == null) "Şablonu Kaydet" else "Değişiklikleri Kaydet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

