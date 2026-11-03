package com.example.data.model

enum class SizeTheme(val displayName: String) {
    FRUITS("Fruits & Veggies"),
    ANIMALS("Cute Animals"),
    SNACKS("Snacks & Treats"),
    SPORTS("Sports Equipment")
}

data class SizeComparison(
    val title: String,
    val emoji: String,
    val description: String = ""
)

data class PregnancyWeek(
    val weekNumber: Int,
    val fruitAnalogy: String,
    val fruitEmoji: String,
    val animalAnalogy: String,
    val animalEmoji: String,
    val snackAnalogy: String,
    val snackEmoji: String,
    val sportAnalogy: String,
    val sportEmoji: String,
    val lengthCm: Double,
    val weightGrams: Double,
    val babyDevelopment: String,
    val momChanges: String,
    val clinicalAdvice: String,
    val twinAdvice: String,
    val trimester: Int,
    val commonSymptoms: List<String>
) {
    fun getComparison(theme: SizeTheme): SizeComparison {
        return when (theme) {
            SizeTheme.FRUITS -> SizeComparison(fruitAnalogy, fruitEmoji)
            SizeTheme.ANIMALS -> SizeComparison(animalAnalogy, animalEmoji)
            SizeTheme.SNACKS -> SizeComparison(snackAnalogy, snackEmoji)
            SizeTheme.SPORTS -> SizeComparison(sportAnalogy, sportEmoji)
        }
    }
}

data class ChecklistItem(
    val id: Long = 0,
    val category: String,
    val title: String,
    val isChecked: Boolean = false,
    val isCustom: Boolean = false
)

data class PrenatalAgendaItem(
    val id: Long = 0,
    val title: String,
    val weekRange: String,
    val targetWeek: Int,
    val description: String,
    val isCrucial: Boolean,
    val isCompleted: Boolean = false
)

data class BabyMilestone(
    val id: String,
    val monthRange: String,
    val targetMonth: Int,
    val domain: String, // Physical, Cognitive, Social
    val title: String,
    val description: String,
    var isAchieved: Boolean = false
)

data class KnowledgeArticle(
    val id: String,
    val category: String, // 1st Trimester, 2nd Trimester, 3rd Trimester, Postpartum, Vaccines
    val title: String,
    val readTime: String,
    val content: String
)
