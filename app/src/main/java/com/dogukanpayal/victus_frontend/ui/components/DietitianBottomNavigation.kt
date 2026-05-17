package com.dogukanpayal.victus_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogukanpayal.victus_frontend.Screen

@Composable
fun DietitianBottomNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val primaryBlue = Color(0xFF3B82F6)
    val inactiveGray = Color(0xFF94A3B8)
    
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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DietitianNavItem(
                    label = "Panel",
                    icon = Icons.Default.Dashboard,
                    isSelected = currentScreen == Screen.DietitianDashboard,
                    onClick = { onNavigate(Screen.DietitianDashboard) },
                    activeColor = primaryBlue,
                    inactiveColor = inactiveGray
                )
                DietitianNavItem(
                    label = "Hastalar",
                    icon = Icons.Default.People,
                    isSelected = currentScreen == Screen.DietitianPatients || currentScreen == Screen.DietitianPatientDetail,
                    onClick = { onNavigate(Screen.DietitianPatients) },
                    activeColor = primaryBlue,
                    inactiveColor = inactiveGray
                )
            }
        }
    }
}

@Composable
private fun RowScope.DietitianNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color,
    inactiveColor: Color
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(26.dp)
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
