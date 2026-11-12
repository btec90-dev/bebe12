package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class IvfPhaseStatus {
    COMPLETED,
    ACTIVE,
    UPCOMING
}

data class IvfPhaseTask(
    val id: String,
    val title: String,
    val detail: String,
    val isCompleted: Boolean = false
)

data class IvfMedicationInstruction(
    val medicationName: String,
    val dosage: String,
    val scheduleTime: String,
    val route: String,
    val instructionSummary: String,
    val cautionNote: String,
    val isDoseTakenToday: Boolean
)

data class IvfPhase(
    val id: String,
    val phaseNumber: Int,
    val title: String,
    val subtitle: String,
    val durationLabel: String,
    val status: IvfPhaseStatus,
    val description: String,
    val medications: List<String>,
    val tasks: List<IvfPhaseTask>,
    val clinicalTip: String,
    val isExpanded: Boolean
)

data class IvfUiState(
    val protocolTitle: String,
    val currentPhaseNumber: Int,
    val cycleDay: Int,
    val medicationInstruction: IvfMedicationInstruction,
    val phases: List<IvfPhase>
)

private data class IvfPhaseTemplate(
    val id: String,
    val phaseNumber: Int,
    val title: String,
    val subtitle: String,
    val durationLabel: String,
    val description: String,
    val medications: List<String>,
    val tasks: List<IvfPhaseTask>,
    val clinicalTip: String,
    val defaultMedication: IvfMedicationInstruction
)

class IVFViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("maman_bebe_ivf_prefs", Context.MODE_PRIVATE)
    private val todayKey: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private val phaseTemplates = listOf(
        IvfPhaseTemplate(
            id = "phase_1",
            phaseNumber = 1,
            title = "Préparation & Blocage Ovarien",
            subtitle = "Synchronisation du cycle et bilan de départ",
            durationLabel = "Jours 1 – 14",
            description = "Mise au repos des ovaires afin d'obtenir une cohorte folliculaire homogène avant le démarrage de la stimulation.",
            medications = listOf("Acide Folique 400 µg", "Provames 2 mg (Œstradiol)", "Décapeptyl 0,1 mg"),
            tasks = listOf(
                IvfPhaseTask("p1_t1", "Bilan hormonal (J2–J3)", "Dosage FSH, LH, Œstradiol (E2) et AMH à jeun."),
                IvfPhaseTask("p1_t2", "Échographie de comptage antral", "Vérification du repos ovarien et de l'épaisseur endométriale."),
                IvfPhaseTask("p1_t3", "Validation du consentement PMA", "Dossier administratif et sérologies à jour pour le couple.")
            ),
            clinicalTip = "Prenez l'acide folique chaque matin à la même heure pour optimiser la préparation ovocytaire et embryonnaire.",
            defaultMedication = IvfMedicationInstruction(
                medicationName = "Provames 2 mg + Acide Folique",
                dosage = "1 comprimé matin & soir",
                scheduleTime = "08h00 & 20h00",
                route = "Voie orale",
                instructionSummary = "Prendre avec un grand verre d'eau pendant ou après le repas pour préparer l'endomètre et synchroniser le cycle.",
                cautionNote = "Ne pas doubler la dose en cas d'oubli ; contacter l'équipe PMA si retard > 12h.",
                isDoseTakenToday = false
            )
        ),
        IvfPhaseTemplate(
            id = "phase_2",
            phaseNumber = 2,
            title = "Stimulation Ovarienne Contrôlée",
            subtitle = "Croissance multi-folliculaire & monitorage",
            durationLabel = "Jours 1 – 12",
            description = "Injections quotidiennes d'hormones FSH/LH pour stimuler la maturation de plusieurs follicules, suivies par échographies et prises de sang.",
            medications = listOf("Gonal-F 150 UI", "Fyremadel 0,25 mg (Antagoniste)", "Ovitrelle 250 µg (Déclenchement)"),
            tasks = listOf(
                IvfPhaseTask("p2_t1", "Injection quotidienne du soir", "Injecter Gonal-F à heure fixe entre 19h00 et 21h00."),
                IvfPhaseTask("p2_t2", "Monitorage folliculaire #1 & #2", "Échographie endovaginale + dosage E2, LH et Progestérone."),
                IvfPhaseTask("p2_t3", "Piqûre de déclenchement (Ovitrelle)", "Injection stricte à l'heure précise indiquée par le médecin (36h avant ponction).")
            ),
            clinicalTip = "Alternez chaque soir le côté du bas-ventre (gauche/droite) et sortez le stylo du réfrigérateur 15 minutes avant l'injection.",
            defaultMedication = IvfMedicationInstruction(
                medicationName = "Gonal-F (Follitropine alfa)",
                dosage = "150 UI • 1 fois par jour",
                scheduleTime = "20h00 (Heure fixe)",
                route = "Injection sous-cutanée",
                instructionSummary = "Injecter dans le bas-ventre (à 3 cm du nombril) en alternant les côtés chaque soir. Conserver au frais entre 2°C et 8°C.",
                cautionNote = "Respectez l'horaire à ±30 min près. Ajoutez Fyremadel le matin dès J6 selon l'avis du centre.",
                isDoseTakenToday = false
            )
        ),
        IvfPhaseTemplate(
            id = "phase_3",
            phaseNumber = 3,
            title = "Ponction & Fécondation en Laboratoire",
            subtitle = "Recueil ovocytaire, recueil sperme & culture J5",
            durationLabel = "Jours 13 – 18",
            description = "Prélèvement des ovocytes sous contrôle échographique (36h après Ovitrelle), suivi de la mise en fécondation (FIV/ICSI) et culture embryonnaire.",
            medications = listOf("Ovitrelle (36h avant)", "Antiseptique local", "Antalgique prescrit post-ponction"),
            tasks = listOf(
                IvfPhaseTask("p3_t1", "Jeûne strict avant ponction", "À jeun (sans boire, manger ni fumer) dès minuit la veille."),
                IvfPhaseTask("p3_t2", "Recueil conjoint & ponction ovocytaire", "Présence du conjoint Bala avec pièce d'identité pour le recueil."),
                IvfPhaseTask("p3_t3", "Suivi embryologique (J1 à J5)", "Appel du biologiste pour le nombre d'ovocytes fécondés et de blastocystes.")
            ),
            clinicalTip = "Prévoyez une journée de repos complet après la ponction et surveillez toute douleur abdominale inhabituelle.",
            defaultMedication = IvfMedicationInstruction(
                medicationName = "Paracétamol 1g + Spasfon (Post-Ponction)",
                dosage = "1 comprimé si besoin (max 3/jour)",
                scheduleTime = "Toutes les 6 heures",
                route = "Voie orale",
                instructionSummary = "Hydratez-vous régulièrement (1,5 L/jour) et prenez l'antalgique prescrit si des tiraillements pelviens apparaissent après la ponction.",
                cautionNote = "Évitez l'aspirine ou les anti-inflammatoires sans accord médical. Prévenez le centre si fièvre > 38°C.",
                isDoseTakenToday = false
            )
        ),
        IvfPhaseTemplate(
            id = "phase_4",
            phaseNumber = 4,
            title = "Transfert Embryonnaire & Soutien Lutéal",
            subtitle = "Implantation, progestérone & test bêta-hCG",
            durationLabel = "Jours 19 – 32",
            description = "Dépôt doux de l'embryon dans la cavité utérine sous contrôle échographique, suivi du traitement de soutien de la muqueuse jusqu'au test de grossesse.",
            medications = listOf("Utrogestan 200 mg (Progestérone)", "Aspegic Nourrisson 100 mg", "Acide Folique 400 µg"),
            tasks = listOf(
                IvfPhaseTask("p4_t1", "Vessie semi-pleine le jour du transfert", "Boire 500 ml d'eau 45 minutes avant le rendez-vous."),
                IvfPhaseTask("p4_t2", "Soutien lutéal quotidien", "Poursuivre la progestérone sans interruption jusqu'au résultat."),
                IvfPhaseTask("p4_t3", "Prise de sang Bêta-hCG (J12–J14)", "Dosage sanguin quantitatif pour confirmer le début de grossesse.")
            ),
            clinicalTip = "Ne modifiez jamais votre dose de progestérone avant le résultat de la prise de sang, même en cas de petits saignements.",
            defaultMedication = IvfMedicationInstruction(
                medicationName = "Utrogestan 200 mg (Progestérone)",
                dosage = "1 capsule matin, midi & soir (600 mg/j)",
                scheduleTime = "08h00 • 14h00 • 22h00",
                route = "Voie vaginale",
                instructionSummary = "Insérer profondément 3 fois par jour selon votre ordonnance. Allongez-vous 15 minutes après la prise du soir pour une absorption optimale.",
                cautionNote = "Traitement indispensable pour maintenir la muqueuse utérine réceptive. À continuer sans interruption.",
                isDoseTakenToday = false
            )
        )
    )

    private var activePhaseNumber: Int = prefs.getInt("ivf_active_phase", 2).coerceIn(1, 4)
    private var cycleDay: Int = prefs.getInt("ivf_cycle_day", 6).coerceIn(1, 35)
    private var expandedPhaseIds: Set<String> = prefs.getStringSet("ivf_expanded_ids", null)
        ?: setOf("phase_$activePhaseNumber")
    private var completedTaskIds: Set<String> = prefs.getStringSet("ivf_completed_tasks", null)
        ?: setOf("p1_t1", "p1_t2", "p1_t3")
    private var doseTakenToday: Boolean = prefs.getString("ivf_dose_taken_date", "") == todayKey

    private val _uiState = MutableStateFlow(buildUiState())
    val uiState: StateFlow<IvfUiState> = _uiState.asStateFlow()

    private fun buildUiState(): IvfUiState {
        val activeTemplate = phaseTemplates.firstOrNull { it.phaseNumber == activePhaseNumber }
            ?: phaseTemplates[1]

        val phases = phaseTemplates.map { template ->
            val status = when {
                template.phaseNumber < activePhaseNumber -> IvfPhaseStatus.COMPLETED
                template.phaseNumber == activePhaseNumber -> IvfPhaseStatus.ACTIVE
                else -> IvfPhaseStatus.UPCOMING
            }
            val mappedTasks = template.tasks.map { task ->
                task.copy(isCompleted = completedTaskIds.contains(task.id))
            }
            IvfPhase(
                id = template.id,
                phaseNumber = template.phaseNumber,
                title = template.title,
                subtitle = template.subtitle,
                durationLabel = template.durationLabel,
                status = status,
                description = template.description,
                medications = template.medications,
                tasks = mappedTasks,
                clinicalTip = template.clinicalTip,
                isExpanded = expandedPhaseIds.contains(template.id)
            )
        }

        return IvfUiState(
            protocolTitle = "Protocole Antagoniste • Parcours FIV",
            currentPhaseNumber = activePhaseNumber,
            cycleDay = cycleDay,
            medicationInstruction = activeTemplate.defaultMedication.copy(
                isDoseTakenToday = doseTakenToday
            ),
            phases = phases
        )
    }

    private fun emitUpdatedState() {
        _uiState.value = buildUiState()
    }

    fun togglePhaseExpanded(phaseId: String) {
        expandedPhaseIds = if (expandedPhaseIds.contains(phaseId)) {
            expandedPhaseIds - phaseId
        } else {
            expandedPhaseIds + phaseId
        }
        prefs.edit().putStringSet("ivf_expanded_ids", expandedPhaseIds).apply()
        emitUpdatedState()
    }

    fun setActivePhase(phaseNumber: Int) {
        val clamped = phaseNumber.coerceIn(1, 4)
        activePhaseNumber = clamped
        expandedPhaseIds = expandedPhaseIds + "phase_$clamped"
        doseTakenToday = false
        prefs.edit()
            .putInt("ivf_active_phase", activePhaseNumber)
            .putStringSet("ivf_expanded_ids", expandedPhaseIds)
            .remove("ivf_dose_taken_date")
            .apply()
        emitUpdatedState()
    }

    fun toggleTaskCompleted(taskId: String) {
        completedTaskIds = if (completedTaskIds.contains(taskId)) {
            completedTaskIds - taskId
        } else {
            completedTaskIds + taskId
        }
        prefs.edit().putStringSet("ivf_completed_tasks", completedTaskIds).apply()
        emitUpdatedState()
    }

    fun toggleDailyDoseTaken() {
        doseTakenToday = !doseTakenToday
        prefs.edit().apply {
            if (doseTakenToday) {
                putString("ivf_dose_taken_date", todayKey)
            } else {
                remove("ivf_dose_taken_date")
            }
        }.apply()
        emitUpdatedState()
    }

    fun updateCycleDay(day: Int) {
        cycleDay = day.coerceIn(1, 35)
        prefs.edit().putInt("ivf_cycle_day", cycleDay).apply()
        emitUpdatedState()
    }

    fun expandAllPhases() {
        expandedPhaseIds = phaseTemplates.map { it.id }.toSet()
        prefs.edit().putStringSet("ivf_expanded_ids", expandedPhaseIds).apply()
        emitUpdatedState()
    }

    fun collapseAllPhases() {
        expandedPhaseIds = emptySet()
        prefs.edit().putStringSet("ivf_expanded_ids", expandedPhaseIds).apply()
        emitUpdatedState()
    }
}
