package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAssistantClient
import com.example.data.ChecklistData
import com.example.data.MilestoneRoadmapData
import com.example.data.PregnancyWeeksData
import com.example.data.PrenatalAgendaData
import com.example.data.local.AppDatabase
import com.example.data.local.AppointmentEntity
import com.example.data.local.BabyLogEntity
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.JournalEntryEntity
import com.example.data.local.KickSessionEntity
import com.example.data.local.PregnancyLogEntity
import com.example.data.model.BabyMilestone
import com.example.data.model.PregnancyWeek
import com.example.data.model.SizeTheme
import com.example.health.HealthConnectAvailability
import com.example.health.HealthConnectManager
import com.example.health.SleepDataPoint
import com.example.health.WeightDataPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.data.local.ExpenseEntity
import com.example.data.local.WalletDocumentEntity
import com.example.data.local.GlycemiaRecordEntity
import com.example.data.local.ContractionEntity
import com.example.data.local.BabyNameEntity
import com.example.data.local.CoupleNoteEntity
import kotlinx.coroutines.withContext

enum class ScreenTab {
    TODAY,
    KICK_COUNTER,
    HEALTH_LOG,
    BABY_LOG,
    ASSISTANT,
    CHECKLIST,
    AGENDA,
    JOURNAL,
    ROADMAP,
    FERTILITY,
    KNOWLEDGE,
    STATS,
    MATERNITY_BAG,
    EXPENSES,
    WALLET,
    NEWBORN_GUIDE,
    COUPLE_HUB
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class CalendarDay(
    val dateString: String,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val fullDate: Date,
    val isToday: Boolean,
    val hasLog: Boolean = false
)

class PregnancyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.pregnancyDao()
    val healthConnectManager = HealthConnectManager(application)
    private val assistantClient = GeminiAssistantClient()
    private val prefs = application.getSharedPreferences("maman_bebe_prefs", Context.MODE_PRIVATE)

    // Navigation state
    private val _currentTab = MutableStateFlow(ScreenTab.TODAY)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // Dynamic Due Date Engine (persisted)
    private val _dueDateMillis = MutableStateFlow(
        prefs.getLong("pref_due_date", System.currentTimeMillis() + 112L * 24 * 3600 * 1000)
    )
    val dueDateMillis: StateFlow<Long> = _dueDateMillis.asStateFlow()

    // Gestational age state (persisted)
    private val _currentWeekNumber = MutableStateFlow(
        prefs.getInt("pref_current_week", 24)
    )
    val currentWeekNumber: StateFlow<Int> = _currentWeekNumber.asStateFlow()

    private val _currentWeekData = MutableStateFlow(
        PregnancyWeeksData.getWeek(_currentWeekNumber.value)
    )
    val currentWeekData: StateFlow<PregnancyWeek> = _currentWeekData.asStateFlow()

    // Single vs. Twin Mode Toggle (persisted)
    private val _isTwinMode = MutableStateFlow(
        prefs.getBoolean("pref_is_twin", false)
    )
    val isTwinMode: StateFlow<Boolean> = _isTwinMode.asStateFlow()

    // Interactive Size Comparer Theme (persisted)
    private val _selectedSizeTheme = MutableStateFlow(
        try {
            val themeName = prefs.getString("pref_size_theme", SizeTheme.FRUITS.name) ?: SizeTheme.FRUITS.name
            SizeTheme.valueOf(themeName)
        } catch (_: Exception) {
            SizeTheme.FRUITS
        }
    )
    val selectedSizeTheme: StateFlow<SizeTheme> = _selectedSizeTheme.asStateFlow()

    // Multi-Language Support (persisted)
    private val _currentLanguage = MutableStateFlow(
        prefs.getString("pref_language", null) ?: run {
            val phoneLang = Locale.getDefault().language
            when (phoneLang) {
                "fr", "es", "pt" -> phoneLang
                else -> "en"
            }
        }
    )
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguage(code: String) {
        _currentLanguage.value = code
        prefs.edit().putString("pref_language", code).apply()
        try {
            val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(code)
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
        } catch (_: Exception) {}
    }

    // User Profile State (Fatou & Bala)
    private val _profileName = MutableStateFlow(
        prefs.getString("pref_profile_name", "Fatou") ?: "Fatou"
    )
    val profileName: StateFlow<String> = _profileName.asStateFlow()

    private val _fatherName = MutableStateFlow(
        prefs.getString("pref_father_name", "Bala") ?: "Bala"
    )
    val fatherName: StateFlow<String> = _fatherName.asStateFlow()

    fun updateFatherName(name: String) {
        _fatherName.value = name
        prefs.edit().putString("pref_father_name", name).apply()
    }

    private val _isBabyLogCompact = MutableStateFlow(
        prefs.getBoolean("pref_baby_compact", false)
    )
    val isBabyLogCompact: StateFlow<Boolean> = _isBabyLogCompact.asStateFlow()

    fun toggleBabyLogCompact() {
        _isBabyLogCompact.value = !_isBabyLogCompact.value
        prefs.edit().putBoolean("pref_baby_compact", _isBabyLogCompact.value).apply()
    }

    private val _profileAge = MutableStateFlow(
        prefs.getInt("pref_profile_age", 28)
    )
    val profileAge: StateFlow<Int> = _profileAge.asStateFlow()

    private val _profilePhotoUri = MutableStateFlow(
        run {
            val saved = prefs.getString("pref_profile_photo", "") ?: ""
            val localAvatar = java.io.File(application.filesDir, "user_profile_avatar.jpg")
            if (localAvatar.exists() && localAvatar.length() > 0) {
                // Permanent internal storage avatar always takes precedence so it never expires
                Uri.fromFile(localAvatar).toString()
            } else {
                saved
            }
        }
    )
    val profilePhotoUri: StateFlow<String> = _profilePhotoUri.asStateFlow()

