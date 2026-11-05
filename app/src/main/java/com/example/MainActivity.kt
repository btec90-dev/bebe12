package com.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.MamanBottomBar
import com.example.ui.components.MamanTopBar
import com.example.ui.components.PinLockScreen
import com.example.ui.components.PublicShareDialog
import com.example.ui.components.SettingsPrivacyDialog
import com.example.ui.components.UserProfileDialog
import com.example.ui.screens.AppointmentsScreen
import com.example.ui.screens.BabyLogScreen
import com.example.ui.screens.ChecklistScreen
import com.example.ui.screens.ExpensesTrackerScreen
import com.example.ui.screens.FirstTimeMomGuideScreen
import com.example.ui.screens.HealthConnectDialog
import com.example.ui.screens.KickCounterScreen
import com.example.ui.screens.MamanAssistantScreen
import com.example.ui.screens.MaternityBagChecklistScreen
import com.example.ui.screens.MedicalWalletScreen
import com.example.ui.screens.MilestoneJournalScreen
import com.example.ui.screens.PregnancyLogScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.screens.WeekDetailScreen
import com.example.ui.theme.MamanBebeTheme
import com.example.ui.viewmodel.PregnancyViewModel
import com.example.ui.viewmodel.ScreenTab
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.example.util.UserSessionManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import java.util.Locale

class MainActivity : androidx.fragment.app.FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(this)
            }
        } catch (e: Throwable) {
            android.util.Log.w("MainActivity", "FirebaseApp init skipped: ${e.message}")
        }
        try {
            com.example.repository.PregnancySyncWorker.scheduleSync(this)
        } catch (e: Throwable) {
            android.util.Log.w("MainActivity", "PregnancySyncWorker init skipped: ${e.message}")
        }
        setContent {
            MamanBebeTheme {
                val auth = remember {
                    try {
                        Firebase.auth
                    } catch (_: Throwable) {
                        null
                    }
                }
                var currentUser by remember {
                    mutableStateOf(
                        try {
                            auth?.currentUser
                        } catch (_: Throwable) {
                            null
                        }
                    )
                }
                // Persistently remember session for bala1fatou@gmail.com so user is never prompted to sign on
                var isSessionConnected by remember {
                    mutableStateOf(UserSessionManager.isConnected(this@MainActivity))
                }

                DisposableEffect(auth) {
                    if (auth != null) {
                        val authStateListener = FirebaseAuth.AuthStateListener { fbAuth ->
                            try {
                                currentUser = fbAuth.currentUser
                                if (fbAuth.currentUser != null) {
                                    UserSessionManager.saveSession(
                                        context = this@MainActivity,
                                        email = fbAuth.currentUser?.email ?: UserSessionManager.DEFAULT_EMAIL,
                                        displayName = fbAuth.currentUser?.displayName ?: "Fatou & Bala",
                                        uid = fbAuth.currentUser?.uid
                                    )
                                    isSessionConnected = true
                                    com.example.repository.PregnancySyncManager.startBackgroundSync(this@MainActivity)
                                }
                            } catch (_: Throwable) {}
                        }
                        try {
                            auth.addAuthStateListener(authStateListener)
                        } catch (_: Throwable) {}
                        onDispose {
                            try {
                                auth.removeAuthStateListener(authStateListener)
                            } catch (_: Throwable) {}
                        }
                    } else {
                        onDispose {}
                    }
                }

                LaunchedEffect(isSessionConnected) {
                    if (isSessionConnected) {
                        try {
                            com.example.repository.PregnancySyncManager.startBackgroundSync(this@MainActivity)
                        } catch (_: Throwable) {}
                    }
                }

                if (!isSessionConnected && currentUser == null) {
                    SignInScreen(
                        onSignInSuccess = {
                            isSessionConnected = true
                            try {
                                currentUser = Firebase.auth.currentUser
                            } catch (_: Throwable) {}
                            UserSessionManager.saveSession(
                                context = this@MainActivity,
                                email = UserSessionManager.getConnectedEmail(this@MainActivity),
                                displayName = UserSessionManager.getDisplayName(this@MainActivity),
                                uid = UserSessionManager.getUserId(this@MainActivity)
                            )
                            try {
                                com.example.repository.PregnancySyncManager.startBackgroundSync(this@MainActivity)
                            } catch (_: Throwable) {}
                        }
                    )
                } else {
                    MamanBebeApp()
                }
            }
        }
    }
}

