package com.dogukanpayal.victus_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dogukanpayal.victus_frontend.ui.theme.Victus_frontendTheme
import com.dogukanpayal.victus_frontend.ui.login.LoginScreen
import com.dogukanpayal.victus_frontend.ui.login.LoginViewModel
import com.dogukanpayal.victus_frontend.ui.register.RegisterScreen
import com.dogukanpayal.victus_frontend.ui.register.RegisterViewModel
import com.dogukanpayal.victus_frontend.ui.setup_profile.SetupProfileScreen
import com.dogukanpayal.victus_frontend.ui.setup_profile.SetupProfileViewModel
import com.dogukanpayal.victus_frontend.ui.profile.ProfileScreen
import com.dogukanpayal.victus_frontend.ui.profile.ProfileViewModel
import com.dogukanpayal.victus_frontend.ui.profile.EditProfileScreen
import com.dogukanpayal.victus_frontend.ui.profile.EditProfileViewModel
import androidx.compose.foundation.layout.Box

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
                        val loginViewModel = remember { LoginViewModel() }
                        val registerViewModel = remember { RegisterViewModel() }
                        val setupProfileViewModel = remember { SetupProfileViewModel() }
                        val profileViewModel = remember { ProfileViewModel() }
                        val editProfileViewModel = remember { EditProfileViewModel() }

                        when (currentScreen.value) {
                            Screen.Login -> {
                                LoginScreen(
                                    viewModel = loginViewModel,
                                    onNavigateToRegister = { currentScreen.value = Screen.Register },
                                    onNavigateToSetupProfile = { currentScreen.value = Screen.SetupProfile },
                                    onNavigateToProfile = { currentScreen.value = Screen.Profile },
                                    onNavigateToEditProfile = { currentScreen.value = Screen.EditProfile }
                                )
                            }
                            Screen.Register -> {
                                RegisterScreen(
                                    viewModel = registerViewModel,
                                    onNavigateToLogin = { currentScreen.value = Screen.Login }
                                )
                            }
                            Screen.SetupProfile -> {
                                SetupProfileScreen(
                                    viewModel = setupProfileViewModel,
                                    onNavigateBack = { currentScreen.value = Screen.Login }
                                )
                            }
                            Screen.Profile -> {
                                ProfileScreen(
                                    viewModel = profileViewModel,
                                    onNavigateBack = { currentScreen.value = Screen.Login },
                                    onNavigateToEditProfile = { currentScreen.value = Screen.EditProfile }
                                )
                            }
                            Screen.EditProfile -> {
                                EditProfileScreen(
                                    viewModel = editProfileViewModel,
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