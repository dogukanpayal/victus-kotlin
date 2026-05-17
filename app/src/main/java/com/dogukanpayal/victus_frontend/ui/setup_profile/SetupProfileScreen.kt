package com.dogukanpayal.victus_frontend.ui.setup_profile

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val primaryGreen = Color(0xFF22C55E)
private val darkText = Color(0xFF0F172A)
private val grayText = Color(0xFF64748B)
private val lightGray = Color(0xFFF1F5F9)
private val superLightGreen = Color(0xFFF0FDF4)
private val darkRed = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupProfileScreen(
        viewModel: SetupProfileViewModel,
        onNavigateBack: () -> Unit = {},
        accessToken: String = "",
        email: String = "",
        fullName: String = "",
        onProfileUpdateSuccess: () -> Unit = {}
) {
        val heightCm by viewModel.heightCm.collectAsState()
        val weightKg by viewModel.weightKg.collectAsState()
        val selectedGoal by viewModel.selectedGoal.collectAsState()
        val age by viewModel.age.collectAsState()
        val selectedSex by viewModel.selectedSex.collectAsState()
        val updateState by viewModel.updateState.collectAsState()
        val errorMessage by viewModel.errorMessage.collectAsState()
        val kvkkConsentApproved by viewModel.kvkkConsentApproved.collectAsState()


        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text(
                                                text = "Profilini Tamamla",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = darkText,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                        )
                                },
                                navigationIcon = {
                                        IconButton(
                                                onClick = onNavigateBack,
                                                enabled = updateState != ProfileUpdateState.LOADING
                                        ) {
                                                Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Back",
                                                        tint = darkText
                                                )
                                        }
                                },
                                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.White
                                        )
                        )
                },
                containerColor = Color.White
        ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .padding(horizontal = 24.dp)
                                                .verticalScroll(rememberScrollState())
                        ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                        text = "Hadi seni tanıyalım",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = darkText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                        text =
                                                "Lütfen fiziksel bilgilerini ve hedefini gir. Bu bilgiler programını özelleştirmemize yardımcı olacak.",
                                        fontSize = 14.sp,
                                        color = grayText,
                                        lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Height Slider
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                ) {
                                        Text(
                                                text = "Boy (cm)",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = darkText
                                        )
                                        Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                        text = "${heightCm.roundToInt()}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 24.sp,
                                                        color = primaryGreen
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                        text = "cm",
                                                        color = grayText,
                                                        fontSize = 14.sp,
                                                        modifier = Modifier.padding(bottom = 2.dp)
                                                )
                                        }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Slider(
                                        value = heightCm,
                                        onValueChange = viewModel::onHeightChanged,
                                        valueRange = 100f..250f,
                                        enabled = updateState != ProfileUpdateState.LOADING,
                                        colors =
                                                SliderDefaults.colors(
                                                        thumbColor = Color(0xFF22C55E),
                                                        activeTrackColor = Color(0xFF22C55E),
                                                        inactiveTrackColor = Color(0xFFE5E7EB)
                                                ),
                                        track = { sliderState ->
                                                SliderDefaults.Track(
                                                        modifier = Modifier.height(4.dp),
                                                        sliderState = sliderState,
                                                        colors = SliderDefaults.colors(
                                                                activeTrackColor = Color(0xFF22C55E),
                                                                inactiveTrackColor = Color(0xFFE5E7EB)
                                                        )
                                                )
                                        },
                                        thumb = {
                                                Box(
                                                        modifier = Modifier
                                                                .size(18.dp)
                                                                .background(color = Color(0xFF22C55E), shape = CircleShape)
                                                )
                                        }
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Weight Slider
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                ) {
                                        Text(
                                                text = "Kilo (kg)",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = darkText
                                        )
                                        Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                        text = "${weightKg.roundToInt()}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 24.sp,
                                                        color = primaryGreen
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                        text = "kg",
                                                        color = grayText,
                                                        fontSize = 14.sp,
                                                        modifier = Modifier.padding(bottom = 2.dp)
                                                )
                                        }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Slider(
                                        value = weightKg,
                                        onValueChange = viewModel::onWeightChanged,
                                        valueRange = 30f..200f,
                                        enabled = updateState != ProfileUpdateState.LOADING,
                                        colors =
                                                SliderDefaults.colors(
                                                        thumbColor = Color(0xFF22C55E),
                                                        activeTrackColor = Color(0xFF22C55E),
                                                        inactiveTrackColor = Color(0xFFE5E7EB)
                                                ),
                                        track = { sliderState ->
                                                SliderDefaults.Track(
                                                        modifier = Modifier.height(4.dp),
                                                        sliderState = sliderState,
                                                        colors = SliderDefaults.colors(
                                                                activeTrackColor = Color(0xFF22C55E),
                                                                inactiveTrackColor = Color(0xFFE5E7EB)
                                                        )
                                                )
                                        },
                                        thumb = {
                                                Box(
                                                        modifier = Modifier
                                                                .size(18.dp)
                                                                .background(color = Color(0xFF22C55E), shape = CircleShape)
                                                )
                                        }
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Age Input
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = "Yaş",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = darkText
                                        )
                                        Row(
                                                modifier =
                                                        Modifier.border(
                                                                        1.dp,
                                                                        lightGray,
                                                                        RoundedCornerShape(8.dp)
                                                                )
                                                                .padding(4.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                IconButton(
                                                        onClick = {
                                                                viewModel.onAgeChanged(
                                                                        (age - 1).coerceAtLeast(1)
                                                                )
                                                        },
                                                        modifier = Modifier.size(32.dp),
                                                        enabled =
                                                                updateState !=
                                                                        ProfileUpdateState
                                                                                .LOADING && age > 1
                                                ) {
                                                        Text(
                                                                text = "−",
                                                                fontSize = 20.sp,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                                Text(
                                                        text = "$age",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp,
                                                        color = primaryGreen,
                                                        modifier = Modifier.width(40.dp),
                                                        textAlign = TextAlign.Center
                                                )
                                                IconButton(
                                                        onClick = {
                                                                viewModel.onAgeChanged(
                                                                        (age + 1).coerceAtMost(120)
                                                                )
                                                        },
                                                        modifier = Modifier.size(32.dp),
                                                        enabled =
                                                                updateState !=
                                                                        ProfileUpdateState
                                                                                .LOADING &&
                                                                        age < 120
                                                ) {
                                                        Icon(
                                                                Icons.Default.Add,
                                                                contentDescription = "Yaşı artır",
                                                                modifier = Modifier.size(16.dp)
                                                        )
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Sex Selection
                                Text(
                                        text = "Cinsiyet",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = darkText
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        SexButton(
                                                text = "Erkek",
                                                isSelected = selectedSex == "male",
                                                onClick = { viewModel.onSexSelected("male") },
                                                enabled = updateState != ProfileUpdateState.LOADING,
                                                primaryGreen = primaryGreen,
                                                lightGray = lightGray,
                                                superLightGreen = superLightGreen,
                                                darkText = darkText,
                                                modifier = Modifier.weight(1f)
                                        )
                                        SexButton(
                                                text = "Kadın",
                                                isSelected = selectedSex == "female",
                                                onClick = { viewModel.onSexSelected("female") },
                                                enabled = updateState != ProfileUpdateState.LOADING,
                                                primaryGreen = primaryGreen,
                                                lightGray = lightGray,
                                                superLightGreen = superLightGreen,
                                                darkText = darkText,
                                                modifier = Modifier.weight(1f)
                                        )
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Text(
                                        text = "Hedefin Nedir?",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = darkText
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Goal Cards
                                GoalCard(
                                        title = "Kilo Ver",
                                        subtitle = "Yağ yakımı ve formunu koru",
                                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                                        isSelected = selectedGoal == Goal.LOSE_WEIGHT,
                                        onClick = { viewModel.onGoalSelected(Goal.LOSE_WEIGHT) },
                                        enabled = updateState != ProfileUpdateState.LOADING,
                                        primaryGreen = primaryGreen,
                                        lightGray = lightGray,
                                        superLightGreen = superLightGreen,
                                        darkText = darkText,
                                        grayText = grayText
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                GoalCard(
                                        title = "Kas Kütlesi Kazan",
                                        subtitle = "Güçlen ve hacim kazan",
                                        icon = Icons.Default.FitnessCenter,
                                        isSelected = selectedGoal == Goal.GAIN_MUSCLE,
                                        onClick = { viewModel.onGoalSelected(Goal.GAIN_MUSCLE) },
                                        enabled = updateState != ProfileUpdateState.LOADING,
                                        primaryGreen = primaryGreen,
                                        lightGray = lightGray,
                                        superLightGreen = superLightGreen,
                                        darkText = darkText,
                                        grayText = grayText
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                GoalCard(
                                        title = "Formda Kal",
                                        subtitle = "Sağlıklı yaşam ve enerji",
                                        icon = Icons.Default.Favorite,
                                        isSelected = selectedGoal == Goal.STAY_IN_SHAPE,
                                        onClick = { viewModel.onGoalSelected(Goal.STAY_IN_SHAPE) },
                                        enabled = updateState != ProfileUpdateState.LOADING,
                                        primaryGreen = primaryGreen,
                                        lightGray = lightGray,
                                        superLightGreen = superLightGreen,
                                        darkText = darkText,
                                        grayText = grayText
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Error Message
                                if (errorMessage.isNotEmpty()) {
                                        Surface(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .clip(RoundedCornerShape(12.dp)),
                                                color = Color(0xFFFFEBEE)
                                        ) {
                                                Row(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(12.dp),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(8.dp),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                Icons.Default.Info,
                                                                contentDescription = "Error",
                                                                tint = darkRed,
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                        Text(
                                                                text = errorMessage,
                                                                fontSize = 12.sp,
                                                                color = darkRed,
                                                                modifier = Modifier.weight(1f)
                                                        )
                                                        IconButton(
                                                                onClick = viewModel::clearError,
                                                                modifier = Modifier.size(24.dp)
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.Close,
                                                                        contentDescription =
                                                                                "Close",
                                                                        tint = darkRed,
                                                                        modifier =
                                                                                Modifier.size(16.dp)
                                                                )
                                                        }
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                }

                                // KVKK/GDPR Consent Box
                                Row(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(if (kvkkConsentApproved) superLightGreen else lightGray.copy(alpha = 0.5f))
                                                .clickable(enabled = updateState != ProfileUpdateState.LOADING) {
                                                        viewModel.onKvkkConsentChanged(!kvkkConsentApproved)
                                                }
                                                .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Checkbox(
                                                checked = kvkkConsentApproved,
                                                onCheckedChange = { viewModel.onKvkkConsentChanged(it) },
                                                enabled = updateState != ProfileUpdateState.LOADING,
                                                colors = CheckboxDefaults.colors(checkedColor = primaryGreen)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                        text = "KVKK ve Gizlilik Sözleşmesi",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = darkText
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                        text = "Diyetisyenimin sağlık ve fiziksel verilerimi görmesine ve takip etmesine yasal olarak izin veriyorum.",
                                                        fontSize = 12.sp,
                                                        color = grayText,
                                                        lineHeight = 16.sp
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Continue Button
                                Button(
                                        onClick = {
                                                viewModel.onContinueClicked(accessToken, email, fullName)
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(28.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = primaryGreen
                                                ),
                                        enabled = updateState != ProfileUpdateState.LOADING && kvkkConsentApproved
                                ) {
                                        if (updateState == ProfileUpdateState.LOADING) {
                                                CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        color = Color.White,
                                                        strokeWidth = 2.dp
                                                )
                                        } else {
                                                Row(
                                                        horizontalArrangement = Arrangement.Center,
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                Text(
                                                        text = "Devam Et",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(
                                                        imageVector =
                                                                Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = "Continue",
                                                        tint = Color.White
                                                )
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Success Dialog
                        if (updateState == ProfileUpdateState.SUCCESS) {
                                AlertDialog(
                                        onDismissRequest = { onProfileUpdateSuccess() },
                                        confirmButton = {
                                                Button(
                                                        onClick = { onProfileUpdateSuccess() },
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                primaryGreen
                                                                )
                                                ) { Text("Devam Et", color = Color.White) }
                                        },
                                        title = {
                                                Text(
                                                        "Başarılı!",
                                                        color = darkText,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        },
                                        text = {
                                                Text(
                                                        "Profilin başarıyla güncellendi.",
                                                        color = grayText
                                                )
                                        },
                                        containerColor = Color.White
                                )
                        }
                }
        }
}

@Composable
fun SexButton(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        primaryGreen: Color,
        lightGray: Color,
        superLightGreen: Color,
        darkText: Color
) {
        val borderColor = if (isSelected) primaryGreen else lightGray
        val backgroundColor = if (isSelected) superLightGreen else Color.White

        Button(
                onClick = onClick,
                modifier =
                        modifier.height(44.dp)
                                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                                .background(backgroundColor),
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor = backgroundColor,
                                disabledContainerColor = backgroundColor
                        ),
                enabled = enabled,
                shape = RoundedCornerShape(12.dp)
        ) {
                Text(
                        text = text,
                        color = if (isSelected) primaryGreen else darkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                )
        }
}

@Composable
fun GoalCard(
        title: String,
        subtitle: String,
        icon: ImageVector,
        isSelected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean = true,
        primaryGreen: Color,
        lightGray: Color,
        superLightGreen: Color,
        darkText: Color,
        grayText: Color
) {
        val borderColor = if (isSelected) primaryGreen else lightGray
        val backgroundColor = if (isSelected) superLightGreen else Color.White
        val iconBackgroundColor = if (isSelected) primaryGreen else lightGray
        val iconColor = if (isSelected) Color.White else grayText

        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .border(
                                        width = 2.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(24.dp)
                                )
                                .background(
                                        color = backgroundColor,
                                        shape = RoundedCornerShape(24.dp)
                                )
                                .clickable(enabled = enabled, onClick = onClick)
                                .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Box(
                        modifier =
                                Modifier.size(48.dp)
                                        .clip(CircleShape)
                                        .background(iconBackgroundColor),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = iconColor,
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
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = subtitle, fontSize = 14.sp, color = grayText)
                }

                if (isSelected) {
                        Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = primaryGreen,
                                modifier = Modifier.size(24.dp)
                        )
                } else {
                        Spacer(modifier = Modifier.size(24.dp))
                }
        }
}
