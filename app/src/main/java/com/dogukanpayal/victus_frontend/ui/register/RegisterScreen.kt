package com.dogukanpayal.victus_frontend.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val primaryGreen = Color(0xFF22C55E)
private val lightGray = Color(0xFFF1F5F9)
private val darkText = Color(0xFF0F172A)
private val grayText = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit = {}
) {
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val termsAccepted by viewModel.termsAccepted.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = darkText,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Hesap Oluştur",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Headers
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Hesap Oluştur",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = darkText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sağlıklı yaşam yolculuğuna bugün başlayın",
                fontSize = 16.sp,
                color = grayText
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Full Name Field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Tam Ad",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = darkText
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = viewModel::onFullNameChanged,
                placeholder = { Text("Adınızı ve soyadınızı girin", color = grayText) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Person Icon",
                        tint = grayText
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = lightGray,
                    focusedTextColor = darkText,
                    unfocusedTextColor = darkText
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Email Field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "E-posta",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = darkText
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChanged,
                placeholder = { Text("E-posta adresinizi girin", color = grayText) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = grayText
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = lightGray,
                    focusedTextColor = darkText,
                    unfocusedTextColor = darkText
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Password Field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Şifre",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = darkText
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChanged,
                placeholder = { Text("Güçlü bir şifre oluşturun", color = grayText) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon",
                        tint = grayText
                    )
                },
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            imageVector = Icons.Default.Info, // Placeholder for Visibility
                            contentDescription = "Toggle Password Visibility",
                            tint = grayText
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = lightGray,
                    focusedTextColor = darkText,
                    unfocusedTextColor = darkText
                ),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Terms Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = viewModel::onTermsAcceptedChanged,
                colors = CheckboxDefaults.colors(
                    checkedColor = primaryGreen,
                    uncheckedColor = grayText
                )
            )
            Text(
                text = "Şartları ve Koşulları Kabul Ediyorum",
                fontSize = 14.sp,
                color = grayText
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Register Button
        Button(
            onClick = viewModel::onRegisterClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kayıt Ol",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Register Arrow"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // VEYA Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = lightGray)
            Text(
                text = "veya şununla devam et",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = grayText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = lightGray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
             modifier = Modifier.fillMaxWidth(),
             horizontalArrangement = Arrangement.spacedBy(16.dp),
             verticalAlignment = Alignment.CenterVertically
        ) {
             // Google Button
            OutlinedButton(
                onClick = viewModel::onGoogleRegisterClicked,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = darkText),
                border = BorderStroke(1.dp, lightGray)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder for Google Icon
                    Box(modifier = Modifier.size(20.dp).background(Color.Black, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                     Text(
                        text = "Google",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                     )
                }
            }

            // Apple Button
            OutlinedButton(
                onClick = viewModel::onAppleRegisterClicked,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = darkText),
                border = BorderStroke(1.dp, lightGray)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder for Apple Icon
                    Icon(
                        imageVector = Icons.Default.Person, // Apple icon is not standard, using placeholder
                        contentDescription = "Apple Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                     Text(
                        text = "Apple",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                     )
                }
            }
        }
       
        Spacer(modifier = Modifier.height(48.dp))

        // Login Text
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = grayText)) {
                    append("Zaten hesabınız var mı? ")
                }
                withStyle(style = SpanStyle(color = primaryGreen, fontWeight = FontWeight.Bold)) {
                    append("Giriş yapın")
                }
            },
            fontSize = 14.sp,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
