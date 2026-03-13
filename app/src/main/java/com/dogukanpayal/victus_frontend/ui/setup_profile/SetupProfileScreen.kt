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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupProfileScreen(
    viewModel: SetupProfileViewModel = SetupProfileViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val heightCm by viewModel.heightCm.collectAsState()
    val weightKg by viewModel.weightKg.collectAsState()
    val selectedGoal by viewModel.selectedGoal.collectAsState()

    val primaryGreen = Color(0xFF22C55E)
    val darkText = Color(0xFF0F172A)
    val grayText = Color(0xFF64748B)
    val lightGray = Color(0xFFF1F5F9)
    val superLightGreen = Color(0xFFF0FDF4)

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
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = darkText
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp)) // To center the title properly
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
                text = "Lütfen fiziksel bilgilerini ve hedefini gir. Bu bilgiler programını özelleştirmemize yardımcı olacak.",
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
                Text(text = "Boy (cm)", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = darkText)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "${heightCm.roundToInt()}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = primaryGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "cm", color = grayText, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = heightCm,
                onValueChange = viewModel::onHeightChanged,
                valueRange = 100f..250f,
                colors = SliderDefaults.colors(
                    thumbColor = primaryGreen,
                    activeTrackColor = primaryGreen,
                    inactiveTrackColor = lightGray
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "100 cm", fontSize = 12.sp, color = grayText)
                Text(text = "250 cm", fontSize = 12.sp, color = grayText)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weight Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = "Kilo (kg)", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = darkText)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "${weightKg.roundToInt()}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = primaryGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "kg", color = grayText, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = weightKg,
                onValueChange = viewModel::onWeightChanged,
                valueRange = 30f..200f,
                colors = SliderDefaults.colors(
                    thumbColor = primaryGreen,
                    activeTrackColor = primaryGreen,
                    inactiveTrackColor = lightGray
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "30 kg", fontSize = 12.sp, color = grayText)
                Text(text = "200 kg", fontSize = 12.sp, color = grayText)
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
                icon = Icons.Default.ArrowDropDown,
                isSelected = selectedGoal == Goal.LOSE_WEIGHT,
                onClick = { viewModel.onGoalSelected(Goal.LOSE_WEIGHT) },
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
                icon = Icons.Default.Add,
                isSelected = selectedGoal == Goal.GAIN_MUSCLE,
                onClick = { viewModel.onGoalSelected(Goal.GAIN_MUSCLE) },
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
                primaryGreen = primaryGreen,
                lightGray = lightGray,
                superLightGreen = superLightGreen,
                darkText = darkText,
                grayText = grayText
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            Button(
                onClick = viewModel::onContinueClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Devam Et",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Continue",
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GoalCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
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
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(24.dp))
            .background(color = backgroundColor, shape = RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
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
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = grayText
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = primaryGreen,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp)) // Maintain alignment when not selected
        }
    }
}
