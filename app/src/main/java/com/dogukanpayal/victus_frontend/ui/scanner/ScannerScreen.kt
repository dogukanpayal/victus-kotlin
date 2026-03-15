package com.dogukanpayal.victus_frontend.ui.scanner

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScannerScreen(viewModel: ScannerViewModel) {
    val primaryGreen = Color(0xFF22C55E)
    val lightGreenBg = Color(0xFFF0FDF4)
    val surfaceWhite = Color.White
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // Subtle off-white background
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

        // Scanning Frame
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color.Gray.copy(alpha = 0.1f)) // Placeholder for camera
                .border(2.dp, primaryGreen.copy(alpha = 0.3f), RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder Image Simulation
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
                // Here we would put the 'food_scan_placeholder'
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

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(icon = Icons.Default.Home, label = "Galeri", primaryGreen = primaryGreen)
            
            // Main Shutter Button
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable { /* Take Photo */ },
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
                        imageVector = Icons.Default.PlayArrow, // Camera icon placeholder
                        contentDescription = "Capture",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            ControlButton(icon = Icons.Default.Settings, label = "Flaş", primaryGreen = primaryGreen)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tip Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp), // Padding for bottom nav
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

@Composable
fun ScanningOverlay(color: Color) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // Corners
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
fun ControlButton(icon: ImageVector, label: String, primaryGreen: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .clickable { /* Action */ },
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
