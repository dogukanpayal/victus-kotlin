package com.dogukanpayal.victus_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dogukanpayal.victus_frontend.ui.theme.Victus_frontendTheme
import com.dogukanpayal.victus_frontend.ui.login.LoginScreen
import com.dogukanpayal.victus_frontend.ui.register.RegisterScreen
import com.dogukanpayal.victus_frontend.ui.setup_profile.SetupProfileScreen
import com.dogukanpayal.victus_frontend.ui.profile.ProfileScreen
import com.dogukanpayal.victus_frontend.ui.profile.EditProfileScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

enum class Screen { Login, Register, SetupProfile, Profile, EditProfile }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Victus_frontendTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        val currentScreen = remember { mutableStateOf(Screen.Login) }

                        when (currentScreen.value) {
                            Screen.Login -> {
                                LoginScreen(
                                    onNavigateToRegister = { currentScreen.value = Screen.Register },
                                    onNavigateToSetupProfile = { currentScreen.value = Screen.SetupProfile },
                                    onNavigateToProfile = { currentScreen.value = Screen.Profile },
                                    onNavigateToEditProfile = { currentScreen.value = Screen.EditProfile }
                                )
                            }
                            Screen.Register -> {
                                RegisterScreen(
                                    onNavigateToLogin = { currentScreen.value = Screen.Login }
                                )
                            }
                            Screen.SetupProfile -> {
                                SetupProfileScreen(
                                    onNavigateBack = { currentScreen.value = Screen.Login }
                                )
                            }
                            Screen.Profile -> {
                                ProfileScreen(
                                    onNavigateBack = { currentScreen.value = Screen.Login },
                                    onNavigateToEditProfile = { currentScreen.value = Screen.EditProfile }
                                )
                            }
                            Screen.EditProfile -> {
                                EditProfileScreen(
                                    onNavigateBack = { currentScreen.value = Screen.Profile }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}