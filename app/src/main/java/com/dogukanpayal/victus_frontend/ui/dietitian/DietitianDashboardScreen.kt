package com.dogukanpayal.victus_frontend.ui.dietitian

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogukanpayal.victus_frontend.data.model.PatientSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietitianDashboardScreen(
    viewModel: DietitianViewModel,
    userName: String,
    token: String
) {
    val uiState by viewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadPatients(token)
        }
    }

    val primaryBlue = Color(0xFF3B82F6)
    val bgGray = Color(0xFFF8FAFC)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGray)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header
        Text(
            text = "Hoş Geldiniz,",
            fontSize = 16.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                title = "Hastalarım",
                value = uiState.patients.size.toString(),
                icon = Icons.Default.People,
                color = primaryBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Activity Section
        Text(
            text = "Son Aktiviteler",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        val sortedPatients = uiState.patients.sortedWith(
            compareByDescending<PatientSummary> { !it.lastActivity.isNullOrEmpty() }
                .thenByDescending { it.lastActivity ?: "" }
        )

        if (sortedPatients.isEmpty()) {
            Text(
                text = "Henüz kayıtlı hastanız bulunmuyor.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            sortedPatients.forEachIndexed { index, patient ->
                val hasActivity = !patient.lastActivity.isNullOrEmpty()
                ActivityItem(
                    patientName = patient.fullName,
                    action = if (hasActivity) (patient.lastAction ?: "Sistemde aktifti") else "Henüz bir aktivite yok",
                    time = if (hasActivity) formatTimeAgo(patient.lastActivity!!) else "-"
                )
                if (index < sortedPatients.size - 1) Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

fun formatTimeAgo(isoString: String): String {
    return try {
        val instant = java.time.Instant.parse(isoString)
        val now = java.time.Instant.now()
        val duration = java.time.Duration.between(instant, now)
        
        when {
            duration.toMinutes() < 1 -> "Az önce"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} dk önce"
            duration.toHours() < 24 -> "${duration.toHours()} saat önce"
            duration.toDays() < 30 -> "${duration.toDays()} gün önce"
            else -> isoString.take(10) // fallback to YYYY-MM-DD
        }
    } catch (e: Exception) {
        "Yakın zamanda"
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text(text = title, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun ActivityItem(patientName: String, action: String, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(patientName.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = patientName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = action, fontSize = 12.sp, color = Color(0xFF64748B))
            }
            Text(text = time, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
    }
}