@Composable
fun MamanBebeApp(
    viewModel: PregnancyViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val hasHealthPerms by viewModel.hasHealthPermissions.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val isAppLocked by viewModel.isAppLocked.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val fatherName by viewModel.fatherName.collectAsState()
    val profileAge by viewModel.profileAge.collectAsState()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsState()
    val titleColorHex by viewModel.titleColorHex.collectAsState()

    var isShowingWeekDetail by remember { mutableStateOf(false) }
    var showHealthConnectDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showPublicShareDialog by remember { mutableStateOf(false) }

    val locale = remember(currentLanguage) {
        when (currentLanguage) {
            "fr" -> Locale.FRENCH
            else -> Locale.ENGLISH
        }
    }

    val baseContext = LocalContext.current
    val baseConfig = LocalConfiguration.current

    val localizedConfig = remember(locale, baseConfig) {
        Configuration(baseConfig).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
    }

    LaunchedEffect(locale, localizedConfig) {
        Locale.setDefault(locale)
        try {
            baseContext.resources.updateConfiguration(localizedConfig, baseContext.resources.displayMetrics)
        } catch (_: Throwable) {}
        try {
            val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(currentLanguage)
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
        } catch (_: Throwable) {}
    }

    val layoutDirection = if (currentLanguage == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalConfiguration provides localizedConfig,
        LocalLayoutDirection provides layoutDirection
    ) {
        if (isPinEnabled && isAppLocked) {
            PinLockScreen(
                viewModel = viewModel
            )
            return@CompositionLocalProvider
        }

        BackHandler(enabled = isShowingWeekDetail || currentTab != ScreenTab.TODAY) {
            if (isShowingWeekDetail) {
                isShowingWeekDetail = false
            } else {
                viewModel.selectTab(ScreenTab.TODAY)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (!isShowingWeekDetail) {
                    MamanTopBar(
                        currentWeek = currentWeek,
                        currentTab = currentTab,
                        profileName = profileName,
                        profileAge = profileAge,
                        profilePhotoUri = profilePhotoUri,
                        titleColorHex = titleColorHex,
                        onWeekClick = { isShowingWeekDetail = true },
                        onProfileClick = { showProfileDialog = true },
                        onSettingsClick = { showSettingsDialog = true },
                        onPublicShareClick = { showPublicShareDialog = true }
                    )
                }
            },
            bottomBar = {
                if (!isShowingWeekDetail) {
                    MamanBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            isShowingWeekDetail = false
                            viewModel.selectTab(tab)
                        }
                    )
                }
            }
        ) { innerPadding ->
            val effectiveBottomPadding = if (currentTab == ScreenTab.ASSISTANT) 0.dp else innerPadding.calculateBottomPadding()
            val effectiveTopPadding = innerPadding.calculateTopPadding()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = effectiveTopPadding,
                        bottom = effectiveBottomPadding
                    )
            ) {
                if (isShowingWeekDetail) {
                    WeekDetailScreen(
                        viewModel = viewModel,
                        onBack = { isShowingWeekDetail = false }
                    )
                } else {
                    when (currentTab) {
                        ScreenTab.TODAY -> TodayScreen(
                            viewModel = viewModel,
                            onNavigateWeekDetail = { isShowingWeekDetail = true },
                            onOpenHealthConnectDialog = { showHealthConnectDialog = true },
                            onOpenLanguageDialog = { showLanguageDialog = true },
                            onOpenSettings = { showSettingsDialog = true },
                            onOpenPublicShareDialog = { showPublicShareDialog = true }
                        )
                        ScreenTab.STATS -> com.example.ui.screens.StatsScreen(
                            viewModel = viewModel,
                            onOpenHealthConnectDialog = { showHealthConnectDialog = true }
                        )
                        ScreenTab.KICK_COUNTER -> KickCounterScreen(
                            viewModel = viewModel
                        )
                        ScreenTab.HEALTH_LOG -> PregnancyLogScreen(
                            viewModel = viewModel,
                            onOpenHealthConnectDialog = { showHealthConnectDialog = true },
                            onOpenLanguageDialog = { showLanguageDialog = true }
                        )
                        ScreenTab.BABY_LOG -> BabyLogScreen(
                            viewModel = viewModel
                        )
                        ScreenTab.ASSISTANT -> MamanAssistantScreen(
                            viewModel = viewModel,
                            bottomBarPadding = innerPadding.calculateBottomPadding(),
                            onOpenLanguageDialog = { showLanguageDialog = true },
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.JOURNAL -> MilestoneJournalScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.CHECKLIST -> ChecklistScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.AGENDA -> AppointmentsScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.ROADMAP -> com.example.ui.screens.RoadmapScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.KNOWLEDGE, ScreenTab.FERTILITY -> com.example.ui.screens.KnowledgeScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.MATERNITY_BAG -> MaternityBagChecklistScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.EXPENSES -> ExpensesTrackerScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.WALLET -> MedicalWalletScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.NEWBORN_GUIDE -> FirstTimeMomGuideScreen(
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                        ScreenTab.COUPLE_HUB -> com.example.ui.screens.CoupleHubScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.selectTab(ScreenTab.TODAY) }
                        )
                    }
                }
            }
        }

        if (showHealthConnectDialog) {
            HealthConnectDialog(
                viewModel = viewModel,
                onDismiss = { showHealthConnectDialog = false }
            )
        }

        if (showLanguageDialog) {
            LanguageSelectorDialog(
                currentLanguageCode = currentLanguage,
                onLanguageSelected = { langCode ->
                    viewModel.setLanguage(langCode)
                },
                onDismiss = { showLanguageDialog = false }
            )
        }

        if (showSettingsDialog) {
            SettingsPrivacyDialog(
                viewModel = viewModel,
                onDismiss = { showSettingsDialog = false }
            )
        }

        if (showProfileDialog) {
            UserProfileDialog(
                viewModel = viewModel,
                onDismiss = { showProfileDialog = false }
            )
        }

        if (showPublicShareDialog) {
            PublicShareDialog(
                currentWeek = currentWeek,
                momName = profileName.ifBlank { "Fatou" },
                dadName = fatherName.ifBlank { "Bala" },
                onDismiss = { showPublicShareDialog = false }
            )
        }
    }
}