    fun persistProfilePhoto(sourceUri: Uri): String {
        return try {
            val destination = java.io.File(getApplication<Application>().filesDir, "user_profile_avatar.jpg")
            getApplication<Application>().contentResolver.openInputStream(sourceUri)?.use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val fileUri = Uri.fromFile(destination).toString()
            _profilePhotoUri.value = fileUri
            prefs.edit().putString("pref_profile_photo", fileUri).apply()
            fileUri
        } catch (e: Exception) {
            val uriStr = sourceUri.toString()
            _profilePhotoUri.value = uriStr
            prefs.edit().putString("pref_profile_photo", uriStr).apply()
            uriStr
        }
    }

    fun updateProfilePhoto(photoUri: String) {
        _profilePhotoUri.value = photoUri
        prefs.edit().putString("pref_profile_photo", photoUri).apply()
    }

    fun updateUserProfile(name: String, age: Int, photoUri: String, father: String = "Bala") {
        _profileName.value = name
        _fatherName.value = father
        _profileAge.value = age
        _profilePhotoUri.value = photoUri
        prefs.edit()
            .putString("pref_profile_name", name)
            .putString("pref_father_name", father)
            .putInt("pref_profile_age", age)
            .putString("pref_profile_photo", photoUri)
            .apply()
    }

    // Title Color Customization (persisted)
    private val _titleColorHex = MutableStateFlow(
        prefs.getString("pref_title_color", "#2D1B10") ?: "#2D1B10"
    )
    val titleColorHex: StateFlow<String> = _titleColorHex.asStateFlow()

    fun updateTitleColor(colorHex: String) {
        _titleColorHex.value = colorHex
        prefs.edit().putString("pref_title_color", colorHex).apply()
    }

    // Privacy PIN & Security State
    private val _isPinEnabled = MutableStateFlow(
        prefs.getBoolean("pref_pin_enabled", false)
    )
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private val _userEmail = MutableStateFlow(
        prefs.getString("pref_user_email", "app@balatec.com") ?: "app@balatec.com"
    )
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _isAppLocked = MutableStateFlow(
        prefs.getBoolean("pref_pin_enabled", false)
    )
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _pinResetCode = MutableStateFlow<String?>(null)
    val pinResetCode: StateFlow<String?> = _pinResetCode.asStateFlow()

    private val _pinResetStatus = MutableStateFlow<String?>(null)
    val pinResetStatus: StateFlow<String?> = _pinResetStatus.asStateFlow()

    // 14-Day Calendar Strip State
    val todayDateFormatted: String = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
    private val _selectedCalendarDate = MutableStateFlow(todayDateFormatted)
    val selectedCalendarDate: StateFlow<String> = _selectedCalendarDate.asStateFlow()

    private val _calendarStrip = MutableStateFlow<List<CalendarDay>>(emptyList())
    val calendarStrip: StateFlow<List<CalendarDay>> = _calendarStrip.asStateFlow()

    // Health Connect status
    private val _healthConnectStatus = MutableStateFlow(healthConnectManager.checkAvailability())
    val healthConnectStatus: StateFlow<HealthConnectAvailability> = _healthConnectStatus.asStateFlow()

    private val _hasHealthPermissions = MutableStateFlow(false)
    val hasHealthPermissions: StateFlow<Boolean> = _hasHealthPermissions.asStateFlow()

    private val _syncedWeights = MutableStateFlow<List<WeightDataPoint>>(emptyList())
    val syncedWeights: StateFlow<List<WeightDataPoint>> = _syncedWeights.asStateFlow()

    private val _syncedSleep = MutableStateFlow<List<SleepDataPoint>>(emptyList())
    val syncedSleep: StateFlow<List<SleepDataPoint>> = _syncedSleep.asStateFlow()

    private val _healthConnectMessage = MutableStateFlow<String?>(null)
    val healthConnectMessage: StateFlow<String?> = _healthConnectMessage.asStateFlow()

    // Kick Counter State
    private val _kickCount = MutableStateFlow(0)
    val kickCount: StateFlow<Int> = _kickCount.asStateFlow()

    private val _kickTimerSeconds = MutableStateFlow(0L)
    val kickTimerSeconds: StateFlow<Long> = _kickTimerSeconds.asStateFlow()

    private val _isKickTimerRunning = MutableStateFlow(false)
    val isKickTimerRunning: StateFlow<Boolean> = _isKickTimerRunning.asStateFlow()

    private var kickTimerJob: Job? = null

