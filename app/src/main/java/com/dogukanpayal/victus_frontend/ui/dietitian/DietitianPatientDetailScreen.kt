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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogukanpayal.victus_frontend.ui.dietitian.components.CompliancePieChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietitianPatientDetailScreen(
    viewModel: DietitianViewModel,
    token: String,
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
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hata: ${uiState.error}", color = Color.Red, modifier = Modifier.padding(16.dp))
                    Button(onClick = { onNavigateBack() }) {
                        Text("Geri Git")
                    }
                }
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

                // [NEW] Compliance Dashboard
                patient.complianceSummary?.let { compliance ->
                    CompliancePieChart(data = compliance)
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

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Message Button
                    var showMessageSheet by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                    
                    Button(
                        onClick = { showMessageSheet = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = primaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mesaj Gönder", fontWeight = FontWeight.Bold, color = primaryBlue)
                    }

                    // Diet Plan Button
                    Button(
                        onClick = { onCreatePlan(patient.profile.id) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Diyet Yaz", fontWeight = FontWeight.Bold)
                    }
                    
                    if (showMessageSheet) {
                        MessageBottomSheet(
                            onDismiss = { showMessageSheet = false },
                            onSend = { title, msg ->
                                viewModel.sendFeedback(token, patient.profile.id, title, msg)
                                showMessageSheet = false
                            }
                        )
                    }
                }
            }
        } else {
             Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Hasta verisi bulunamadı.")
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageBottomSheet(
    onDismiss: () -> Unit,
    onSend: (String, String) -> Unit
) {
    var title by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var message by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Mesaj Gönder", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Konu") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Mesajınız") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )
            
            Button(
                onClick = { if (title.isNotEmpty() && message.isNotEmpty()) onSend(title, message) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(16.dp),
                enabled = title.isNotEmpty() && message.isNotEmpty()
            ) {
                Text("Gönder", fontWeight = FontWeight.Bold)
            }
        }
    }
}
