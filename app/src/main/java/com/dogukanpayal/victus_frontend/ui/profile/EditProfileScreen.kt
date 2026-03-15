package com.dogukanpayal.victus_frontend.ui.profile

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dogukanpayal.victus_frontend.ui.setup_profile.Goal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onNavigateBack: () -> Unit = {},
    accessToken: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()

    val primaryGreen = Color(0xFF22C55E)
    val darkText = Color(0xFF0F172A)
    val grayText = Color(0xFF64748B)
    val lightGray = Color(0xFFF8FAFC)
    val mediumGray = Color(0xFF94A3B8)
    val borderGray = Color(0xFFE2E8F0)

    // Load profile when screen is opened
    LaunchedEffect(accessToken) {
        if (accessToken.isNotEmpty()) {
            viewModel.loadUserProfile(accessToken)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profili Düzenle",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = darkText,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = darkText
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (uiState.isLoading) {
            // Loading State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryGreen)
            }
        } else {
            // Image picker launcher
            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri != null) {
                    viewModel.onPhotoSelected(uri.toString())
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Image with Camera Overlay
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Background Profile Image or Icon
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(lightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!uiState.selectedImageUri.isNullOrEmpty()) {
                            // Show selected image
                            AsyncImage(
                                model = uiState.selectedImageUri,
                                contentDescription = "Selected Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center
                            )
                        } else if (!uiState.avatarUrl.isNullOrEmpty()) {
                            // Show uploaded avatar URL
                            AsyncImage(
                                model = uiState.avatarUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center
                            )
                        } else {
                            // Show placeholder icon
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(80.dp),
                                tint = mediumGray
                            )
                        }
                    }

                    // Camera Icon Overlay - Green circular background with white camera icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(primaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "FOTOĞRAFI DEĞİŞTİR",
                    color = primaryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Input Fields Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    EditProfileInputField(
                        label = "Ad Soyad",
                        value = uiState.name,
                        onValueChange = viewModel::onNameChanged,
                        icon = Icons.Default.Person,
                        placeholder = "Adınız Soyadınız",
                        primaryGreen = primaryGreen,
                        lightGray = lightGray,
                        grayText = grayText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    EditProfileInputField(
                        label = "E-posta",
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        icon = Icons.Default.Email,
                        placeholder = "e-posta@adresiniz.com",
                        primaryGreen = primaryGreen,
                        lightGray = lightGray,
                        grayText = grayText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            EditProfileInputField(
                                label = "Boy (cm)",
                                value = uiState.heightCm,
                                onValueChange = viewModel::onHeightChanged,
                                placeholder = "180",
                                primaryGreen = primaryGreen,
                                lightGray = lightGray,
                                grayText = grayText
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            EditProfileInputField(
                                label = "Kilo (kg)",
                                value = uiState.weightKg,
                                onValueChange = viewModel::onWeightChanged,
                                placeholder = "75",
                                primaryGreen = primaryGreen,
                                lightGray = lightGray,
                                grayText = grayText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Fitness Hedefi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = grayText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(lightGray, RoundedCornerShape(16.dp))
                            .border(1.dp, borderGray, RoundedCornerShape(16.dp))
                            .clickable { expanded = true }
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when(uiState.fitnessGoal) {
                                        Goal.LOSE_WEIGHT -> Icons.AutoMirrored.Filled.TrendingDown
                                        Goal.GAIN_MUSCLE -> Icons.Default.FitnessCenter
                                        Goal.STAY_IN_SHAPE -> Icons.Default.Favorite
                                    },
                                    contentDescription = null,
                                    tint = mediumGray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = when(uiState.fitnessGoal) {
                                        Goal.LOSE_WEIGHT -> "Kilo Ver"
                                        Goal.GAIN_MUSCLE -> "Kas Kütlesi Artırmak"
                                        Goal.STAY_IN_SHAPE -> "Formda Kal"
                                    },
                                    color = darkText,
                                    fontSize = 16.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = mediumGray
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Goal.entries.forEach { goal ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = when(goal) {
                                                    Goal.LOSE_WEIGHT -> Icons.AutoMirrored.Filled.TrendingDown
                                                    Goal.GAIN_MUSCLE -> Icons.Default.FitnessCenter
                                                    Goal.STAY_IN_SHAPE -> Icons.Default.Favorite
                                                },
                                                contentDescription = null,
                                                tint = primaryGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                when(goal) {
                                                    Goal.LOSE_WEIGHT -> "Kilo Ver"
                                                    Goal.GAIN_MUSCLE -> "Kas Kütlesi Artırmak"
                                                    Goal.STAY_IN_SHAPE -> "Formda Kal"
                                                }
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.onGoalChanged(goal)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Error/Success Message
                    if (uiState.errorMessage.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            color = if (uiState.updateState == EditProfileUpdateState.ERROR) {
                                Color(0xFFFFEBEE)
                            } else {
                                Color(0xFFE8F5E9)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (uiState.updateState == EditProfileUpdateState.ERROR) {
                                        Icons.Default.Close
                                    } else {
                                        Icons.Default.Check
                                    },
                                    contentDescription = null,
                                    tint = if (uiState.updateState == EditProfileUpdateState.ERROR) {
                                        Color(0xFFD32F2F)
                                    } else {
                                        Color(0xFF388E3C)
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = uiState.errorMessage,
                                    color = if (uiState.updateState == EditProfileUpdateState.ERROR) {
                                        Color(0xFFD32F2F)
                                    } else {
                                        Color(0xFF388E3C)
                                    },
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Save Button
                    Button(
                        onClick = {
                            viewModel.onSaveClicked(accessToken) {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryGreen,
                            disabledContainerColor = primaryGreen.copy(alpha = 0.6f)
                        ),
                        enabled = uiState.updateState != EditProfileUpdateState.LOADING
                    ) {
                        if (uiState.updateState == EditProfileUpdateState.LOADING) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text(
                            text = if (uiState.updateState == EditProfileUpdateState.LOADING) {
                                "Kaydediliyor..."
                            } else {
                                "Değişiklikleri Kaydet"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun EditProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector? = null,
    placeholder: String = "",
    primaryGreen: Color,
    lightGray: Color,
    grayText: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = grayText,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = icon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            placeholder = { Text(text = placeholder, color = Color(0xFF94A3B8)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = lightGray,
                unfocusedContainerColor = lightGray,
                focusedIndicatorColor = Color(0xFFE2E8F0),
                unfocusedIndicatorColor = Color(0xFFE2E8F0),
                cursorColor = primaryGreen
            ),
            singleLine = true
        )
    }
}

