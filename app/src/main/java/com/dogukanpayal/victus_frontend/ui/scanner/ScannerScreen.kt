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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(viewModel: ScannerViewModel, token: String) {
    val context = LocalContext.current
    val lastScan by viewModel.lastScanResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val error by viewModel.error.collectAsState()

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
    val isShowingReview by viewModel.isShowingReview.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isShowingReview) {
        ModalBottomSheet(
            onDismissRequest = { if (!isSaving) viewModel.dismissReview() },
            sheetState = sheetState,
            containerColor = surfaceWhite,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFE2E8F0)) }
        ) {
            lastScan?.let { scan ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Analiz Sonucu",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Image Preview in Sheet
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF1F5F9))
                    ) {
                        AsyncImage(
                            model = selectedImageUri ?: "🥗", // URI yoksa emoji (fallback)
                            contentDescription = "Yemek",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Food Name Input
                    OutlinedTextField(
                        value = scan.foodName,
                        onValueChange = { viewModel.updateFoodName(it) },
                        label = { Text("Yemek Adı") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryGreen,
                            focusedLabelColor = primaryGreen
                        ),
                        singleLine = true,
                        enabled = !isSaving
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Portion Selector (Slider)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Porsiyon",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textDark
                        )
                        Surface(
                            color = lightGreenBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "x ${String.format("%.1f", scan.portion)}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = primaryGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    Slider(
                        value = scan.portion,
                        onValueChange = { viewModel.updatePortion(it) },
                        valueRange = 0.5f..5.0f,
                        steps = 8, // 0.5, 1.0, 1.5 ... 5.0
                        enabled = !isSaving,
                        colors = SliderDefaults.colors(
                            thumbColor = primaryGreen,
                            activeTrackColor = primaryGreen,
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Summary Calories
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Toplam Kalori", fontSize = 13.sp, color = textGray)
                                Text(
                                    text = "${scan.calories} kcal",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textDark
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.PlayArrow, // Onay ikonu olarak geçici
                                contentDescription = null,
                                tint = primaryGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.dismissReview() },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isSaving,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text("Vazgeç", color = textGray)
                        }
                        Button(
                            onClick = { viewModel.confirmMeal(token) },
                            modifier = Modifier
                                .weight(1f)
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
                                Text("Öğünü Ekle", fontWeight = FontWeight.Bold)
                            }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
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
                    .weight(1f)
                    .fillMaxWidth()
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
                            .clip(RoundedCornerShape(32.dp)),
                        contentScale = ContentScale.Crop
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
                    val yOffset by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "yOffset"
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .offset(y = maxHeight * yOffset)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, primaryGreen, Color.Transparent)
                                    )
                                )
                        )
                    }
                }

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
                    icon = Icons.Default.Home,
                    label = "Galeri",
                    primaryGreen = primaryGreen,
                    onClick = { launchGallery() }
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
                    icon = Icons.Default.Settings,
                    label = "Flaş",
                    primaryGreen = primaryGreen,
                    onClick = { /* Flash toggle */ }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Last Scan Result Card
            lastScan?.let { scan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                text = "Son Tarama",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryGreen,
                                letterSpacing = 1.sp
                            )
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
