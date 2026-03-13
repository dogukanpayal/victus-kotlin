package com.dogukanpayal.victus_frontend.ui.profile

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogukanpayal.victus_frontend.ui.setup_profile.Goal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = EditProfileViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val primaryGreen = Color(0xFF22C55E)
    val darkText = Color(0xFF0F172A)
    val grayText = Color(0xFF64748B)
    val lightGray = Color(0xFFF1F5F9)
    val superLightGreen = Color(0xFFF0FDF4)
    val mediumGray = Color(0xFF94A3B8)

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
                            imageVector = Icons.Default.ArrowBack,
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
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Background Profile Icon
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(width = 4.dp, color = superLightGreen, shape = CircleShape)
                        .clip(CircleShape)
                        .background(lightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(80.dp),
                        tint = mediumGray
                    )
                }
                
                // Camera Icon Overlay
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(primaryGreen)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, // Using Add as camera icon proxy
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "FOTOĞRAFI DEĞİŞTİR",
                color = primaryGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { viewModel.onChangePhotoClicked() }
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
                    darkText = darkText,
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
                    darkText = darkText,
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
                            darkText = darkText,
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
                            darkText = darkText,
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
                        .background(lightGray.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                        .border(1.dp, lightGray, RoundedCornerShape(28.dp))
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
                                imageVector = Icons.Default.Info, // Placeholder for target icon
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
                                    Text(
                                        when(goal) {
                                            Goal.LOSE_WEIGHT -> "Kilo Ver"
                                            Goal.GAIN_MUSCLE -> "Kas Kütlesi Artırmak"
                                            Goal.STAY_IN_SHAPE -> "Formda Kal"
                                        }
                                    )
                                },
                                onClick = {
                                    viewModel.onGoalChanged(goal)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Password Change Item
                EditProfileSettingItem(
                    icon = Icons.Default.Lock,
                    title = "Şifre",
                    subtitle = "Güvenlik ayarlarını yönet",
                    actionText = "Şifre Değiştir",
                    onActionClick = viewModel::onChangePasswordClicked,
                    primaryGreen = primaryGreen,
                    lightGray = lightGray,
                    darkText = darkText,
                    grayText = grayText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Workout Reminders Item
                EditProfileReminderItem(
                    title = "Antrenman Hatırlatıcıları",
                    subtitle = "Günlük bildirimler al",
                    checked = uiState.workoutRemindersEnabled,
                    onCheckedChange = viewModel::onWorkoutRemindersToggled,
                    primaryGreen = primaryGreen,
                    lightGray = lightGray,
                    darkText = darkText,
                    grayText = grayText
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                // Save Button
                Button(
                    onClick = viewModel::onSaveClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                ) {
                    Text(
                        text = "Değişiklikleri Kaydet",
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

@Composable
fun EditProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector? = null,
    placeholder: String = "",
    primaryGreen: Color,
    lightGray: Color,
    darkText: Color,
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
            shape = RoundedCornerShape(28.dp),
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
                focusedContainerColor = lightGray.copy(alpha = 0.5f),
                unfocusedContainerColor = lightGray.copy(alpha = 0.5f),
                focusedIndicatorColor = lightGray,
                unfocusedIndicatorColor = lightGray,
                cursorColor = primaryGreen
            ),
            singleLine = true
        )
    }
}

@Composable
fun EditProfileSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionText: String,
    onActionClick: () -> Unit,
    primaryGreen: Color,
    lightGray: Color,
    darkText: Color,
    grayText: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(lightGray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .border(1.dp, lightGray, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(lightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = darkText
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = grayText
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onActionClick() }
        ) {
            Text(
                text = actionText,
                color = primaryGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EditProfileReminderItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryGreen: Color,
    lightGray: Color,
    darkText: Color,
    grayText: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(lightGray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .border(1.dp, lightGray, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = darkText
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = grayText
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = lightGray
            )
        )
    }
}