    // Room Database Flows
    val pregnancyLogs: StateFlow<List<PregnancyLogEntity>> = dao.getAllPregnancyLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kickSessions: StateFlow<List<KickSessionEntity>> = dao.getAllKickSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val babyLogs: StateFlow<List<BabyLogEntity>> = dao.getAllBabyLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<JournalEntryEntity>> = dao.getAllJournalEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appointments: StateFlow<List<AppointmentEntity>> = dao.getAllAppointments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val checklistItems: StateFlow<List<ChecklistItemEntity>> = dao.getAllChecklistItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medicalContacts: StateFlow<List<com.example.data.local.MedicalContactEntity>> = dao.getAllMedicalContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cachedContacts: StateFlow<List<com.example.data.CachedContact>> = db.contactDao().getAllCachedContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = dao.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val walletDocuments: StateFlow<List<WalletDocumentEntity>> = dao.getAllWalletDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val glycemiaRecords: StateFlow<List<GlycemiaRecordEntity>> = dao.getAllGlycemiaRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        dateString: String = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE).format(Date()),
        receiptUri: String? = null,
        notes: String = ""
    ) {
        viewModelScope.launch {
            dao.insertExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    dateString = dateString,
                    receiptUri = receiptUri,
                    notes = notes
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            dao.deleteExpense(expense)
        }
    }

    fun addWalletDocument(
        title: String,
        documentType: String,
        holderName: String = "Fatou",
        documentNumber: String = "",
        expirationDate: String = "",
        photoUri: String? = null,
        photoBackUri: String? = null,
        googleDriveLink: String? = null,
        notes: String = ""
    ) {
        viewModelScope.launch {
            dao.insertWalletDocument(
                WalletDocumentEntity(
                    title = title,
                    documentType = documentType,
                    holderName = holderName,
                    documentNumber = documentNumber,
                    expirationDate = expirationDate,
                    photoUri = photoUri,
                    photoBackUri = photoBackUri,
                    googleDriveLink = googleDriveLink,
                    notes = notes
                )
            )
        }
    }

    fun deleteWalletDocument(doc: WalletDocumentEntity) {
        viewModelScope.launch {
            dao.deleteWalletDocument(doc)
        }
    }

    fun updateWalletDocument(doc: WalletDocumentEntity) {
        viewModelScope.launch {
            dao.updateWalletDocument(doc)
        }
    }

    fun addGlycemiaRecord(
        dateString: String = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date()),
        period: String,
        glucoseValueMgDl: Double,
        targetMaxMgDl: Double = if (period.contains("jeun", ignoreCase = true)) 95.0 else 120.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            dao.insertGlycemiaRecord(
                GlycemiaRecordEntity(
                    dateString = dateString,
                    period = period,
                    glucoseValueMgDl = glucoseValueMgDl,
                    targetMaxMgDl = targetMaxMgDl,
                    notes = notes
                )
            )
        }
    }

    fun deleteGlycemiaRecord(record: GlycemiaRecordEntity) {
        viewModelScope.launch {
            dao.deleteGlycemiaRecord(record)
        }
    }

    // Couple Hub: Contractions, Baby Names & Notes
    val contractions: StateFlow<List<ContractionEntity>> = dao.getAllContractions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val babyNames: StateFlow<List<BabyNameEntity>> = dao.getAllBabyNames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupleNotes: StateFlow<List<CoupleNoteEntity>> = dao.getAllCoupleNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Contraction Timer
    private val _isContractionRunning = MutableStateFlow(false)
    val isContractionRunning: StateFlow<Boolean> = _isContractionRunning.asStateFlow()

    private val _currentContractionSeconds = MutableStateFlow(0L)
    val currentContractionSeconds: StateFlow<Long> = _currentContractionSeconds.asStateFlow()

    private var contractionTimerJob: Job? = null
    private var contractionStartTimeMillis: Long = 0L

    fun startContractionTimer() {
        if (_isContractionRunning.value) return
        contractionStartTimeMillis = System.currentTimeMillis()
        _isContractionRunning.value = true
        _currentContractionSeconds.value = 0L
        contractionTimerJob = viewModelScope.launch {
            while (_isContractionRunning.value) {
                delay(1000L)
                _currentContractionSeconds.value += 1
            }
        }
    }

    fun stopAndSaveContraction(intensity: String = "MODEREE", notes: String = "") {
        if (!_isContractionRunning.value) return
        val endMillis = System.currentTimeMillis()
        val duration = _currentContractionSeconds.value
        _isContractionRunning.value = false
        contractionTimerJob?.cancel()

        val lastContraction = contractions.value.firstOrNull()
        val interval = if (lastContraction != null) {
            ((contractionStartTimeMillis - lastContraction.startTimestamp) / 1000L).coerceAtLeast(0L)
        } else 0L

        viewModelScope.launch {
            dao.insertContraction(
                ContractionEntity(
                    startTimestamp = contractionStartTimeMillis,
                    endTimestamp = endMillis,
                    durationSeconds = duration.coerceAtLeast(1L),
                    intervalSeconds = interval,
                    intensity = intensity,
                    notes = notes
                )
            )
            _currentContractionSeconds.value = 0L
        }
    }

    fun cancelContractionTimer() {
        _isContractionRunning.value = false
        contractionTimerJob?.cancel()
        _currentContractionSeconds.value = 0L
    }

    fun deleteContraction(contraction: ContractionEntity) {
        viewModelScope.launch {
            dao.deleteContraction(contraction)
        }
    }

    fun clearAllContractions() {
        viewModelScope.launch {
            dao.clearAllContractions()
        }
    }

    // Baby Names Shortlist (Fatou & Bala)
    fun addBabyName(
        name: String,
        gender: String,
        origin: String,
        fatouRating: Int,
        balaRating: Int,
        notes: String = ""
    ) {
        viewModelScope.launch {
            dao.insertBabyName(
                BabyNameEntity(
                    name = name.trim(),
                    gender = gender,
                    originOrMeaning = origin,
                    fatouRating = fatouRating,
                    balaRating = balaRating,
                    isFavorite = (fatouRating >= 4 && balaRating >= 4),
                    notes = notes
                )
            )
        }
    }

    fun updateBabyNameRating(entity: BabyNameEntity, fatouStars: Int, balaStars: Int) {
        viewModelScope.launch {
            dao.updateBabyName(
                entity.copy(
                    fatouRating = fatouStars,
                    balaRating = balaStars,
                    isFavorite = (fatouStars >= 4 && balaStars >= 4)
                )
            )
        }
    }

    fun toggleBabyNameFavorite(entity: BabyNameEntity) {
        viewModelScope.launch {
            dao.updateBabyName(entity.copy(isFavorite = !entity.isFavorite))
        }
    }

    fun deleteBabyName(entity: BabyNameEntity) {
        viewModelScope.launch {
            dao.deleteBabyName(entity)
        }
    }

    // Couple Notes (Sweet Words & Letters to Baby)
    fun addCoupleNote(author: String, recipient: String, title: String, message: String) {
        viewModelScope.launch {
            dao.insertCoupleNote(
                CoupleNoteEntity(
                    author = author,
                    recipient = recipient,
                    title = title,
                    message = message
                )
            )
        }
    }

    fun deleteCoupleNote(entity: CoupleNoteEntity) {
        viewModelScope.launch {
            dao.deleteCoupleNote(entity)
        }
    }

    // Baby Nursing / Sleep Active Timers
    private val _nursingSide = MutableStateFlow("LEFT")
    val nursingSide: StateFlow<String> = _nursingSide.asStateFlow()

    private val _nursingDurationSeconds = MutableStateFlow(0L)
    val nursingDurationSeconds: StateFlow<Long> = _nursingDurationSeconds.asStateFlow()

    private val _isNursingTimerRunning = MutableStateFlow(false)
    val isNursingTimerRunning: StateFlow<Boolean> = _isNursingTimerRunning.asStateFlow()

    private var nursingTimerJob: Job? = null

    // Daily Health Log Input Form State
    private val _todayWaterGlasses = MutableStateFlow(4)
    val todayWaterGlasses: StateFlow<Int> = _todayWaterGlasses.asStateFlow()

    private val _todayWeightKg = MutableStateFlow(64.5)
    val todayWeightKg: StateFlow<Double> = _todayWeightKg.asStateFlow()

    private val _todayMood = MutableStateFlow("Calm")
    val todayMood: StateFlow<String> = _todayMood.asStateFlow()

    private val _todaySymptoms = MutableStateFlow<Set<String>>(setOf("Mild fatigue"))
    val todaySymptoms: StateFlow<Set<String>> = _todaySymptoms.asStateFlow()

    // Symptom severity map: symptom name -> "Mild" / "Moderate" / "Severe"
    private val _symptomSeverities = MutableStateFlow<Map<String, String>>(
        mapOf("Mild fatigue" to "Mild")
    )
    val symptomSeverities: StateFlow<Map<String, String>> = _symptomSeverities.asStateFlow()

    // 24-Month Roadmap Checklist State
    private val _achievedMilestoneIds = MutableStateFlow<Set<String>>(setOf("m_0_1"))
    val achievedMilestoneIds: StateFlow<Set<String>> = _achievedMilestoneIds.asStateFlow()

    // Fertility Tracker State
    private val _cycleLengthDays = MutableStateFlow(28)
    val cycleLengthDays: StateFlow<Int> = _cycleLengthDays.asStateFlow()

    private val _periodDaysAgo = MutableStateFlow(12)
    val periodDaysAgo: StateFlow<Int> = _periodDaysAgo.asStateFlow()

    // AI Assistant Chat Messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Bonjour! Welcome to Maman & bébé Pregnancy Tracker. How can I support you and your baby today?",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAssistantThinking = MutableStateFlow(false)
    val isAssistantThinking: StateFlow<Boolean> = _isAssistantThinking.asStateFlow()

    init {
        build14DayCalendarStrip()
        bootstrapDefaultData()
        refreshHealthConnectState()
    }

    private fun build14DayCalendarStrip() {
        val list = mutableListOf<CalendarDay>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6) // 6 days before today

        val dayFmt = SimpleDateFormat("d", Locale.getDefault())
        val dayOfWeekFmt = SimpleDateFormat("EEE", Locale.getDefault())
        val fullFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val todayStr = fullFmt.format(Date())

        for (i in 0 until 14) {
            val date = cal.time
            val dateStr = fullFmt.format(date)
            list.add(
                CalendarDay(
                    dateString = dateStr,
                    dayOfMonth = dayFmt.format(date),
                    dayOfWeek = dayOfWeekFmt.format(date),
                    fullDate = date,
                    isToday = dateStr == todayStr
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        _calendarStrip.value = list
    }

    private fun bootstrapDefaultData() {
        viewModelScope.launch {
            try {
                if (dao.getChecklistCount() == 0) {
                    val entities = ChecklistData.defaultItems.map {
                        ChecklistItemEntity(
                            category = it.category,
                            title = it.title,
                            isChecked = false,
                            isCustom = false
                        )
                    }
                    dao.insertChecklistItems(entities)
                }

                launch {
                    try {
                        dao.getAllAppointments().collect { list ->
                            if (list.isEmpty()) {
                                PrenatalAgendaData.defaultAgenda.forEach { agenda ->
                                    dao.insertAppointment(
                                        AppointmentEntity(
                                            title = agenda.title,
                                            weekSuggested = agenda.targetWeek,
                                            dateTimestamp = System.currentTimeMillis() + (agenda.targetWeek - _currentWeekNumber.value).coerceAtLeast(1) * 7L * 24 * 3600 * 1000,
                                            doctorName = "Dr. Laurent (OB/GYN)",
                                            location = "Maternal Health Center",
                                            notes = agenda.description,
                                            isCompleted = false
                                        )
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("PregnancyViewModel", "Error collecting appointments", e)
                    }
                }

                launch {
                    try {
                        dao.getAllBabyNames().collect { list ->
                            if (list.isEmpty()) {
                                dao.insertBabyName(
                                    BabyNameEntity(
                                        name = "Amina",
                                        gender = "FILLE",
                                        originOrMeaning = "Fidèle, digne de confiance & douce",
                                        fatouRating = 5,
                                        balaRating = 5,
                                        isFavorite = true,
                                        notes = "Notre coup de cœur commun !"
                                    )
                                )
                                dao.insertBabyName(
                                    BabyNameEntity(
                                        name = "Ibrahim",
                                        gender = "GARCON",
                                        originOrMeaning = "Père des nations, noble & protecteur",
                                        fatouRating = 5,
                                        balaRating = 5,
                                        isFavorite = true,
                                        notes = "Très beau prénom cher à Bala"
                                    )
                                )
                                dao.insertBabyName(
                                    BabyNameEntity(
                                        name = "Mariam",
                                        gender = "FILLE",
                                        originOrMeaning = "Gracieuse, pure & lumineuse",
                                        fatouRating = 4,
                                        balaRating = 5,
                                        isFavorite = true,
                                        notes = "Doux et mélodieux"
                                    )
                                )
                            }
                        }
                    } catch (_: Exception) {}
                }

                launch {
                    try {
                        dao.getAllCoupleNotes().collect { list ->
                            if (list.isEmpty()) {
                                dao.insertCoupleNote(
                                    CoupleNoteEntity(
                                        author = "Bala",
                                        recipient = "Fatou",
                                        title = "Tu es formidable, ma femme ❤️",
                                        message = "Chaque jour qui passe à tes côtés et avec notre bébé me remplit de fierté. Tu portes la vie avec tant de grâce et de courage. Je suis là avec toi à chaque instant."
                                    )
                                )
                                dao.insertCoupleNote(
                                    CoupleNoteEntity(
                                        author = "Fatou & Bala",
                                        recipient = "Bébé",
                                        title = "Lettre d'amour à notre trésor 👶",
                                        message = "À notre petit amour qui grandit au chaud à la semaine 24, papa et maman ont hâte de te serrer dans leurs bras. La chambre est presque prête, et nous t'aimons déjà infiniment."
                                    )
                                )
                            }
                        }
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                android.util.Log.e("PregnancyViewModel", "Error bootstrapping default data", e)
            }
        }
    }

    // Dynamic Due Date Engine
    fun updateDueDate(newDueDateMillis: Long) {
        _dueDateMillis.value = newDueDateMillis
        prefs.edit().putLong("pref_due_date", newDueDateMillis).apply()
        val diffDays = ((newDueDateMillis - System.currentTimeMillis()) / (1000L * 3600 * 24)).toInt()
        val daysElapsed = 280 - diffDays
        val calculatedWeek = (daysElapsed / 7).coerceIn(1, 42)
        setWeek(calculatedWeek)
    }

    fun setWeek(week: Int) {
        val clamped = week.coerceIn(1, 42)
        _currentWeekNumber.value = clamped
        _currentWeekData.value = PregnancyWeeksData.getWeek(clamped)
        prefs.edit().putInt("pref_current_week", clamped).apply()
    }

    fun toggleTwinMode() {
        val updated = !_isTwinMode.value
        _isTwinMode.value = updated
        prefs.edit().putBoolean("pref_is_twin", updated).apply()
    }

    fun setSizeTheme(theme: SizeTheme) {
        _selectedSizeTheme.value = theme
        prefs.edit().putString("pref_size_theme", theme.name).apply()
    }

    fun selectCalendarDate(dateString: String) {
        _selectedCalendarDate.value = dateString
    }

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    // Privacy PIN & Security Methods
    fun setPin(pin: String) {
        prefs.edit()
            .putString("pref_pin_code", pin)
            .putBoolean("pref_pin_enabled", true)
            .apply()
        _isPinEnabled.value = true
        _isAppLocked.value = false
        _pinResetStatus.value = "PIN protection activated successfully"
    }

    fun disablePin() {
        prefs.edit()
            .remove("pref_pin_code")
            .putBoolean("pref_pin_enabled", false)
            .apply()
        _isPinEnabled.value = false
        _isAppLocked.value = false
        _pinResetStatus.value = "PIN protection disabled"
    }

    fun verifyPin(inputPin: String): Boolean {
        val savedPin = prefs.getString("pref_pin_code", "") ?: ""
        return inputPin == savedPin
    }

    fun unlockAppWithPin(inputPin: String): Boolean {
        val savedPin = prefs.getString("pref_pin_code", "") ?: ""
        return if (inputPin == savedPin) {
            _isAppLocked.value = false
            true
        } else {
            false
        }
    }

    fun unlockWithBiometric() {
        _isAppLocked.value = false
    }

    fun lockApp() {
        if (_isPinEnabled.value) {
            _isAppLocked.value = true
        }
    }

    fun requestPinResetCode() {
        val code = (100000..999999).random().toString()
        _pinResetCode.value = code
        _pinResetStatus.value = "Reset code ($code) sent to ${_userEmail.value}"
    }

    fun verifyResetCodeAndResetPin(code: String, newPin: String): Boolean {
        val currentCode = _pinResetCode.value
        return if (currentCode != null && code.trim() == currentCode.trim()) {
            setPin(newPin)
            _pinResetCode.value = null
            _pinResetStatus.value = "PIN has been reset successfully"
            true
        } else {
            false
        }
    }

    fun clearPinResetStatus() {
        _pinResetStatus.value = null
    }

    // Bump Milestone Photo Persistence
    fun saveBumpPhoto(bitmap: Bitmap, week: Int, title: String = "", notes: String = "") {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val dir = java.io.File(getApplication<Application>().filesDir, "bump_photos")
                if (!dir.exists()) dir.mkdirs()
                val filename = "bump_wk_${week}_${System.currentTimeMillis()}.jpg"
                val file = java.io.File(dir, filename)
                java.io.FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
                }
                val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                dao.insertJournalEntry(
                    JournalEntryEntity(
                        weekNumber = week,
                        dateString = dateFmt,
                        timestamp = System.currentTimeMillis(),
                        title = title.ifBlank { "Week $week Bump Photo" },
                        reflection = notes.ifBlank { "Captured on $dateFmt • Week $week" },
                        photoUri = file.absolutePath
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveBumpPhotoUri(uriString: String, week: Int, title: String = "", notes: String = "") {
        viewModelScope.launch {
            val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
            dao.insertJournalEntry(
                JournalEntryEntity(
                    weekNumber = week,
                    dateString = dateFmt,
                    timestamp = System.currentTimeMillis(),
                    title = title.ifBlank { "Week $week Bump Photo" },
                    reflection = notes.ifBlank { "Added from gallery on $dateFmt" },
                    photoUri = uriString
                )
            )
        }
    }

    // Health Connect Actions
    fun refreshHealthConnectState() {
        viewModelScope.launch {
            _healthConnectStatus.value = healthConnectManager.checkAvailability()
            val hasPerms = healthConnectManager.hasAllPermissions()
            _hasHealthPermissions.value = hasPerms
            if (hasPerms) {
                loadHealthConnectData()
            }
        }
    }

    fun onHealthPermissionsResult(granted: Boolean) {
        _hasHealthPermissions.value = granted
        if (granted) {
            _healthConnectMessage.value = "Health Connect connected securely!"
            loadHealthConnectData()
        } else {
            _healthConnectMessage.value = "Health Connect permissions were not granted."
        }
    }

    fun clearHealthMessage() {
        _healthConnectMessage.value = null
    }

    private fun loadHealthConnectData() {
        viewModelScope.launch {
            healthConnectManager.readRecentWeights(days = 60).onSuccess { list ->
                _syncedWeights.value = list
            }
            healthConnectManager.readRecentSleepSessions(days = 14).onSuccess { list ->
                _syncedSleep.value = list
            }
        }
    }

    // Kick Counter
    fun registerKick() {
        _kickCount.value += 1
        triggerHaptic()
        if (!_isKickTimerRunning.value) {
            startKickTimer()
        }
    }

    fun startKickTimer() {
        _isKickTimerRunning.value = true
        kickTimerJob?.cancel()
        kickTimerJob = viewModelScope.launch {
            while (_isKickTimerRunning.value) {
                delay(1000)
                _kickTimerSeconds.value += 1
            }
        }
    }

    fun pauseKickTimer() {
        _isKickTimerRunning.value = false
        kickTimerJob?.cancel()
    }

    fun resetKickCounter() {
        pauseKickTimer()
        _kickCount.value = 0
        _kickTimerSeconds.value = 0L
    }

    fun saveKickSession(notes: String = "") {
        val count = _kickCount.value
        val duration = _kickTimerSeconds.value
        val reached = count >= 10
        viewModelScope.launch {
            dao.insertKickSession(
                KickSessionEntity(
                    timestamp = System.currentTimeMillis(),
                    durationSeconds = duration,
                    kickCount = count,
                    targetReached = reached,
                    notes = notes
                )
            )
            resetKickCounter()
        }
    }

    private fun triggerHaptic() {
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(VibratorManager::class.java)
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Vibrator::class.java)
                vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (_: Exception) {}
    }

    // Daily Pregnancy Health Log
    fun incrementWater() {
        _todayWaterGlasses.value += 1
        if (_hasHealthPermissions.value) {
            viewModelScope.launch {
                healthConnectManager.writeHydration(0.25)
            }
        }
    }

    fun decrementWater() {
        if (_todayWaterGlasses.value > 0) {
            _todayWaterGlasses.value -= 1
        }
    }

    fun setWeight(weight: Double) {
        _todayWeightKg.value = weight
    }

    fun setTodayWeight(weight: Double) = setWeight(weight)

    fun setTodayWater(glasses: Int) {
        _todayWaterGlasses.value = glasses.coerceAtLeast(0)
    }

    fun setMood(mood: String) {
        _todayMood.value = mood
    }

    fun setTodayMood(mood: String) = setMood(mood)

    fun saveDailyHealthLog(bp: String = "", notes: String = "") = saveTodayLog(bp, notes)

    fun toggleSymptom(symptom: String) {
        toggleSymptomWithSeverity(symptom)
    }

    fun toggleSymptomWithSeverity(symptom: String, severity: String = "Mild") {
        val current = _todaySymptoms.value.toMutableSet()
        val severities = _symptomSeverities.value.toMutableMap()
        if (symptom in current) {
            current.remove(symptom)
            severities.remove(symptom)
        } else {
            current.add(symptom)
            severities[symptom] = severity
        }
        _todaySymptoms.value = current
        _symptomSeverities.value = severities
    }

    fun saveTodayLog(bp: String = "", notes: String = "") {
        viewModelScope.launch {
            val weight = _todayWeightKg.value
            val glasses = _todayWaterGlasses.value
            val mood = _todayMood.value
            val symptomsWithSeverityStr = _todaySymptoms.value.joinToString(", ") { symptom ->
                val sev = _symptomSeverities.value[symptom] ?: "Mild"
                "$symptom ($sev)"
            }

            var synced = false
            if (_hasHealthPermissions.value && weight > 0) {
                val result = healthConnectManager.writeWeight(weight)
                synced = result.isSuccess
                if (synced) loadHealthConnectData()
            }

            dao.insertOrUpdatePregnancyLog(
                PregnancyLogEntity(
                    dateString = _selectedCalendarDate.value,
                    timestamp = System.currentTimeMillis(),
                    weightKg = weight,
                    waterGlasses = glasses,
                    mood = mood,
                    symptoms = symptomsWithSeverityStr,
                    bloodPressure = bp,
                    notes = notes,
                    syncedHealthConnect = synced
                )
            )
            _healthConnectMessage.value = if (synced) "Saved & synced with Health Connect!" else "Daily entry saved!"
        }
    }

    // Baby Postpartum Logger
    fun toggleNursingTimer(side: String) {
        if (_isNursingTimerRunning.value) {
            if (_nursingSide.value == side) {
                _isNursingTimerRunning.value = false
                nursingTimerJob?.cancel()
            } else {
                _nursingSide.value = side
            }
        } else {
            _nursingSide.value = side
            _isNursingTimerRunning.value = true
            nursingTimerJob?.cancel()
            nursingTimerJob = viewModelScope.launch {
                while (_isNursingTimerRunning.value) {
                    delay(1000)
                    _nursingDurationSeconds.value += 1
                }
            }
        }
    }

    fun saveNursingSession(notes: String = "") {
        _isNursingTimerRunning.value = false
        nursingTimerJob?.cancel()
        val duration = _nursingDurationSeconds.value
        val side = _nursingSide.value
        viewModelScope.launch {
            dao.insertBabyLog(
                BabyLogEntity(
                    logType = "NURSING",
                    detail = "$side Breast",
                    durationSeconds = duration,
                    amountMl = 0,
                    notes = notes
                )
            )
            _nursingDurationSeconds.value = 0L
        }
    }

    fun logBottleFeeding(amountMl: Int, formula: Boolean, notes: String = "") {
        viewModelScope.launch {
            dao.insertBabyLog(
                BabyLogEntity(
                    logType = "BOTTLE",
                    detail = if (formula) "Formula" else "Breastmilk",
                    durationSeconds = 0,
                    amountMl = amountMl,
                    notes = notes
                )
            )
        }
    }

    fun logDiaper(type: String, notes: String = "") {
        viewModelScope.launch {
            dao.insertBabyLog(
                BabyLogEntity(
                    logType = "DIAPER",
                    detail = type,
                    notes = notes
                )
            )
        }
    }

    fun logPumping(amountMl: Int, notes: String = "") {
        viewModelScope.launch {
            dao.insertBabyLog(
                BabyLogEntity(
                    logType = "PUMPING",
                    detail = "Pumping ($amountMl ml)",
                    amountMl = amountMl,
                    notes = notes
                )
            )
        }
    }

    fun logSleepSession(durationMinutes: Long, isBaby: Boolean = true, notes: String = "") {
        viewModelScope.launch {
            val endTime = Instant.now()
            val startTime = endTime.minusSeconds(durationMinutes * 60)
            var synced = false

            if (_hasHealthPermissions.value) {
                val title = if (isBaby) "Baby Nap" else "Maternal Sleep"
                val res = healthConnectManager.writeSleepSession(startTime, endTime, title)
                synced = res.isSuccess
                if (synced) loadHealthConnectData()
            }

            dao.insertBabyLog(
                BabyLogEntity(
                    logType = "SLEEP",
                    detail = if (isBaby) "Baby Sleep ($durationMinutes min)" else "Mom Sleep ($durationMinutes min)",
                    durationSeconds = durationMinutes * 60,
                    notes = notes,
                    syncedHealthConnect = synced
                )
            )
            _healthConnectMessage.value = if (synced) "Sleep synced to Health Connect!" else "Sleep recorded!"
        }
    }

    // 24-Month Roadmap
    fun toggleMilestone(milestoneId: String) {
        val current = _achievedMilestoneIds.value.toMutableSet()
        if (milestoneId in current) {
            current.remove(milestoneId)
        } else {
            current.add(milestoneId)
        }
        _achievedMilestoneIds.value = current
    }

    // Fertility Tracker
    fun setCycleLength(days: Int) {
        _cycleLengthDays.value = days.coerceIn(21, 45)
    }

    fun setPeriodDaysAgo(days: Int) {
        _periodDaysAgo.value = days.coerceIn(0, 45)
    }

    // Bump Journal & Gallery Management
    fun addJournalEntry(
        weekNumber: Int,
        title: String,
        reflection: String,
        photoUri: String? = null,
        category: String = "Belly Bump",
        audioUri: String? = null,
        videoUri: String? = null
    ) {
        viewModelScope.launch {
            dao.insertJournalEntry(
                JournalEntryEntity(
                    weekNumber = weekNumber,
                    dateString = _selectedCalendarDate.value,
                    title = title,
                    reflection = reflection,
                    photoUri = photoUri,
                    category = category,
                    audioUri = audioUri,
                    videoUri = videoUri
                )
            )
        }
    }

    fun updateJournalEntry(entry: JournalEntryEntity) {
        viewModelScope.launch {
            dao.updateJournalEntry(entry)
        }
    }

    fun deleteJournalEntry(entry: JournalEntryEntity) {
        viewModelScope.launch {
            dao.deleteJournalEntry(entry)
        }
    }

    fun savePhotoToDeviceGallery(context: Context, photoUriStr: String?) {
        if (photoUriStr.isNullOrBlank()) return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val uri = Uri.parse(photoUriStr)
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                val filename = "MamanBebe_${System.currentTimeMillis()}.jpg"
                var fos: java.io.OutputStream? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/MamanBebe")
                    }
                    val imageUri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (imageUri != null) {
                        fos = resolver.openOutputStream(imageUri)
                    }
                } else {
                    val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
                    val appDir = java.io.File(imagesDir, "MamanBebe")
                    if (!appDir.exists()) appDir.mkdirs()
                    val image = java.io.File(appDir, filename)
                    fos = java.io.FileOutputStream(image)
                }
                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)
                    android.os.Handler(context.mainLooper).post {
                        android.widget.Toast.makeText(context, "Photo saved to device Gallery!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.os.Handler(context.mainLooper).post {
                    android.widget.Toast.makeText(context, "Failed to save photo: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sharePhoto(context: Context, photoUriStr: String?, caption: String) {
        if (photoUriStr.isNullOrBlank()) return
        try {
            val uri = Uri.parse(photoUriStr)
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                putExtra(android.content.Intent.EXTRA_TEXT, "Maman & Bébé Memory: $caption")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "Share Photo via"))
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Failed to share photo: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun cropAndSavePhoto(
        context: Context,
        originalUriStr: String,
        aspectRatio: String, // "1:1", "4:5", "16:9", "ORIGINAL"
        rotationAngle: Float,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val uri = Uri.parse(originalUriStr)
                val inputStream = if (originalUriStr.startsWith("/")) {
                    java.io.FileInputStream(java.io.File(originalUriStr))
                } else {
                    context.contentResolver.openInputStream(uri)
                } ?: return@launch

                val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                if (originalBitmap == null) {
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "Cannot read photo bitmap to crop", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Apply rotation
                val matrix = android.graphics.Matrix().apply {
                    if (rotationAngle != 0f) postRotate(rotationAngle)
                }
                val rotatedBitmap = android.graphics.Bitmap.createBitmap(
                    originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true
                )

                // Calculate crop bounds based on chosen aspect ratio
                val targetRatio = when (aspectRatio) {
                    "1:1" -> 1.0f
                    "4:5" -> 0.8f
                    "16:9" -> 16f / 9f
                    else -> rotatedBitmap.width.toFloat() / rotatedBitmap.height.toFloat()
                }

                val currentRatio = rotatedBitmap.width.toFloat() / rotatedBitmap.height.toFloat()
                var cropW = rotatedBitmap.width
                var cropH = rotatedBitmap.height
                var startX = 0
                var startY = 0

                if (currentRatio > targetRatio) {
                    cropW = (rotatedBitmap.height * targetRatio).toInt().coerceAtMost(rotatedBitmap.width)
                    startX = (rotatedBitmap.width - cropW) / 2
                } else if (currentRatio < targetRatio) {
                    cropH = (rotatedBitmap.width / targetRatio).toInt().coerceAtMost(rotatedBitmap.height)
                    startY = (rotatedBitmap.height - cropH) / 2
                }

                val croppedBitmap = android.graphics.Bitmap.createBitmap(
                    rotatedBitmap, startX, startY, cropW, cropH
                )

                val dir = java.io.File(getApplication<Application>().filesDir, "bump_photos")
                if (!dir.exists()) dir.mkdirs()
                val croppedFile = java.io.File(dir, "crop_${System.currentTimeMillis()}.jpg")
                java.io.FileOutputStream(croppedFile).use { out ->
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
                }

                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onSuccess(croppedFile.absolutePath)
                    android.widget.Toast.makeText(context, "Photo cropped & saved!", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Failed to crop: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Checklist
    fun toggleChecklistItem(item: ChecklistItemEntity) {
        viewModelScope.launch {
            dao.updateChecklistItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun addCustomChecklistItem(category: String, title: String) {
        viewModelScope.launch {
            dao.insertChecklistItem(
                ChecklistItemEntity(
                    category = category,
                    title = title,
                    isChecked = false,
                    isCustom = true
                )
            )
        }
    }

    fun addChecklistItem(title: String, category: String) {
        addCustomChecklistItem(category, title)
    }

    // Appointments
    fun addAppointment(title: String, doctor: String, location: String, dateTimestamp: Long, notes: String) {
        viewModelScope.launch {
            dao.insertAppointment(
                AppointmentEntity(
                    title = title,
                    doctorName = doctor,
                    location = location,
                    dateTimestamp = dateTimestamp,
                    notes = notes
                )
            )
        }
    }

    fun toggleAppointmentCompleted(app: AppointmentEntity) {
        viewModelScope.launch {
            dao.updateAppointment(app.copy(isCompleted = !app.isCompleted))
        }
    }

    fun updateAppointment(app: AppointmentEntity) {
        viewModelScope.launch {
            dao.updateAppointment(app)
        }
    }

    fun deleteAppointment(app: AppointmentEntity) {
        viewModelScope.launch {
            dao.deleteAppointment(app)
        }
    }

    fun addMedicalContact(type: String, name: String, phone: String, address: String, notes: String) {
        viewModelScope.launch {
            dao.insertMedicalContact(
                com.example.data.local.MedicalContactEntity(
                    type = type,
                    name = name,
                    phone = phone,
                    address = address,
                    notes = notes
                )
            )
        }
    }

    fun updateMedicalContact(contact: com.example.data.local.MedicalContactEntity) {
        viewModelScope.launch {
            dao.updateMedicalContact(contact)
        }
    }

    fun deleteMedicalContact(contact: com.example.data.local.MedicalContactEntity) {
        viewModelScope.launch {
            dao.deleteMedicalContact(contact)
        }
    }

    fun addCachedContact(type: String, name: String, phone: String, address: String, notes: String) {
        viewModelScope.launch {
            val id = "contact_${System.currentTimeMillis()}"
            db.contactDao().insertSingleContact(
                com.example.data.CachedContact(
                    id = id,
                    type = type,
                    name = name,
                    phone = phone,
                    address = address,
                    notes = notes
                )
            )
        }
    }

    fun updateCachedContact(contact: com.example.data.CachedContact) {
        viewModelScope.launch {
            db.contactDao().updateContact(contact)
        }
    }

    fun deleteCachedContact(contact: com.example.data.CachedContact) {
        viewModelScope.launch {
            db.contactDao().deleteContact(contact)
        }
    }

    // AI Assistant
    fun askAssistant(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = ChatMessage(text = prompt, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAssistantThinking.value = true

        viewModelScope.launch {
            val result = assistantClient.askAssistant(
                prompt = prompt,
                currentWeek = _currentWeekNumber.value
            )
            _isAssistantThinking.value = false
            val replyText = result.getOrElse {
                "I am here to support you. Please make sure to discuss any medical concerns with your obstetrician or midwife."
            }
            _chatMessages.value = _chatMessages.value + ChatMessage(text = replyText, isUser = false)
        }
    }
}
