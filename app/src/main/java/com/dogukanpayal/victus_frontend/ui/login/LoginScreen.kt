package com.dogukanpayal.victus_frontend.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = LoginViewModel(),
    onNavigateToRegister: () -> Unit = {},
    onNavigateToSetupProfile: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {}
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()

    val primaryGreen = Color(0xFF22C55E)
    val lightGray = Color(0xFFF1F5F9)
    val darkText = Color(0xFF0F172A)
    val grayText = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Top Row: Logo and Help Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(primaryGreen),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for barbell icon
                Icon(
                    imageVector = Icons.Default.PlayArrow, // Using PlayArrow as placeholder if FitnessCenter is not standard
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Help",
                tint = grayText,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Titles
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Hoş Geldiniz",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = darkText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Devam etmek için giriş yapın",
                fontSize = 16.sp,
                color = grayText
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

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
                placeholder = { Text("eposta@example.com", color = grayText) },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Şifre",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = darkText
                )
                Text(
                    text = "Şifremi Unuttum",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryGreen,
                    modifier = Modifier.clickable { /* Handle click */ }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChanged,
                placeholder = { Text("••••••••", color = grayText) },
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

        // Remember Me Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = viewModel::onRememberMeChanged,
                colors = CheckboxDefaults.colors(
                    checkedColor = primaryGreen,
                    uncheckedColor = grayText
                )
            )
            Text(
                text = "Beni Hatırla",
                fontSize = 14.sp,
                color = darkText
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = viewModel::onLoginClicked,
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
                    text = "Giriş Yap",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Login Arrow"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // VEYA Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f), color = lightGray)
            Text(
                text = "VEYA",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = grayText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Divider(modifier = Modifier.weight(1f), color = lightGray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Google Button
        OutlinedButton(
            onClick = viewModel::onGoogleLoginClicked,
            modifier = Modifier
                .fillMaxWidth()
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
                Box(modifier = Modifier.size(24.dp).background(Color.Red, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Google ile Devam Et",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apple Button
        Button(
            onClick = viewModel::onAppleLoginClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for Apple Icon
                Icon(
                    imageVector = Icons.Default.Person, // Apple icon is not standard, using placeholder
                    contentDescription = "Apple Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Apple ile Devam Et",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Sign Up Text
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = grayText)) {
                    append("Hesabınız yok mu? ")
                }
                withStyle(style = SpanStyle(color = primaryGreen, fontWeight = FontWeight.Bold)) {
                    append("Kaydolun")
                }
            },
            fontSize = 14.sp,
            modifier = Modifier.clickable { onNavigateToRegister() }
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Navigation to Register Screen Button
        OutlinedButton(
            onClick = onNavigateToRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryGreen),
            border = BorderStroke(1.dp, primaryGreen)
        ) {
            Text(
                text = "Kayıt Ol Ekranına Git",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation to Setup Profile Screen Button
        OutlinedButton(
            onClick = onNavigateToSetupProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryGreen),
            border = BorderStroke(1.dp, primaryGreen)
        ) {
            Text(
                text = "Profili Tamamla Ekranına Git",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation to Profile Screen Button (profili tamamla ekranına git)
        OutlinedButton(
            onClick = onNavigateToProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryGreen),
            border = BorderStroke(1.dp, primaryGreen)
        ) {
            Text(
                text = "profili tamamla ekranına git",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation to Edit Profile Screen Button (profili tamamla ekranına git)
        OutlinedButton(
            onClick = onNavigateToEditProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryGreen),
            border = BorderStroke(1.dp, primaryGreen)
        ) {
            Text(
                text = "profili tamamla ekranına git",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
