package com.dogukanpayal.victus_frontend.ui.dietitian_link

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkDietitianScreen(
    viewModel: LinkDietitianViewModel,
    token: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var code by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        when (uiState) {
            is LinkDietitianState.Success -> {
                snackbarHostState.showSnackbar("Diyetisyen başarıyla bağlandı!")
                viewModel.resetState()
                onNavigateToHome()
            }
            is LinkDietitianState.Error -> {
                // Show error in a snackbar or text below input
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diyetisyene Bağlan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E293B)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Diyetisyeninizin size verdiği VIC-XXXX formatındaki kodu girerek profilinizi eşleştirebilirsiniz.",
                fontSize = 16.sp,
                color = Color(0xFF475569),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = code,
                onValueChange = { 
                    if (it.length <= 10) {
                        code = it.uppercase()
                    }
                },
                label = { Text("Diyetisyen Kodu") },
                singleLine = true,
                isError = uiState is LinkDietitianState.Error,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF22C55E),
                    focusedLabelColor = Color(0xFF22C55E)
                )
            )
            
            if (uiState is LinkDietitianState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (uiState as LinkDietitianState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { viewModel.linkDietitian(token, code) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState != LinkDietitianState.Loading && code.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (uiState == LinkDietitianState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Diyetisyene Bağlan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
