package com.example.ai

import com.example.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAssistantClient {

    private val systemInstruction = """
        Tu es « Assistant Maman », une accompagnante virtuelle bienveillante et rigoureuse spécialisée dans la grossesse et le post-partum.
        Réponds TOUJOURS en français clair, chaleureux et rassurant (sauf demande explicite d'une autre langue).
        Appuie-toi sur des recommandations obstétricales fondées sur les preuves (HAS / CNGOF / OMS / ACOG).
        Fournis des conseils pratiques et concrets tout en rappelant de consulter un(e) gynécologue, une sage-femme ou les urgences maternité en cas de symptôme inhabituel ou urgent.
        Structure tes réponses avec des paragraphes aérés et des listes à puces lisibles.
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
        userLanguage: String = "fr"
    ): Result<String> = withContext(Dispatchers.IO) {
        val model = generativeModel
        if (model != null) {
            try {
                val contextualPrompt = buildString {
                    if (currentWeek != null) {
                        append("[Contexte : La maman est actuellement à la semaine $currentWeek de grossesse. Langue souhaitée : $userLanguage (répondre en français).]\n\n")
                    }
                    append(prompt)
                }
                val response = model.generateContent(contextualPrompt)
                val reply = response.text?.trim()
                if (!reply.isNullOrBlank()) {
                    return@withContext Result.success(reply)
                }
            } catch (e: Exception) {
                return@withContext Result.success(getCuratedObstetricGuidance(prompt, currentWeek))
            }
        }

        Result.success(getCuratedObstetricGuidance(prompt, currentWeek))
    }

    private fun getCuratedObstetricGuidance(prompt: String, currentWeek: Int?): String {
        val query = prompt.lowercase()
        val weekContext = if (currentWeek != null) "À la semaine $currentWeek de votre grossesse, " else ""

        return when {
            query.contains("brûlure") || query.contains("brulure") || query.contains("reflux") ||
                    query.contains("estomac") || query.contains("heartburn") || query.contains("acid") -> {
                """
                ${weekContext}les brûlures d'estomac et remontées acides sont très fréquentes : la progestérone relâche le sphincter de l'œsophage et bébé appuie progressivement sur l'estomac.

                💡 Conseils pratiques pour vous soulager :
                • Fractionnez vos repas : mangez en plus petites quantités, plus souvent.
                • Restez en position assise ou semi-assise au moins 45 à 60 minutes après le repas.
                • Évitez les aliments déclencheurs (plats très épicés, fritures, agrumes, caféine, boissons gazeuses).
                • Testez une tisane au gingembre ou à la camomille, ou un verre de lait frais.
                • Surélevez légèrement votre buste la nuit avec un coussin supplémentaire.

                ⚠️ Demandez toujours l'avis de votre sage-femme ou médecin avant de prendre un antiacide.
                """.trimIndent()
            }
            query.contains("coup") || query.contains("mouvement") || query.contains("bouge") ||
                    query.contains("kick") || query.contains("movement") -> {
                """
                ${weekContext}surveiller les mouvements de bébé est un excellent moyen de se rassurer au quotidien !

                🩺 Repères pratiques pour compter les mouvements :
                • Meilleur moment : après un repas ou au calme le soir, allongée sur le côté gauche.
                • Objectif : percevoir environ 10 mouvements distincts (coups, vagues, rotations) en moins de 2 heures.
                • La plupart des bébés atteignent ces 10 mouvements en moins de 30 minutes lorsqu'ils sont éveillés.

                ⚠️ En cas de diminution nette ou d'absence de mouvements après vous être allongée au calme en buvant un verre d'eau fraîche, contactez rapidement votre maternité.
                """.trimIndent()
            }
            query.contains("exercice") || query.contains("étirement") || query.contains("etirement") ||
                    query.contains("sport") || query.contains("exercise") || query.contains("stretch") -> {
                """
                ${weekContext}rester active en douceur est bénéfique pour votre dos, votre moral et la préparation à l'accouchement !

                🧘 Activités sûres pendant la grossesse :
                • Marche quotidienne, yoga prénatal, vélo d'appartement et natation (qui soulage le poids sur les articulations).
                • Étirements doux du dos (posture du chat / de la vache) et bascules du bassin.
                • Exercices de renforcement doux du périnée.

                ⛔ Évitez les sports à risque de chute ou de choc, ainsi que la position allongée à plat dos prolongée après 20 semaines. Pensez à bien vous hydrater.
                """.trimIndent()
            }
            query.contains("valise") || query.contains("maternité") || query.contains("maternite") ||
                    query.contains("sac") || query.contains("bag") || query.contains("hospital") -> {
                """
                Préparer votre valise de maternité vers les semaines 34–36 apporte une grande sérénité !

                🎒 Essentiels pour Maman :
                • Pièce d'identité, carte Vitale, mutuelle, dossier médical complet et projet de naissance.
                • 2–3 chemises de nuit ou pyjamas amples ouvrables devant pour le peau-à-peau et l'allaitement.
                • Chaussettes chaudes, chaussons antidérapants et baume à lèvres.
                • Câble chargeur de téléphone extra-long.
                • Culottes confortables post-partum et serviettes maternité.

                👶 Essentiels pour Bébé :
                • Siège auto homologué (groupe 0+) prêt pour le retour à la maison.
                • Tenues de naissance (bodies croisés, pyjamas velours, bonnet, brassière, chaussons).
                • Gigoteuse / turbulette adaptée et langes en coton doux.
                """.trimIndent()
            }
            query.contains("colique") || query.contains("pleur") || query.contains("calmer") ||
                    query.contains("tétée") || query.contains("allaitement") || query.contains("colic") || query.contains("cry") -> {
                """
                Apaiser un nouveau-né demande parfois beaucoup de patience, mais des gestes doux font des merveilles !

                👶 Gestes apaisants recommandés :
                • Le portage en écharpe ou en peau-à-peau contre votre poitrine rassure instantanément bébé.
                • Un léger bercement rythmé ou une promenade en douceur.
                • Un bruit blanc doux ou un « chuuut » régulier à voix basse.
                • Vérifiez les besoins simples : couche propre, faim, rot, ou besoin de succion.

                N'hésitez pas à passer le relais au co-parent si vous ressentez de la fatigue : vous faites un travail formidable !
                """.trimIndent()
            }
            query.contains("alimentation") || query.contains("manger") || query.contains("nutrition") ||
                    query.contains("repas") || query.contains("food") || query.contains("eat") -> {
                """
                ${weekContext}une alimentation variée et équilibrée soutient la croissance rapide de votre bébé.

                🥗 À privilégier au quotidien :
                • Protéines : œufs bien cuits, lentilles, volaille bien cuite, yaourts nature ou fromage pasteurisé.
                • Fer & Folates : légumes verts à feuilles, légumineuses et céréales complètes.
                • Oméga-3 (DHA) : poissons gras cuits pauvres en mercure (saumon, sardine) et huile de colza ou noix.
                • Hydratation : 2 à 2,5 litres d'eau par jour.

                🚫 Par précaution : évitez viandes et poissons crus, fromages au lait cru, charcuterie non cuite et limitez la caféine.
                """.trimIndent()
            }
            else -> {
                """
                ${weekContext}je suis votre Assistante Maman, là pour vous accompagner à chaque étape.

                Chaque grossesse est unique. Votre corps accomplit un travail remarquable chaque jour tandis que votre bébé grandit.

                Prenez le temps de vous reposer, hydratez-vous régulièrement et écoutez vos sensations. Vous pouvez me poser vos questions sur les symptômes, les mouvements de bébé, la valise de maternité ou la nutrition !

                👩‍⚕️ En cas d'œdèmes soudains, de maux de tête intenses et persistants, de troubles visuels ou de saignements, contactez immédiatement votre professionnel de santé.
                """.trimIndent()
            }
        }
    }
}
