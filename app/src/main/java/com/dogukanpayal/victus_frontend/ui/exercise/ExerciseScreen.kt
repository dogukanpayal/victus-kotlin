package com.dogukanpayal.victus_frontend.ui.exercise

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.dogukanpayal.victus_frontend.data.model.BodyCompositionReport
import com.dogukanpayal.victus_frontend.data.model.HealthMetricsData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(viewModel: ExerciseViewModel, token: String) {
    val context = LocalContext.current
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()
    val metricsHistory by viewModel.metricsHistory.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val isComparisonOpen by viewModel.isComparisonOpen.collectAsState()

    val primaryGreen = Color(0xFF22C55E)
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)

    LaunchedEffect(Unit) {
        viewModel.loadMetricsHistory(token)
    }

    // Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onImageSelected(it, context, token) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.onPhotoTaken(success, context, token)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = viewModel.createPhotoUri(context)
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val uri = viewModel.createPhotoUri(context)
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Result Bottom Sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (analysisResult != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissResult() },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            AnalysisResultContent(analysisResult!!, primaryGreen)
        }
    }

    // Comparison Sheet
    if (isComparisonOpen) {
        ComparisonSheet(viewModel, token)
    }

    val scrollState = rememberScrollState()
    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vücut Analizi",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textDark
            )
            Text(
                text = "AI ile vücut kompozisyonunu ve postürünü keşfet",
                fontSize = 14.sp,
                color = textGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Section 1: Upload
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Black.copy(alpha = 0.05f))
                    .border(2.dp, primaryGreen.copy(alpha = 0.3f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    BodyGuideOverlay(primaryGreen)
                }

                if (isAnalyzing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("🧬 AI Analiz Ediyor...", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseControlButton(Icons.Default.PhotoLibrary, "Galeri") {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                Surface(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .clickable { launchCamera() },
                    color = primaryGreen,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                ExerciseControlButton(Icons.Default.Refresh, "Sıfırla") {
                    viewModel.clearSelectedImage()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Section 2: Saved Records
            if (metricsHistory.isNotEmpty()) {
                Text(
                    text = "Kaydedilenler",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                
                metricsHistory.reversed().forEach { metric ->
                    HistoryItem(
                        metric = metric,
                        color = primaryGreen,
                        onDelete = { viewModel.deleteMetrics(token, metric.id) }
                    )
                }

                if (metricsHistory.size >= 2) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.openComparison() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = textDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Compare, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Gelişimi Karşılaştır", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonSheet(viewModel: ExerciseViewModel, token: String) {
    val metricsHistory by viewModel.metricsHistory.collectAsState()
    val selectedBefore by viewModel.selectedBefore.collectAsState()
    val selectedAfter by viewModel.selectedAfter.collectAsState()
    val comparisonResult by viewModel.comparisonResult.collectAsState()
    val isComparisonLoading by viewModel.isComparisonLoading.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = { viewModel.closeComparison() },
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Gelişimi Karşılaştır",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (comparisonResult == null) {
                // Step 1: Selection
                ComparisonSelection(
                    label = "ÖNCESİ",
                    selected = selectedBefore,
                    options = metricsHistory,
                    onSelect = { viewModel.selectBefore(it) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                ComparisonSelection(
                    label = "SONRASI",
                    selected = selectedAfter,
                    options = metricsHistory,
                    onSelect = { viewModel.selectAfter(it) }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.performComparison(token) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = selectedBefore != null && selectedAfter != null && selectedBefore!!.id != selectedAfter!!.id && !isComparisonLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isComparisonLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Yapay Zeka Analiz Ediyor...", fontWeight = FontWeight.Bold)
                    } else {
                        Text("Karşılaştır", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Step 2: Result
                ComparisonResultUI(comparisonResult!!)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ComparisonSelection(
    label: String,
    selected: HealthMetricsData?,
    options: List<HealthMetricsData>,
    onSelect: (HealthMetricsData) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF1F5F9))
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF64748B))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selected?.let { "${it.createdAt.replace("T", " ").take(19)} - ${it.weight} kg" } ?: "Kayıt seçin ▼",
                    color = if (selected != null) Color(0xFF0F172A) else Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
            ) {
                options.reversed().forEach { option ->
                    DropdownMenuItem(
                        text = { Text("${option.createdAt.replace("T", " ").take(19)} - ${option.weight} kg", color = Color.Black) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ComparisonResultUI(result: com.dogukanpayal.victus_frontend.data.model.ComparisonResult) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ComparisonMetricRow("Yağ Oranı", "%${result.before.fatPercentage}", "%${result.after.fatPercentage}", result.fatDelta, false)
            ComparisonMetricRow("Kas Oranı", "%${result.before.musclePercentage}", "%${result.after.musclePercentage}", result.muscleDelta, true)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ComparisonMetricRow("Ağırlık", "${result.before.weight} kg", "${result.after.weight} kg", result.weightDelta, null)
            ComparisonMetricRow("BMI", result.before.bmi.toString().take(4), result.after.bmi.toString().take(4), result.bmiDelta, false)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Postür Değişimi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PostureNoteBox("Önceki", result.before.postureNotes, Modifier.weight(1f))
            PostureNoteBox("Sonraki", result.after.postureNotes, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Özet Değerlendirme", fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                Spacer(modifier = Modifier.height(8.dp))
                Text(result.summaryText, fontSize = 14.sp, color = Color(0xFF166534).copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun ComparisonMetricRow(label: String, before: String, after: String, delta: Double, isPositiveGood: Boolean?) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(before, fontSize = 14.sp, color = Color.Gray)
                Text(" → ", fontSize = 12.sp, color = Color.Gray)
                Text(after, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            
            val color = when {
                isPositiveGood == null -> Color(0xFF64748B)
                delta > 0 -> if (isPositiveGood) Color(0xFF22C55E) else Color(0xFFEF4444)
                delta < 0 -> if (isPositiveGood) Color(0xFFEF4444) else Color(0xFF22C55E)
                else -> Color.Gray
            }
            
            val icon = if (delta > 0) Icons.Default.ArrowUpward else if (delta < 0) Icons.Default.ArrowDownward else null
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let { Icon(it, contentDescription = null, tint = color, modifier = Modifier.size(12.dp)) }
                Text(
                    text = if (delta == 0.0) "Değişim yok" else "${String.format("%.1f", Math.abs(delta))}",
                    color = color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PostureNoteBox(label: String, note: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F5F9))
                .padding(8.dp)
        ) {
            Text(note, fontSize = 11.sp, color = Color(0xFF334155), overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun BodyGuideOverlay(color: Color) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        // Corner guides
        val cornerSize = 40.dp
        val stroke = 3.dp
        
        Box(modifier = Modifier.align(Alignment.TopStart).size(cornerSize).border(stroke, color, RoundedCornerShape(topStart = 16.dp)))
        Box(modifier = Modifier.align(Alignment.TopEnd).size(cornerSize).border(stroke, color, RoundedCornerShape(topEnd = 16.dp)))
        Box(modifier = Modifier.align(Alignment.BottomStart).size(cornerSize).border(stroke, color, RoundedCornerShape(bottomStart = 16.dp)))
        Box(modifier = Modifier.align(Alignment.BottomEnd).size(cornerSize).border(stroke, color, RoundedCornerShape(bottomEnd = 16.dp)))

        // Silhouette placeholder
        Icon(
            imageVector = Icons.Default.AccessibilityNew,
            contentDescription = null,
            tint = color.copy(alpha = 0.1f),
            modifier = Modifier.size(240.dp).align(Alignment.Center)
        )
        
        Text(
            text = "Lütfen boydan, net bir fotoğraf çekin",
            color = color.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
fun AnalysisResultContent(report: BodyCompositionReport, primaryGreen: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text("Analiz Raporu", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(
                label = "Yağ Oranı",
                value = "%${report.currentMetrics.fatPercentage}",
                delta = report.fatDelta,
                modifier = Modifier.weight(1f),
                isPositiveGood = false
            )
            MetricCard(
                label = "Kas Oranı",
                value = "%${report.currentMetrics.musclePercentage}",
                delta = report.muscleDelta,
                modifier = Modifier.weight(1f),
                isPositiveGood = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = primaryGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Postür Notu", fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(report.postureNotes, fontSize = 14.sp, color = Color(0xFF166534).copy(alpha = 0.8f))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("BMI: ${"%.1f".format(report.currentMetrics.bmi)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Surface(
                color = primaryGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Güven: %${(report.confidenceScore * 100).toInt()}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = primaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, delta: Double, modifier: Modifier, isPositiveGood: Boolean) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            
            val deltaColor = if (delta > 0) {
                if (isPositiveGood) Color(0xFF22C55E) else Color(0xFFEF4444)
            } else if (delta < 0) {
                if (isPositiveGood) Color(0xFFEF4444) else Color(0xFF22C55E)
            } else Color.Gray

            val icon = if (delta > 0) Icons.Default.ArrowUpward else if (delta < 0) Icons.Default.ArrowDownward else null

            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let { Icon(it, contentDescription = null, tint = deltaColor, modifier = Modifier.size(14.dp)) }
                Text(
                    text = if (delta == 0.0) "-" else "%.1f".format(Math.abs(delta)),
                    color = deltaColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HistoryItem(metric: HealthMetricsData, color: Color, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("📈", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${metric.weight} kg", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(metric.createdAt.replace("T", " ").take(19), fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Yağ: %${metric.fatPercentage}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Kas: %${metric.musclePercentage}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Sil",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseControlButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(56.dp).clip(CircleShape).clickable { onClick() },
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF334155))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B))
    }
}
