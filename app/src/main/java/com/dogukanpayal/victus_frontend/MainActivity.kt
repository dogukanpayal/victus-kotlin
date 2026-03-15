package com.dogukanpayal.victus_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.launch
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
import com.dogukanpayal.victus_frontend.ui.home.HomeScreen
import com.dogukanpayal.victus_frontend.ui.home.HomeViewModel
import com.dogukanpayal.victus_frontend.ui.workout.WorkoutScreen
import com.dogukanpayal.victus_frontend.ui.workout.WorkoutViewModel
import com.dogukanpayal.victus_frontend.ui.exercise.ExerciseScreen
import com.dogukanpayal.victus_frontend.ui.exercise.ExerciseViewModel
import com.dogukanpayal.victus_frontend.ui.diet.DietScreen
import com.dogukanpayal.victus_frontend.ui.diet.DietViewModel
import com.dogukanpayal.victus_frontend.ui.scanner.ScannerScreen
import com.dogukanpayal.victus_frontend.ui.scanner.ScannerViewModel
import com.dogukanpayal.victus_frontend.ui.components.MainBottomNavigation
import com.dogukanpayal.victus_frontend.ui.components.MainDrawerContent
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepositoryImpl

enum class Screen { Login, Register, SetupProfile, Profile, EditProfile, Home, Workout, Exercise, Diet, Scanner }

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Victus_frontendTheme {
                val currentScreen = remember { mutableStateOf(Screen.Login) }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                // User Data States
                val userName = remember { mutableStateOf("Yükleniyor...") }
                val userEmail = remember { mutableStateOf("") }

                val setupToken = remember { mutableStateOf("") }
                val setupEmail = remember { mutableStateOf("") }
                val profileRepository = remember { ProfileRepositoryImpl() }

                val showBottomBar = currentScreen.value in listOf(
                    Screen.Home, Screen.Workout, Screen.Exercise, Screen.Diet, Screen.Scanner
                )

                val screenTitle = when (currentScreen.value) {
                    Screen.Home -> "Ana Sayfa"
                    Screen.Workout -> "Antrenman"
                    Screen.Exercise -> "Egzersiz"
                    Screen.Diet -> "Diyet"
                    Screen.Scanner -> "Tarayıcı"
                    Screen.Profile -> "Profil"
                    Screen.EditProfile -> "Profili Düzenle"
                    Screen.SetupProfile -> "Profili Tamamla"
                    else -> ""
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = showBottomBar,
                    drawerContent = {
                        ModalDrawerSheet {
                            MainDrawerContent(
                                userName = userName.value,
                                userEmail = userEmail.value,
                                onNotificationsClick = { /* Handle Notifications */ },
                                onSettingsClick = {
                                    scope.launch { drawerState.close() }
                                    currentScreen.value = Screen.Profile
                                },
                                onLogoutClick = {
                                    scope.launch { drawerState.close() }
                                    currentScreen.value = Screen.Login
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (showBottomBar || currentScreen.value == Screen.Profile || currentScreen.value == Screen.EditProfile) {
                                CenterAlignedTopAppBar(
                                    title = {
                                        Text(
                                            text = screenTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    },
                                    navigationIcon = {
                                        if (showBottomBar) {
                                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                                Icon(
                                                    imageVector = Icons.Default.Menu,
                                                    contentDescription = "Menü"
                                                )
                                            }
                                        }
                                    },
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = Color.White
                                    )
                                )
                            }
                        },
                        bottomBar = {
                            if (showBottomBar) {
                                MainBottomNavigation(
                                    currentScreen = currentScreen.value,
                                    onNavigate = { screen -> currentScreen.value = screen }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            // Fetch Real User Profile when Token is available
                            LaunchedEffect(setupToken.value) {
                                if (setupToken.value.isNotEmpty()) {
                                    val result = profileRepository.getProfile(setupToken.value)
                                    result.onSuccess { profile ->
                                        userName.value = profile.fullName ?: "İsimsiz Kullanıcı"
                                        userEmail.value = profile.email
                                    }.onFailure {
                                        // Fallback if fetch fails
                                        if (userEmail.value.isEmpty()) {
                                            userEmail.value = setupEmail.value
                                        }
                                    }
                                }
                            }

                            val loginViewModel = remember { LoginViewModel() }
                            val registerViewModel = remember { RegisterViewModel() }
                            val setupProfileViewModel = remember { SetupProfileViewModel() }
                            val profileViewModel = remember { ProfileViewModel() }
                            val editProfileViewModel = remember { EditProfileViewModel() }
                            val homeViewModel = remember { HomeViewModel() }
                            val workoutViewModel = remember { WorkoutViewModel() }
                            val exerciseViewModel = remember { ExerciseViewModel() }
                            val dietViewModel = remember { DietViewModel() }
                            val scannerViewModel = remember { ScannerViewModel() }

                            val mainScreens = listOf(
                                Screen.Scanner,
                                Screen.Diet,
                                Screen.Home,
                                Screen.Workout,
                                Screen.Exercise
                            )
                            val pagerState = rememberPagerState(
                                initialPage = 2, // Default to Home
                                pageCount = { mainScreens.size }
                            )

                            // Sync currentScreen with pagerState
                            LaunchedEffect(pagerState.currentPage) {
                                if (showBottomBar) {
                                    currentScreen.value = mainScreens[pagerState.currentPage]
                                }
                            }

                            // Sync pagerState with currentScreen (when changed from outside, e.g., login)
                            LaunchedEffect(currentScreen.value) {
                                if (showBottomBar) {
                                    val targetPage = mainScreens.indexOf(currentScreen.value)
                                    if (targetPage != -1 && targetPage != pagerState.currentPage) {
                                        pagerState.scrollToPage(targetPage)
                                    }
                                }
                            }

                            when (currentScreen.value) {
                                Screen.Login -> {
                                    LoginScreen(
                                        viewModel = loginViewModel,
                                        onNavigateToRegister = {
                                            currentScreen.value = Screen.Register
                                        },
                                        onNavigateToProfile = { authSession ->
                                            setupToken.value = authSession.accessToken
                                            currentScreen.value = Screen.Home
                                        }
                                    )
                                }

                                Screen.Register -> {
                                    RegisterScreen(
                                        viewModel = registerViewModel,
                                        onNavigateToLogin = { currentScreen.value = Screen.Login },
                                        onNavigateToSetup = { token, email ->
                                            setupToken.value = token
                                            setupEmail.value = email
                                            currentScreen.value = Screen.SetupProfile
                                        }
                                    )
                                }

                                Screen.SetupProfile -> {
                                    SetupProfileScreen(
                                        viewModel = setupProfileViewModel,
                                        onNavigateBack = { currentScreen.value = Screen.Login },
                                        accessToken = setupToken.value,
                                        email = setupEmail.value,
                                        onProfileUpdateSuccess = {
                                            currentScreen.value = Screen.Home
                                        }
                                    )
                                }

                                Screen.Profile -> {
                                    ProfileScreen(
                                        viewModel = profileViewModel,
                                        accessToken = setupToken.value,  // ✅ AccessToken geçiliyor
                                        onNavigateBack = { currentScreen.value = Screen.Login },
                                        onNavigateToEditProfile = {
                                            currentScreen.value = Screen.EditProfile
                                        }
                                    )
                                }

                                Screen.EditProfile -> {
                                    EditProfileScreen(
                                        viewModel = editProfileViewModel,
                                        onNavigateBack = { currentScreen.value = Screen.Profile }
                                    )
                                }
                                // Main screens handled by HorizontalPager
                                Screen.Home, Screen.Workout, Screen.Exercise, Screen.Diet, Screen.Scanner -> {
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier.fillMaxSize(),
                                        userScrollEnabled = true
                                    ) { page ->
                                        when (mainScreens[page]) {
                                            Screen.Home -> HomeScreen(viewModel = homeViewModel)
                                            Screen.Workout -> WorkoutScreen(viewModel = workoutViewModel)
                                            Screen.Exercise -> ExerciseScreen(viewModel = exerciseViewModel)
                                            Screen.Diet -> DietScreen(viewModel = dietViewModel)
                                            Screen.Scanner -> ScannerScreen(viewModel = scannerViewModel)
                                            else -> {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}