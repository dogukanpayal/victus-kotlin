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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dogukanpayal.victus_frontend.ui.setup_profile.Goal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    accessToken: String = "",
    onNavigateBack: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load user profile when screen is first displayed
    androidx.compose.runtime.LaunchedEffect(accessToken) {
        if (accessToken.isNotEmpty()) {
            viewModel.loadUserProfile(accessToken)
        }
    }

    // Debug logging
    androidx.compose.runtime.LaunchedEffect(Unit) {
        android.util.Log.d("ProfileScreen", "AccessToken: ${if (accessToken.isNotEmpty()) "Geçildi (${accessToken.take(20)}...)" else "BOŞ!"}")
        android.util.Log.d("ProfileScreen", "Current uiState - Height: ${uiState.heightCm}, Weight: ${uiState.weightKg}")
    }

    val primaryGreen = Color(0xFF22C55E)
    val darkText = Color(0xFF0F172A)
    val grayText = Color(0xFF64748B)
    val lightGray = Color(0xFFF1F5F9)
    val superLightGreen = Color(0xFFF0FDF4)
    val lightRed = Color(0xFFFFEBEB)
    val primaryRed = Color(0xFFEF4444)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil ve Ayarlar",
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
                    IconButton(onClick = { /* No function */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = darkText
                        )
                    }
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

            // Profile Image with Border
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(lightGray),
                contentAlignment = Alignment.Center
            ) {
                if (!uiState.avatarUrl.isNullOrEmpty()) {
                    // Show uploaded avatar URL
                    AsyncImage(
                        model = uiState.avatarUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        onError = {}
                    )
                    // If AsyncImage fails to load, the icon below will be shown
                }
                
                // Always show placeholder icon if no avatar URL
                if (uiState.avatarUrl.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(lightGray),
                        tint = grayText
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = uiState.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = darkText
            )
            Text(
                text = uiState.email,
                fontSize = 14.sp,
                color = grayText
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateToEditProfile,
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Profili Düzenle", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Account Info Section
            SettingsSectionTitle(title = "HESAP BİLGİLERİ")
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .border(width = 1.dp, color = lightGray, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                SettingsItem(
                    icon = Icons.Default.Straighten,
                    title = "Boy",
                    value = "${uiState.heightCm} cm",
                    iconBackground = superLightGreen,
                    iconColor = primaryGreen
                )
                HorizontalDivider(color = lightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    icon = Icons.Default.FitnessCenter,
                    title = "Kilo",
                    value = "${uiState.weightKg} kg",
                    iconBackground = superLightGreen,
                    iconColor = primaryGreen
                )
                HorizontalDivider(color = lightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    icon = Icons.Default.TrackChanges,
                    title = "Fitness Hedefi",
                    value = when(uiState.fitnessGoal) {
                        Goal.LOSE_WEIGHT -> "Kilo Ver"
                        Goal.GAIN_MUSCLE -> "Kas Kütlesi Kazan"
                        Goal.STAY_IN_SHAPE -> "Formda Kal"
                    },
                    iconBackground = superLightGreen,
                    iconColor = primaryGreen
                )
                HorizontalDivider(color = lightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    icon = Icons.Default.Favorite,
                    title = "BMR",
                    value = "${uiState.bmr.toInt()} kcal",
                    iconBackground = superLightGreen,
                    iconColor = primaryGreen
                )
                HorizontalDivider(color = lightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    icon = Icons.Default.Restaurant,
                    title = "Günlük Kalori",
                    value = "${uiState.dailyCalories.toInt()} kcal",
                    iconBackground = superLightGreen,
                    iconColor = primaryGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Settings Section
            SettingsSectionTitle(title = "UYGULAMA AYARLARI")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .border(width = 1.dp, color = lightGray, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Bildirimler",
                    iconBackground = lightGray,
                    iconColor = darkText
                )
                HorizontalDivider(color = lightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Gizlilik ve Güvenlik",
                    iconBackground = lightGray,
                    iconColor = darkText
                )
                HorizontalDivider(color = lightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Yardım Merkezi",
                    iconBackground = lightGray,
                    iconColor = darkText
                )
                HorizontalDivider(color = lightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                
                // Geliştirici Modu: Diyetisyen Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Diyetisyen Modu",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = darkText
                        )
                        Text(
                            text = "Test amaçlı arayüz değişimi",
                            fontSize = 12.sp,
                            color = grayText
                        )
                    }
                    Switch(
                        checked = uiState.role == "dietitian",
                        onCheckedChange = { viewModel.onRoleToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF3B82F6)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, lightRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryRed)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Oturumu Kapat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "VERSİYON ${uiState.version}",
                fontSize = 10.sp,
                color = grayText,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        letterSpacing = 0.5.sp
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    iconBackground: Color,
    iconColor: Color,
    darkText: Color = Color(0xFF0F172A)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* No function */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Modern circular icon background
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = darkText
            )
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}
