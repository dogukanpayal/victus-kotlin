package com.dogukanpayal.victus_frontend.ui.exercise

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExerciseScreen(viewModel: ExerciseViewModel) {
    val primaryGreen = Color(0xFF22C55E)
    val errorRed = Color(0xFFEF4444)
    val overlayDark = Color.Black.copy(alpha = 0.8f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B)) // Temporary dark bg simulating camera
    ) {
        // Background Simulation (Camera / Video area)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Simulated Person performing squat
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏋️‍♂️", fontSize = 200.sp)
            }
            
            // Skeletal Points Simulation
            SkeletalPoint(modifier = Modifier.offset(x = (-40).dp, y = 100.dp), color = primaryGreen)
            SkeletalPoint(modifier = Modifier.offset(x = 40.dp, y = 100.dp), color = primaryGreen)
        }

        // Top HUD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Live Analysis Badge
                LiveBadge(primaryGreen)

                // Score Badges
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ScoreBadge(label = "DOĞRU", score = "85%", color = primaryGreen)
                    ScoreBadge(label = "YANLIŞ", score = "15%", color = errorRed)
                }
            }
        }

        // Center Overlay & Finish Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = overlayDark),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DURUM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hareket Formu Analiz Ediliyor",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Dizlerinizi ayak parmaklarınızın hizasında tutun",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Finish Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .clickable { /* End Workout */ },
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(errorRed)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Antrenmanı Bitir",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }

        // Bottom Controls Over Camera
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp, start = 32.dp, end = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CameraControlIcon(Icons.Default.ThumbUp) // Gallery placeholder
                
                // Shutter Button
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    color = primaryGreen
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }

                CameraControlIcon(Icons.Default.Refresh) // Switch camera
            }
        }
    }
}

@Composable
fun LiveBadge(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "live")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        color = Color.Black.copy(alpha = 0.6f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = alpha))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Canlı Analiz", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ScoreBadge(label: String, score: String, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = score, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun SkeletalPoint(modifier: Modifier, color: Color) {
    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Color.White, CircleShape)
    )
}

@Composable
fun CameraControlIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = Modifier.size(56.dp),
        color = Color.White.copy(alpha = 0.2f),
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}
