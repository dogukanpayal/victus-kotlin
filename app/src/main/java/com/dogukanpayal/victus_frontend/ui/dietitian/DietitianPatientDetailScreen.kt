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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietitianPatientDetailScreen(
    viewModel: DietitianViewModel,
    onNavigateBack: () -> Unit,
    onCreatePlan: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val patient = uiState.selectedPatient
    val primaryBlue = Color(0xFF3B82F6)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hasta Detayı", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryBlue)
            }
        } else if (patient != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Patient Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(64.dp).background(Color(0xFFEFF6FF), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(patient.profile.fullName ?: "İsimsiz Hasta", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(patient.profile.email, fontSize = 14.sp, color = Color(0xFF64748B))
                        }
                    }
                }

                // Stats Section
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricBox(label = "Kilo", value = "${patient.profile.weightKg} kg", modifier = Modifier.weight(1f))
                    MetricBox(label = "Boy", value = "${patient.profile.heightCm.toInt()} cm", modifier = Modifier.weight(1f))
                    MetricBox(label = "Yaş", value = "${patient.profile.age}", modifier = Modifier.weight(1f))
                }

                // [NEW] Active Diet Plan Summary
                SummaryCard(
                    title = "Aktif Diyet Planı",
                    content = patient.activeDietPlanSummary ?: "Diyet planı bulunamadı.",
                    icon = Icons.Default.Restaurant,
                    color = Color(0xFF22C55E)
                )

                // [NEW] Exercise Status
                SummaryCard(
                    title = "Egzersiz Durumu",
                    content = patient.recentWorkoutSummary ?: "Henüz antrenman kaydı yok.",
                    icon = Icons.Default.FitnessCenter,
                    color = Color(0xFFEAB308)
                )

                // Action Button
                Button(
                    onClick = { onCreatePlan(patient.profile.id) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Yeni Diyet Planı Oluştur", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, content: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = content, fontSize = 14.sp, color = Color(0xFF64748B), lineHeight = 20.sp)
        }
    }
}

@Composable
fun MetricBox(label: String, value: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
    }
}
