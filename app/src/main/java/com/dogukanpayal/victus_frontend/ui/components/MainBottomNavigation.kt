package com.dogukanpayal.victus_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogukanpayal.victus_frontend.Screen

@Composable
fun MainBottomNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val primaryGreen = Color(0xFF22C55E)
    val inactiveGray = Color(0xFF94A3B8)
    val backgroundColor = Color.White
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Scanner
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    NavigationItem(
                        label = "Tarayıcı",
                        icon = Icons.Default.CameraAlt,
                        isSelected = currentScreen == Screen.Scanner,
                        onClick = { onNavigate(Screen.Scanner) },
                        inactiveColor = inactiveGray,
                        activeColor = primaryGreen
                    )
                }
                
                // 2. Diet
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    NavigationItem(
                        label = "Diyet",
                        icon = Icons.Default.Restaurant,
                        isSelected = currentScreen == Screen.Diet,
                        onClick = { onNavigate(Screen.Diet) },
                        inactiveColor = inactiveGray,
                        activeColor = primaryGreen
                    )
                }

                // 3. Home (Beautiful highlighted circle inside the bar)
                Box(modifier = Modifier.weight(1.2f), contentAlignment = Alignment.Center) {
                    val isHomeSelected = currentScreen == Screen.Home
                    val homeBg = if (isHomeSelected) primaryGreen else Color(0xFFF1F5F9)
                    val homeColor = if (isHomeSelected) Color.White else primaryGreen
                    
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(homeBg)
                            .clickable { onNavigate(Screen.Home) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Ana Sayfa",
                            tint = homeColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // 4. Workout
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    NavigationItem(
                        label = "Antrenman",
                        icon = Icons.Default.FitnessCenter,
                        isSelected = currentScreen == Screen.Workout,
                        onClick = { onNavigate(Screen.Workout) },
                        inactiveColor = inactiveGray,
                        activeColor = primaryGreen
                    )
                }

                // 5. Analysis
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    NavigationItem(
                        label = "Analiz",
                        icon = Icons.Default.AccessibilityNew,
                        isSelected = currentScreen == Screen.Exercise,
                        onClick = { onNavigate(Screen.Exercise) },
                        inactiveColor = inactiveGray,
                        activeColor = primaryGreen
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    inactiveColor: Color,
    activeColor: Color
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) activeColor else inactiveColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
