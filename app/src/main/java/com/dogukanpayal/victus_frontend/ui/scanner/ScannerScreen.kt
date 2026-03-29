package com.dogukanpayal.victus_frontend.ui.scanner

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(viewModel: ScannerViewModel, token: String) {
    val context = LocalContext.current
    val lastScans by viewModel.lastScanResults.collectAsState()
    val isShowingReview by viewModel.isShowingReview.collectAsState()
    val scanHistory by viewModel.scanHistory.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var isManualEntryDialogVisible by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF22C55E)
    val lightGreenBg = Color(0xFFF0FDF4)
    val surfaceWhite = Color.White
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)

    val snackbarHostState = remember { SnackbarHostState() }

    // Kayıt başarılı olduğunda tetiklenen efekt
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("Öğün başarıyla kaydedildi! 🥗")
            viewModel.resetSaveSuccess()
            viewModel.clearSelectedImage()
        }
    }

    // ═══════════════════════════════════════════
    // Gallery Launcher (PickVisualMedia - izin gerektirmez)
    // ═══════════════════════════════════════════
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onImageSelected(it, context, token) }
    }

    // ═══════════════════════════════════════════
    // Camera Launcher (TakePicture)
    // ═══════════════════════════════════════════
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.onPhotoTaken(success, context, token)
    }

    // ═══════════════════════════════════════════
    // Camera Permission Launcher
    // ═══════════════════════════════════════════
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = viewModel.createPhotoUri(context)
            cameraLauncher.launch(uri)
        }
    }

    // Kamera butonuna basıldığında çağrılan fonksiyon
    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val uri = viewModel.createPhotoUri(context)
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Galeri butonuna basıldığında çağrılan fonksiyon
    fun launchGallery() {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    // ═══════════════════════════════════════════
    // Review Bottom Sheet
    // ═══════════════════════════════════════════
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isShowingReview) {
        ModalBottomSheet(
            onDismissRequest = { if (!isSaving) viewModel.dismissReview() },
            sheetState = sheetState,
            containerColor = surfaceWhite,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFE2E8F0)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Bulunan Yiyecekler",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    itemsIndexed(lastScans) { index, scan ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                // Delete / Remove Button
                                IconButton(
                                    onClick = { viewModel.removeScanResult(index) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(32.dp),
                                    enabled = !isSaving
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Kaldır",
                                        tint = textGray.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Mini Icon/Emoji
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(primaryGreen.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🍴", fontSize = 18.sp)
                                        }
                                        
                                        // Food Name
                                        OutlinedTextField(
                                            value = scan.foodName,
                                            onValueChange = { viewModel.updateFoodName(index, it) },
                                            label = { Text("Yemek Adı", fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true,
                                            enabled = !isSaving,
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = textDark,
                                                unfocusedTextColor = textDark,
                                                focusedBorderColor = primaryGreen,
                                                unfocusedBorderColor = Color(0xFFE2E8F0)
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Portion
                                        OutlinedTextField(
                                            value = scan.portion.toString(),
                                            onValueChange = { newValue ->
                                                val floatValue = newValue.replace(",", ".").toFloatOrNull()
                                                if (floatValue != null && floatValue > 0f) {
                                                    viewModel.updatePortion(index, floatValue)
                                                }
                                            },
                                            label = { Text("Porsiyon", fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true,
                                            enabled = !isSaving,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = textDark,
                                                unfocusedTextColor = textDark,
                                                focusedBorderColor = primaryGreen,
                                                unfocusedBorderColor = Color(0xFFE2E8F0)
                                            )
                                        )

                                        // Calories Display
                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text("Kalori", fontSize = 11.sp, color = textGray)
                                            Text(
                                                text = "${scan.calories} kcal",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Buttons at Fixed bottom of Sheet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.dismissReview() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isSaving
                    ) {
                        Text("Vazgeç", color = textGray)
                    }
                    Button(
                        onClick = { viewModel.confirmAllMeals(token) },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Hepsini Ekle", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(top = 24.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Yemeğini Tara",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI teknolojisi ile saniyeler içinde kalori değerlerini öğrenin",
                fontSize = 14.sp,
                color = textGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ═══════════════════════════════════════════
            // Scanning Frame / Selected Image
            // ═══════════════════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp) // Fixed height to provide a large, clear preview
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .border(
                        2.dp,
                        if (selectedImageUri != null) primaryGreen else primaryGreen.copy(alpha = 0.3f),
                        RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    // Seçilen fotoğrafı göster
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Seçilen Yemek Fotoğrafı",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp))
                            .padding(8.dp), // Give it some padding for border effect
                        contentScale = ContentScale.Fit // Show the WHOLE photo
                    )

                    // Temizle butonu (sağ üst köşe)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { viewModel.clearSelectedImage() },
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fotoğrafı Kaldır",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Fotoğraf seçildi bilgi bandı (alt kısım)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "📸",
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Fotoğraf hazır — analiz için gönderilecek",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    // Placeholder (fotoğraf seçilmemiş)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF134E4A), Color(0xFF0F766E))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🥗",
                            fontSize = 120.sp
                        )
                    }

                    // Scanning Frame Corners
                    ScanningOverlay(primaryGreen)

                    // Scanning Line Animation
                    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
                    val translateY by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 240f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scanningLine"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(2.dp)
                            .align(Alignment.Center)
                            .offset(y = (-120 + translateY.toInt()).dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, primaryGreen, Color.Transparent)
                                )
                            )
                    )
                }
            }

            if (isManualEntryDialogVisible) {
                ManualEntryDialog(
                    onDismiss = { isManualEntryDialogVisible = false },
                    onSubmit = { name, port ->
                        isManualEntryDialogVisible = false
                        viewModel.analyzeTextPortion(name, port, token)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

                // Analiz ediliyor loading overlay
                if (isAnalyzing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Yapay Zeka Analiz Ediyor...",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }


            Spacer(modifier = Modifier.height(24.dp))

            // Hata Mesajı
            error?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            color = Color(0xFF991B1B),
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearSelectedImage() }) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF991B1B), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }


            // ═══════════════════════════════════════════
            // Action Buttons
            // ═══════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButton(
                    icon = if (selectedImageUri != null) Icons.Default.Close else Icons.Default.Home,
                    label = if (selectedImageUri != null) "Temizle" else "Galeri",
                    primaryGreen = primaryGreen,
                    onClick = { 
                        if (selectedImageUri != null) viewModel.clearSelectedImage() 
                        else launchGallery() 
                    }
                )

                // Main Shutter Button
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable { launchCamera() },
                    color = primaryGreen,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(4.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        )
                        Icon(
                            imageVector = if (selectedImageUri != null) Icons.Default.Refresh else Icons.Default.PlayArrow,
                            contentDescription = "Capture",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                ControlButton(
                    icon = Icons.Default.Edit,
                    label = "Manuel",
                    primaryGreen = primaryGreen,
                    onClick = { isManualEntryDialogVisible = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            // Scan History Header and Clear Button
            if (scanHistory.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Oturum Geçmişi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(
                        text = "Temizle",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryGreen,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.clearHistory() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Scan History List
            scanHistory.reversed().forEach { scan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceWhite),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✅", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = scan.foodName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textDark
                            )
                            Text(
                                text = "${scan.portion} porsiyon",
                                fontSize = 12.sp,
                                color = textGray
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${scan.calories}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textDark
                            )
                            Text(
                                text = "kcal",
                                fontSize = 11.sp,
                                color = textGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tip Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                colors = CardDefaults.cardColors(containerColor = lightGreenBg),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(primaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("i", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "İpucu: En iyi sonuç için yemeği iyi aydınlatılmış bir ortamda ve net bir şekilde kadraja alın.",
                        fontSize = 12.sp,
                        color = Color(0xFF166534),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ScanningOverlay(color: Color) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        val cornerSize = 40.dp
        val strokeWidth = 4.dp

        // Top Left
        Box(modifier = Modifier.align(Alignment.TopStart).size(cornerSize)
            .border(width = strokeWidth, color = color, shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 0.dp, topEnd = 0.dp, bottomEnd = 0.dp)))

        // Top Right
        Box(modifier = Modifier.align(Alignment.TopEnd).size(cornerSize)
            .border(width = strokeWidth, color = color, shape = RoundedCornerShape(topEnd = 12.dp, topStart = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp)))

        // Bottom Left
        Box(modifier = Modifier.align(Alignment.BottomStart).size(cornerSize)
            .border(width = strokeWidth, color = color, shape = RoundedCornerShape(bottomStart = 12.dp, topStart = 0.dp, topEnd = 0.dp, bottomEnd = 0.dp)))

        // Bottom Right
        Box(modifier = Modifier.align(Alignment.BottomEnd).size(cornerSize)
            .border(width = strokeWidth, color = color, shape = RoundedCornerShape(bottomEnd = 12.dp, topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp)))
    }
}

@Composable
fun ControlButton(icon: ImageVector, label: String, primaryGreen: Color, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .clickable { onClick() },
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF334155),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color(0xFF64748B))
    }
}

@Composable
fun ManualEntryDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Double) -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var portion by remember { mutableStateOf("1.0") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manuel Yemek Girişi", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Yemek Adı") },
                    placeholder = { Text("Örn: Tavuk Sote") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = portion,
                    onValueChange = { portion = it },
                    label = { Text("Porsiyon Adedi") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val p = portion.toDoubleOrNull() ?: 1.0
                    onSubmit(foodName, p)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
            ) {
                Text("Ekle", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
