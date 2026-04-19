package com.dogukanpayal.victus_frontend.ui.diet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogukanpayal.victus_frontend.data.model.DietMeal
import com.dogukanpayal.victus_frontend.data.model.DietPlan
import com.dogukanpayal.victus_frontend.data.model.MealItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlanSection(
    viewModel: DietPlanViewModel,
    token: String,
    meals: List<MealItem>,
    onParseDiet: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Dosya seçici (PDF vs)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(it) ?: "application/pdf"
            viewModel.onFileSelected(context, it, mimeType, token)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Hata Mesajı
        uiState.uploadError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (uiState.isUploading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF7C3AED))
            }
        } else if (uiState.plan == null) {
            UploadPromptCard(onUploadClick = { viewModel.onUploadRequested() })
        } else {
            // Plan Varsa Göster
            DietPlanView(
                plan = uiState.plan!!,
                selectedDayIndex = uiState.selectedDayIndex,
                onDaySelected = { viewModel.onDaySelected(it) },
                onDeleteClick = { viewModel.onDeletePlan() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // AI Analysis Output / Button
            DietComplianceCard(
                isAnalyzing = uiState.isAnalyzingCompliance,
                feedback = uiState.complianceFeedback,
                onAnalyzeClick = { viewModel.analyzeCompliance(token, meals) }
            )
        }
    }

    // Yükleme Seçenekleri (Bottom Sheet)
    if (uiState.showUploadSheet) {
        ModalBottomSheet(onDismissRequest = { viewModel.dismissUploadSheet() }) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Diyet Listesi Yükle",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                
                Button(
                    onClick = {
                        // Sadece fotoğraf ve pdf filtreliyoruz
                        filePickerLauncher.launch("*/*") 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF475569))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Dosya Seç (Görsel veya PDF)", color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                }

                var textContent by remember { mutableStateOf("") }
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Veya Metin Olarak Yapıştır", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textContent,
                        onValueChange = { textContent = it },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        placeholder = { Text("Sabah: 2 yumurta, 1 dilim tam buğday ekmeği...", fontSize = 12.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { 
                            if (textContent.isNotBlank()) {
                                onParseDiet(textContent)
                                viewModel.dismissUploadSheet()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        enabled = textContent.isNotBlank()
                    ) {
                        Text("Yapay Zeka ile Çözümle", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun UploadPromptCard(onUploadClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFFEDE9FE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(32.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Diyetisyenin Programını Yükle", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text("Yapay zeka 7 günlük planını oluştursun", fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(top = 4.dp))
            }
            Button(
                onClick = onUploadClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) {
                Text("Listeyi Yükle", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun DietPlanView(
    plan: DietPlan,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    val pageCount = if (plan.days.isNotEmpty()) plan.days.size else 1
    val dayNames = List(pageCount) { index -> "${index + 1}. Gün" }
    
    // Ensure selected index is within valid bounds for dynamic lengths
    val validInitialPage = selectedDayIndex.coerceIn(0, pageCount - 1)
    val pagerState = rememberPagerState(initialPage = validInitialPage, pageCount = { pageCount })
    val scope = rememberCoroutineScope()
    // ViewModel state ile Pager'ı senkronize tut
    LaunchedEffect(selectedDayIndex) {
        val validIndex = selectedDayIndex.coerceIn(0, pageCount - 1)
        if (pagerState.currentPage != validIndex) {
            pagerState.animateScrollToPage(validIndex)
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        if (selectedDayIndex != pagerState.currentPage) {
            onDaySelected(pagerState.currentPage)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Yapay Zeka Diyet Planı",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(vertical = 12.dp)
            )
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color(0xFFEF4444))
            }
        }

        // Günlük Sekmeler
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF7C3AED),
            edgePadding = 0.dp,
            divider = {}
        ) {
            dayNames.forEachIndexed { index, title ->
                val isSelected = pagerState.currentPage == index
                Tab(
                    selected = isSelected,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF7C3AED) else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pager ile 7 günün listesi
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val dailyPlan = plan.days.find { it.dayIndex == page }
            if (dailyPlan != null && dailyPlan.meals.isNotEmpty()) {
                DailyDietPlanPage(meals = dailyPlan.meals)
            } else {
                Text(
                    "Bu gün için öğün bulunamadı.",
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun DailyDietPlanPage(meals: List<DietMeal>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        meals.forEach { meal ->
            DietMealCard(meal = meal)
        }
    }
}

@Composable
private fun DietMealCard(meal: DietMeal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = meal.time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
                if (meal.calories != null) {
                    Text(
                        text = "${meal.calories} kcal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = meal.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = meal.description,
                fontSize = 14.sp,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
private fun DietComplianceCard(
    isAnalyzing: Boolean,
    feedback: String?,
    onAnalyzeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Yapay Zeka Diyet Asistanı",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            }

            if (isAnalyzing) {
                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF3B82F6), modifier = Modifier.size(32.dp))
                }
            } else if (feedback != null) {
                Text(
                    text = feedback,
                    fontSize = 15.sp,
                    color = Color(0xFF1E3A8A),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    onClick = onAnalyzeClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDBEAFE), contentColor = Color(0xFF1E3A8A))
                ) {
                    Text("Tekrar Analiz Et", fontWeight = FontWeight.SemiBold)
                }
            } else {
                Text(
                    text = "Günün öğünlerini diyet planınla kıyaslayarak anlık değerlendirme al.",
                    fontSize = 14.sp,
                    color = Color(0xFF60A5FA),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    onClick = onAnalyzeClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("Günü Analiz Et", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
