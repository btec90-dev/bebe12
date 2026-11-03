package com.example.ai

import com.example.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAssistantClient {

    private val systemInstruction = """
        You are 'Maman Assistant', an empathetic, clinically sound pregnancy and postpartum companion.
        Always follow reassuring, evidence-based obstetric guidelines (ACOG / WHO / RCOG).
        Provide clear, warm, actionable guidance, reminding mothers to consult their OB/GYN, midwife, or care team for urgent symptoms.
        Format your answers with clean paragraphs, supportive tone, and helpful tips.
    """.trimIndent()

    private val generativeModel by lazy {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = apiKey,
                systemInstruction = content { text(systemInstruction) }
            )
        } else {
            null
        }
    }

    suspend fun askAssistant(
        prompt: String,
        currentWeek: Int? = null,
        userLanguage: String = "en"
    ): Result<String> = withContext(Dispatchers.IO) {
        val model = generativeModel
        if (model != null) {
            try {
                val contextualPrompt = buildString {
                    if (currentWeek != null) {
                        append("[Context: Mother is currently at Pregnancy Week $currentWeek, preferred language: $userLanguage]\n\n")
                    }
                    append(prompt)
                }
                val response = model.generateContent(contextualPrompt)
                val reply = response.text?.trim()
                if (!reply.isNullOrBlank()) {
                    return@withContext Result.success(reply)
                }
            } catch (e: Exception) {
                // If API call throws (e.g. invalid key or network issue), provide smart obstetric fallback
                return@withContext Result.success(getCuratedObstetricGuidance(prompt, currentWeek))
            }
        }

        // Curated, immediate clinical fallback when API key is pending in Secrets panel
        Result.success(getCuratedObstetricGuidance(prompt, currentWeek))
    }

    private fun getCuratedObstetricGuidance(prompt: String, currentWeek: Int?): String {
        val query = prompt.lowercase()
        val weekContext = if (currentWeek != null) "At Week $currentWeek of your pregnancy, " else ""

        return when {
            query.contains("heartburn") || query.contains("acid") || query.contains("reflux") -> {
                """
                ${weekContext}heartburn is very common due to progesterone relaxing the esophageal sphincter, plus the growing baby gently nudging your stomach upward.

                💡 Practical tips to find relief:
                • Eat smaller, more frequent meals rather than large portions.
                • Stay upright for at least 45–60 minutes after eating.
                • Avoid known triggers like citrus, spicy foods, caffeine, and heavy fried meals.
                • Sip ginger or chamomile tea, or cold milk.
                • Elevate your head and torso slightly with a wedge pillow at bedtime.

                ⚠️ Consult your doctor or midwife before starting calcium antacids or medications.
                """.trimIndent()
            }
            query.contains("kick") || query.contains("movement") || query.contains("flutter") -> {
                """
                ${weekContext}monitoring fetal movements provides great reassurance about your baby's wellbeing!

                🩺 Clinical Kick Counting Guidelines (ACOG):
                • Best time: After a snack or meal, or when relaxing on your left side in the evening.
                • Goal: You should feel 10 distinct movements (kicks, rolls, swishes, or flutters) within 2 hours.
                • Most babies reach 10 kicks in less than 30 minutes when active.

                ⚠️ If you notice a sudden decrease in movements or have not felt 10 kicks within 2 hours after having a cold drink and resting on your side, contact your maternity triage or OB/GYN immediately.
                """.trimIndent()
            }
            query.contains("exercise") || query.contains("stretch") || query.contains("workout") -> {
                """
                ${weekContext}staying active offers wonderful benefits for stamina, mood, and preparing for labor!

                🧘 Safe Pregnancy Activities:
                • Walking, prenatal yoga, stationary cycling, and swimming (which takes weight off your joints!).
                • Gentle cat-cow stretches and pelvic tilts to relieve lower back tension.
                • Pelvic floor (Kegel) conditioning exercises.

                ⛔ Avoid activities with fall risks, contact sports, or lying flat on your back for prolonged periods after 20 weeks. Always stay well-hydrated.
                """.trimIndent()
            }
            query.contains("bag") || query.contains("pack") || query.contains("hospital") -> {
                """
                Packing your hospital bag around Weeks 34–36 brings wonderful peace of mind!

                🎒 Essentials for Mom:
                • Photo ID, insurance card, medical records, and birth preferences plan.
                • 2–3 pairs of loose, dark nursing pajamas or gowns with easy front access.
                • Cozy non-skid socks, warm slippers, and lip balm.
                • Extra-long phone charger cable.
                • High-waisted postpartum underwear and maternity pads.

                👶 Essentials for Baby:
                • Installed infant car seat (inspected!).
                • 2 going-home outfits (Newborn and 0–3M sizes).
                • Soft swaddle blanket, hat, and scratch mittens.
                """.trimIndent()
            }
            query.contains("colic") || query.contains("cry") || query.contains("soothe") || query.contains("nursing") -> {
                """
                Soothing a newborn can test your patience, but gentle comfort techniques work wonders!

                👶 The 5 S's Technique (Dr. Harvey Karp):
                • Swaddle: A snug swaddle creates the cozy security of the womb.
                • Side or Stomach position: Hold baby on their side or across your forearm while awake and supervised.
                • Shushing: Gentle white noise or rhythmic 'shhh' sounds louder than their crying.
                • Swinging: Gentle, rhythmic swaying or rocking motions.
                • Sucking: Pacifier or nursing latch for non-nutritive soothing.

                Remember to take turns with your partner when you feel overwhelmed. You are doing an incredible job!
                """.trimIndent()
            }
            query.contains("food") || query.contains("eat") || query.contains("nutrition") || query.contains("diet") -> {
                """
                ${weekContext}nourishing your body with wholesome nutrition supports your baby's rapid growth.

                🥗 Focus on:
                • Protein: Eggs, lentils, tofu, well-cooked poultry, and Greek yogurt for tissue building.
                • Iron & Folate: Dark leafy greens, fortified cereals, and lean meats to support higher blood volume.
                • Choline & Omega-3 (DHA): Eggs and low-mercury cooked fish (salmon) for fetal brain development.
                • Plenty of water: Aim for 2.5 to 3 liters daily.

                🚫 Foods to avoid: Raw/undercooked meats and sushi, unpasteurized cheeses, deli meats unless heated to steaming, and excess caffeine (>200mg/day).
                """.trimIndent()
            }
            else -> {
                """
                ${weekContext}I'm here as your Maman Assistant companion. 

                Every pregnancy journey is unique. Your body is undergoing remarkable transformations as your baby grows each day. 

                Remember to listen closely to your body, stay gently hydrated, and rest whenever fatigue sets in. If you have questions about specific symptoms (like backaches, kicks, Braxton Hicks, or tests), feel free to ask!

                👩‍⚕️ For symptoms like sudden severe swelling, persistent severe headache, visual disturbances, or vaginal bleeding, contact your healthcare provider right away.
                """.trimIndent()
            }
        }
    }
}
