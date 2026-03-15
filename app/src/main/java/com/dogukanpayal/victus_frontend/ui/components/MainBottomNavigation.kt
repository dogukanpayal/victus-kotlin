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
            .height(100.dp), // Height to accommodate the protruding button
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            color = backgroundColor,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Items
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

                // Space for Home Button
                Spacer(modifier = Modifier.weight(1f))

                // Right Items
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
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    NavigationItem(
                        label = "Egzersiz",
                        icon = Icons.Default.AccessibilityNew,
                        isSelected = currentScreen == Screen.Exercise,
                        onClick = { onNavigate(Screen.Exercise) },
                        inactiveColor = inactiveGray,
                        activeColor = primaryGreen
                    )
                }
            }
        }

        // Protruding Home Button
        val isHomeSelected = currentScreen == Screen.Home
        val homeButtonBg = if (isHomeSelected) primaryGreen else Color(0xFFE2E8F0)
        val homeIconColor = if (isHomeSelected) Color.White else primaryGreen

        Box(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp) // Border effect
                .clip(CircleShape)
                .background(homeButtonBg)
                .clickable { onNavigate(Screen.Home) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Ana Sayfa",
                tint = homeIconColor,
                modifier = Modifier.size(36.dp)
            )
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
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) activeColor else inactiveColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
