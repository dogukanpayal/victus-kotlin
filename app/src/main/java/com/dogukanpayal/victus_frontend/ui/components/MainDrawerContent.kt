package com.dogukanpayal.victus_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun MainDrawerContent(
    userName: String,
    userEmail: String,
    userAvatarUrl: String? = null,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val primaryGreen = Color(0xFF22C55E)
    val lightGreenBg = Color(0xFFDCFCE7)
    val inactiveGray = Color(0xFF64748B)
    val logoutRedBg = Color(0xFFFEF2F2)
    val logoutRedText = Color(0xFFEF4444)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.White)
            .padding(24.dp)
    ) {
        // User Profile Header
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(lightGreenBg),
            contentAlignment = Alignment.Center
        ) {
            if (!userAvatarUrl.isNullOrEmpty()) {
                // Show uploaded avatar image
                AsyncImage(
                    model = userAvatarUrl,
                    contentDescription = "Profil Fotoğrafı",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    onSuccess = {},
                    onError = {},
                    onLoading = {}
                )
                // Fallback: Show Person icon if image fails to load
                // This is handled by checking if userAvatarUrl is empty
            }
            
            // Show placeholder person icon if no avatar URL
            if (userAvatarUrl.isNullOrEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil Fotoğrafı",
                    tint = primaryGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = userName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Text(
            text = userEmail,
            fontSize = 14.sp,
            color = inactiveGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Menu Items
        DrawerMenuItem(
            label = "Bildirimler",
            icon = Icons.Default.Notifications,
            iconColor = primaryGreen,
            containerColor = lightGreenBg,
            onClick = onNotificationsClick
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        DrawerMenuItem(
            label = "Profil ve Ayarlar",
            icon = Icons.Default.Settings,
            iconColor = inactiveGray,
            containerColor = Color.Transparent,
            onClick = onSettingsClick
        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = logoutRedBg,
                contentColor = logoutRedText
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Oturumu Kapat",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@Composable
fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    iconColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                color = if (containerColor == Color.Transparent) Color(0xFF475569) else iconColor,
                fontWeight = if (containerColor == Color.Transparent) FontWeight.Medium else FontWeight.SemiBold
            )
        }
    }
}

// deneme
