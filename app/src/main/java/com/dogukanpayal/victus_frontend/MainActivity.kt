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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
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
import com.dogukanpayal.victus_frontend.ui.diet.DietPlanViewModel
import com.dogukanpayal.victus_frontend.ui.diet.DietPlanCreatorScreen
import com.dogukanpayal.victus_frontend.ui.diet.DietPlanCreatorViewModel
import com.dogukanpayal.victus_frontend.ui.scanner.ScannerScreen
import com.dogukanpayal.victus_frontend.ui.scanner.ScannerViewModel
import com.dogukanpayal.victus_frontend.ui.components.MainBottomNavigation
import com.dogukanpayal.victus_frontend.ui.components.MainDrawerContent
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepositoryImpl
import com.dogukanpayal.victus_frontend.data.repository.WorkoutPresetRepository
import com.dogukanpayal.victus_frontend.data.repository.DailyWorkoutRepository
import com.dogukanpayal.victus_frontend.data.storage.SessionManager
import com.dogukanpayal.victus_frontend.ui.dietitian.*
import com.dogukanpayal.victus_frontend.ui.components.DietitianBottomNavigation

enum class Screen { 
    Login, Register, SetupProfile, Profile, EditProfile, 
    Home, Workout, Exercise, Diet, Scanner, DietPlanCreator,
    DietitianDashboard, DietitianPatients, DietitianPatientDetail,
    LinkDietitian
}

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
                val userAvatarUrl = remember { mutableStateOf<String?>(null) }
                val currentPatientIdForPlan = remember { mutableStateOf<String?>(null) }

                val setupToken = remember { mutableStateOf("") }
                val setupEmail = remember { mutableStateOf("") }
                val setupFullName = remember { mutableStateOf("") }
                val profileRepository = remember { ProfileRepositoryImpl() }
                val context = LocalContext.current.applicationContext
                val sessionManager = remember { SessionManager(context) }
                val userRole = remember { mutableStateOf(sessionManager.getRole()) }

                // Role senkronizasyonu
                LaunchedEffect(currentScreen.value) {
                    userRole.value = sessionManager.getRole()
                }

                // Otomatik Yönlendirme (Rol değişiminde veya girişte)
                LaunchedEffect(userRole.value) {
                    if (setupToken.value.isNotEmpty()) {
                        if (userRole.value == "dietitian") {
                            // Diyetisyen moduna geçildiyse ve şu an hasta ekranlarındaysa yönlendir
                            if (currentScreen.value in listOf(Screen.Home, Screen.Workout, Screen.Exercise, Screen.Diet, Screen.Scanner)) {
                                currentScreen.value = Screen.DietitianDashboard
                            }
                        } else {
                            // Hasta moduna geçildiyse ve şu an diyetisyen ekranlarındaysa yönlendir
                            if (currentScreen.value in listOf(Screen.DietitianDashboard, Screen.DietitianPatients, Screen.DietitianPatientDetail)) {
                                currentScreen.value = Screen.Home
                            }
                        }
                    }
                }

                // Uygulama açılışında kaydedilmiş token kontrolü
                LaunchedEffect(Unit) {
                    val savedToken = sessionManager.getSavedToken()
                    if (!savedToken.isNullOrBlank()) {
                        setupToken.value = savedToken
                        currentScreen.value = Screen.Home
                    }

                    // Global auth olaylarını dinle (örn: 401 Unauthorized)
                    com.dogukanpayal.victus_frontend.data.remote.AuthEventBus.authEvents.collect { event ->
                        when (event) {
                            is com.dogukanpayal.victus_frontend.data.remote.AuthEvent.Unauthorized -> {
                                scope.launch {
                                    sessionManager.clearSession()
                                    setupToken.value = ""
                                    currentScreen.value = Screen.Login
                                    drawerState.close()
                                }
                            }
                        }
                    }
                }

                val showBottomBar = currentScreen.value in listOf(
                    Screen.Home, Screen.Workout, Screen.Exercise, Screen.Diet, Screen.Scanner,
                    Screen.DietitianDashboard, Screen.DietitianPatients
                )

                val screenTitle = when (currentScreen.value) {
                    Screen.Home -> "Ana Sayfa"
                    Screen.Workout -> "Antrenman"
                    Screen.Exercise -> "Analiz"
                    Screen.Diet -> "Diyet"
                    Screen.Scanner -> "Tarayıcı"
                    Screen.Profile -> "Profil"
                    Screen.EditProfile -> "Profili Düzenle"
                    Screen.SetupProfile -> "Profilini Tamamla"
                    Screen.DietPlanCreator -> "Beslenme Planı Oluştur"
                    Screen.DietitianDashboard -> "Panel"
                    Screen.DietitianPatients -> "Hastalarım"
                    Screen.DietitianPatientDetail -> "Hasta Detayı"
                    Screen.LinkDietitian -> "Diyetisyene Bağlan"
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
                                userAvatarUrl = userAvatarUrl.value,
                                onNotificationsClick = { /* Handle Notifications */ },
                                onSettingsClick = {
                                    scope.launch { drawerState.close() }
                                    currentScreen.value = Screen.Profile
                                },
                                onLinkDietitianClick = {
                                    scope.launch { drawerState.close() }
                                    currentScreen.value = Screen.LinkDietitian
                                },
                                onLogoutClick = {
                                    scope.launch { drawerState.close() }
                                    sessionManager.clearSession()
                                    setupToken.value = ""
                                    currentScreen.value = Screen.Login
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.White, // Root background should be white
                        contentWindowInsets = WindowInsets(0, 0, 0, 0), // No default insets for the content
                        topBar = {
                            if (showBottomBar) {
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
                                if (userRole.value == "dietitian") {
                                    DietitianBottomNavigation(
                                        currentScreen = currentScreen.value,
                                        onNavigate = { screen -> currentScreen.value = screen }
                                    )
                                } else {
                                    MainBottomNavigation(
                                        currentScreen = currentScreen.value,
                                        onNavigate = { screen -> currentScreen.value = screen }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        // Manual top padding for topBar since we disabled Scaffold insets
                        val topPadding = if (showBottomBar) innerPadding.calculateTopPadding() else 0.dp
                        val bottomPadding = 0.dp
                        
                        val currentUserId = remember { mutableStateOf(sessionManager.getUserId()) }

                        Box(modifier = Modifier.padding(top = topPadding, bottom = bottomPadding)) {
                            // Token geldiğinde kullanıcı bilgilerini çek
                            LaunchedEffect(setupToken.value) {
                                if (setupToken.value.isNotEmpty()) {
                                    val result = profileRepository.getProfile(setupToken.value)
                                    result.onSuccess { profile ->
                                        userName.value = profile.fullName ?: "İsimsiz Kullanıcı"
                                        userEmail.value = profile.email
                                        userAvatarUrl.value = profile.avatarUrl
                                        currentUserId.value = profile.id
                                        
                                        // Rol bilgisini otomatik kaydet
                                        profile.role?.let { role ->
                                            sessionManager.saveRole(role)
                                            userRole.value = role
                                            currentScreen.value = if (role == "dietitian") Screen.DietitianDashboard else Screen.Home
                                        }
                                    }
                                }
                            }

                            val loginViewModel = remember { LoginViewModel(sessionManager = sessionManager) }
                            val registerViewModel = remember { RegisterViewModel() }
                            val setupProfileViewModel = remember { SetupProfileViewModel() }
                            val profileViewModel = remember(setupToken.value, currentUserId.value) { ProfileViewModel(sessionManager = sessionManager) }
                            val dietitianRepository = remember { com.dogukanpayal.victus_frontend.data.repository.DietitianRepositoryImpl(com.dogukanpayal.victus_frontend.data.remote.RetrofitClient.apiService) }
                            val dietitianViewModel = remember(setupToken.value, currentUserId.value) { DietitianViewModel(repository = dietitianRepository) }
                            val editProfileViewModel = remember(setupToken.value, currentUserId.value) { EditProfileViewModel(context = context) }
                            val homeViewModel = remember(setupToken.value, currentUserId.value) { HomeViewModel() }
                            val dietPlanRepository = remember(setupToken.value, currentUserId.value) { com.dogukanpayal.victus_frontend.data.repository.DietPlanRepositoryImpl(context, currentUserId.value) }
                            val dietPlanCreatorViewModel = remember(setupToken.value, currentUserId.value) { DietPlanCreatorViewModel(context = context, repository = dietPlanRepository) }
                            val workoutPresetRepository = remember(setupToken.value, currentUserId.value) { WorkoutPresetRepository(context, currentUserId.value) }
                            val dailyWorkoutRepository = remember(setupToken.value, currentUserId.value) { DailyWorkoutRepository(context, currentUserId.value) }
                            val workoutViewModel = remember(setupToken.value, currentUserId.value) { WorkoutViewModel(workoutPresetRepository, dailyWorkoutRepository) }
                            val exerciseViewModel = remember(setupToken.value, currentUserId.value) { ExerciseViewModel() }
                            val dietViewModel = remember(setupToken.value, currentUserId.value) { DietViewModel() }
                            val dietPlanViewModel = remember(setupToken.value, currentUserId.value) { DietPlanViewModel(context = context, repository = dietPlanRepository) }
                            val scannerViewModel = remember(setupToken.value, currentUserId.value) { ScannerViewModel() }
                            val linkDietitianViewModel = remember(setupToken.value, currentUserId.value) { com.dogukanpayal.victus_frontend.ui.dietitian_link.LinkDietitianViewModel(profileRepository) }

                            val mainScreens = listOf(
                                Screen.Scanner,
                                Screen.Diet,
                                Screen.Home,
                                Screen.Workout,
                                Screen.Exercise
                            )
                            val pagerState = rememberPagerState(
                                initialPage = 2,
                                pageCount = { mainScreens.size }
                            )

                            // Pager ve Screen senkronizasyonu
                            LaunchedEffect(pagerState.currentPage) {
                                if (showBottomBar && userRole.value != "dietitian") {
                                    currentScreen.value = mainScreens[pagerState.currentPage]
                                }
                            }

                            LaunchedEffect(currentScreen.value) {
                                if (showBottomBar) {
                                    val targetPage = mainScreens.indexOf(currentScreen.value)
                                    if (targetPage != -1 && targetPage != pagerState.currentPage) {
                                        pagerState.scrollToPage(targetPage)
                                    }
                                }
                            }

                            when (currentScreen.value) {
                                Screen.Login -> LoginScreen(
                                    viewModel = loginViewModel,
                                    onNavigateToRegister = { currentScreen.value = Screen.Register },
                                    onNavigateToProfile = { authSession ->
                                        setupToken.value = authSession.accessToken
                                        currentUserId.value = authSession.userId
                                        currentScreen.value = Screen.Home
                                    }
                                )

                                Screen.Register -> RegisterScreen(
                                    viewModel = registerViewModel,
                                    onNavigateToLogin = { currentScreen.value = Screen.Login },
                                    onNavigateToSetup = { token, email, name ->
                                        setupToken.value = token
                                        setupEmail.value = email
                                        setupFullName.value = name
                                        currentScreen.value = Screen.SetupProfile
                                    }
                                )

                                Screen.SetupProfile -> SetupProfileScreen(
                                    viewModel = setupProfileViewModel,
                                    onNavigateBack = { currentScreen.value = Screen.Login },
                                    accessToken = setupToken.value,
                                    email = setupEmail.value,
                                    fullName = setupFullName.value,
                                    onProfileUpdateSuccess = { currentScreen.value = Screen.Home }
                                )

                                Screen.Home, Screen.Workout, Screen.Exercise, Screen.Diet, Screen.Scanner -> {
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier.fillMaxSize(),
                                        userScrollEnabled = true
                                    ) { page ->
                                        when (mainScreens[page]) {
                                            Screen.Home -> HomeScreen(
                                                viewModel = homeViewModel,
                                                token = setupToken.value
                                            )
                                            Screen.Workout -> WorkoutScreen(
                                                viewModel = workoutViewModel,
                                                token = setupToken.value
                                            )
                                            Screen.Exercise -> ExerciseScreen(viewModel = exerciseViewModel, token = setupToken.value)
                                            Screen.Diet -> DietScreen(
                                                viewModel = dietViewModel,
                                                dietPlanViewModel = dietPlanViewModel,
                                                token = setupToken.value,
                                                onNavigateToCreator = { 
                                                    dietPlanCreatorViewModel.resetForm()
                                                    currentScreen.value = Screen.DietPlanCreator 
                                                }
                                            )
                                            Screen.Scanner -> ScannerScreen(viewModel = scannerViewModel, token = setupToken.value)
                                            else -> {}
                                        }
                                    }
                                }

                                Screen.Profile -> ProfileScreen(
                                    viewModel = profileViewModel,
                                    accessToken = setupToken.value,
                                    onNavigateBack = { 
                                        currentScreen.value = if (userRole.value == "dietitian") Screen.DietitianDashboard else Screen.Home 
                                    },
                                    onNavigateToEditProfile = { currentScreen.value = Screen.EditProfile },
                                    onLogout = {
                                        sessionManager.clearSession()
                                        setupToken.value = ""
                                        
                                        // RESET ALL VIEWMODELS
                                        dietPlanViewModel.resetState()
                                        dietViewModel.resetState()
                                        homeViewModel.resetState()
                                        dietitianViewModel.clearState()
                                        workoutViewModel.resetState()

                                        currentScreen.value = Screen.Login
                                    }
                                )

                                Screen.EditProfile -> {
                                    EditProfileScreen(
                                        viewModel = editProfileViewModel,
                                        accessToken = setupToken.value,
                                        onNavigateBack = {
                                            // Geri dönüş sırasında profil verisini yenile
                                            if (setupToken.value.isNotEmpty()) {
                                                scope.launch {
                                                    val result = profileRepository.getProfile(setupToken.value)
                                                    result.onSuccess { profile ->
                                                        userName.value = profile.fullName ?: "İsimsiz Kullanıcı"
                                                        userEmail.value = profile.email
                                                        userAvatarUrl.value = profile.avatarUrl
                                                    }
                                                }
                                            }
                                            currentScreen.value = Screen.Profile
                                        }
                                    )
                                }

                                Screen.DietPlanCreator -> {
                                     // Sayfa açıldığında ID'yi ViewModel'e set et
                                     LaunchedEffect(currentPatientIdForPlan.value) {
                                         dietPlanCreatorViewModel.setPatientId(currentPatientIdForPlan.value)
                                     }
                                     DietPlanCreatorScreen(
                                         viewModel = dietPlanCreatorViewModel,
                                         token = setupToken.value,
                                         onNavigateBack = { 
                                             if (userRole.value == "dietitian") {
                                                 currentScreen.value = Screen.DietitianPatientDetail
                                             } else {
                                                 currentScreen.value = Screen.Diet
                                             }
                                         },
                                         onPlanCreated = {
                                             dietPlanViewModel.loadRemotePlan(setupToken.value)
                                             if (userRole.value == "dietitian") {
                                                 // Hastanın bilgilerini sunucudan tekrar çek (yenile)
                                                 currentPatientIdForPlan.value?.let { patientId ->
                                                     dietitianViewModel.loadPatientDetail(setupToken.value, patientId)
                                                 }
                                                 currentScreen.value = Screen.DietitianPatientDetail
                                             } else {
                                                 currentScreen.value = Screen.Diet
                                             }
                                         }
                                     )
                                 }
                                Screen.DietitianDashboard -> DietitianDashboardScreen(
                                    viewModel = dietitianViewModel,
                                    userName = userName.value,
                                    token = setupToken.value
                                )
                                Screen.DietitianPatients -> {
                                    LaunchedEffect(Unit) {
                                        dietitianViewModel.loadPatients(setupToken.value)
                                    }
                                    DietitianPatientListScreen(
                                        viewModel = dietitianViewModel,
                                        onPatientClick = { patientId ->
                                            android.util.Log.d("Victus", "Patient clicked: $patientId")
                                            dietitianViewModel.loadPatientDetail(setupToken.value, patientId)
                                            currentScreen.value = Screen.DietitianPatientDetail
                                        }
                                    )
                                }
                                Screen.DietitianPatientDetail -> DietitianPatientDetailScreen(
                                    viewModel = dietitianViewModel,
                                    token = setupToken.value,
                                    onNavigateBack = { currentScreen.value = Screen.DietitianPatients },
                                    onCreatePlan = { patientId ->
                                        currentPatientIdForPlan.value = patientId
                                        dietPlanCreatorViewModel.resetForm()
                                        dietPlanCreatorViewModel.setPatientId(patientId)
                                        currentScreen.value = Screen.DietPlanCreator
                                    }
                                )
                                Screen.LinkDietitian -> {
                                    com.dogukanpayal.victus_frontend.ui.dietitian_link.LinkDietitianScreen(
                                        viewModel = linkDietitianViewModel,
                                        token = setupToken.value,
                                        onNavigateBack = { currentScreen.value = Screen.Home },
                                        onNavigateToHome = { currentScreen.value = Screen.Home }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
// deneme
// deneme
// deneme