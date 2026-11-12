package com.example.data

import com.example.data.model.BabyMilestone
import com.example.data.model.KnowledgeArticle

object MilestoneRoadmapData {
    val milestones24Months = listOf(
        // 0 - 2 Mois
        BabyMilestone(
            id = "m_0_1",
            monthRange = "0–2 Mois",
            targetMonth = 1,
            domain = "Physique",
            title = "Soulève brièvement la tête sur le ventre",
            description = "Parvient à relever la tête quelques secondes pendant les moments d'éveil sur le ventre."
        ),
        BabyMilestone(
            id = "m_0_2",
            monthRange = "0–2 Mois",
            targetMonth = 2,
            domain = "Social",
            title = "Premier sourire-réponse",
            description = "Sourit en réponse à la voix de maman et de papa et aux expressions douces du visage."
        ),
        BabyMilestone(
            id = "m_0_3",
            monthRange = "0–2 Mois",
            targetMonth = 2,
            domain = "Cognitif",
            title = "Suit les visages du regard",
            description = "Suit les mouvements des parents sur près de 180 degrés."
        ),

        // 3 - 4 Mois
        BabyMilestone(
            id = "m_4_1",
            monthRange = "3–4 Mois",
            targetMonth = 4,
            domain = "Physique",
            title = "Prend appui sur les avant-bras & attrape les jouets",
            description = "Maintient bien sa tête droite et cherche à toucher les objets suspendus."
        ),
        BabyMilestone(
            id = "m_4_2",
            monthRange = "3–4 Mois",
            targetMonth = 4,
            domain = "Social",
            title = "Gazouille et rit aux éclats",
            description = "Éclats de rire spontanés et cris de joie pendant les moments de jeu."
        ),

        // 6 Mois
        BabyMilestone(
            id = "m_6_1",
            monthRange = "5–6 Mois",
            targetMonth = 6,
            domain = "Physique",
            title = "Se retourne des deux côtés (dos-ventre)",
            description = "Passe du dos au ventre et inversement ; tient assis avec un léger appui."
        ),
        BabyMilestone(
            id = "m_6_2",
            monthRange = "5–6 Mois",
            targetMonth = 6,
            domain = "Cognitif",
            title = "Passe un jouet d'une main à l'autre",
            description = "Transfère les objets d'une main à l'autre et explore les textures avec la bouche."
        ),

        // 9 Mois
        BabyMilestone(
            id = "m_9_1",
            monthRange = "7–9 Mois",
            targetMonth = 9,
            domain = "Physique",
            title = "Tient assis sans appui & se déplace à quatre pattes",
            description = "Équilibre assis stable ; commence à ramper ou marcher à quatre pattes."
        ),
        BabyMilestone(
            id = "m_9_2",
            monthRange = "7–9 Mois",
            targetMonth = 9,
            domain = "Social",
            title = "Répond à son prénom",
            description = "Tourne systématiquement la tête à l'appel de son prénom ; distingue visages familiers et inconnus."
        ),

        // 12 Mois
        BabyMilestone(
            id = "m_12_1",
            monthRange = "10–12 Mois",
            targetMonth = 12,
            domain = "Physique",
            title = "Se met debout & longe les meubles",
            description = "Se hisse debout avec appui et tente ses premiers pas."
        ),
        BabyMilestone(
            id = "m_12_2",
            monthRange = "10–12 Mois",
            targetMonth = 12,
            domain = "Cognitif",
            title = "Dit ses premiers mots (« Maman », « Papa »)",
            description = "Utilise des gestes simples comme faire au revoir de la main et pointer du doigt."
        ),

        // 18 Mois
        BabyMilestone(
            id = "m_18_1",
            monthRange = "13–18 Mois",
            targetMonth = 18,
            domain = "Physique",
            title = "Marche avec assurance & monte les marches avec aide",
            description = "Peut transporter ses jouets en marchant et gribouiller au crayon."
        ),
        BabyMilestone(
            id = "m_18_2",
            monthRange = "13–18 Mois",
            targetMonth = 18,
            domain = "Cognitif",
            title = "Comprend les consignes simples",
            description = "Montre du doigt les objets familiers dans un livre lorsqu'on le lui demande."
        ),

        // 24 Mois
        BabyMilestone(
            id = "m_24_1",
            monthRange = "19–24 Mois",
            targetMonth = 24,
            domain = "Physique",
            title = "Court, tape dans un ballon et saute à pieds joints",
            description = "Empile des tours de 4 cubes ou plus et mange seul à la cuillère."
        ),
        BabyMilestone(
            id = "m_24_2",
            monthRange = "19–24 Mois",
            targetMonth = 24,
            domain = "Cognitif",
            title = "Fait des phrases de 2 mots (« encore lait », « gros chien »)",
            description = "Vocabulaire de plus de 50 mots ; imite les gestes du quotidien."
        )
    )

    val knowledgeArticles = listOf(
        KnowledgeArticle(
            id = "art_1",
            category = "Vaccins",
            title = "Calendrier Vaccinal de Bébé (0 à 24 mois)",
            readTime = "4 min de lecture",
            content = """
                Protéger votre nouveau-né grâce aux vaccinations recommandées renforce son immunité dès les premiers mois :
                
                • Naissance : BCQ (selon recommandations) et Hépatite B (si indiqué).
                • 2 Mois : Hexavalent (DTP, Coqueluche, Hib, Hépatite B) + Pneumocoque + Rotavirus.
                • 4 Mois : Rappel Hexavalent + Pneumocoque + Rotavirus.
                • 5 Mois : Méningocoque B / C ou ACWY selon schéma en vigueur.
                • 11–12 Mois : ROR (Rougeole, Oreillons, Rubéole) + rappels essentiels.
                
                Faites toujours le point avec votre pédiatre ou médecin traitant lors des visites mensuelles.
            """.trimIndent()
        ),
        KnowledgeArticle(
            id = "art_2",
            category = "1er Trimestre",
            title = "Alimentation du 1er Trimestre & Gestion des Nausées",
            readTime = "3 min de lecture",
            content = """
                Apprivoiser les nausées matinales avec des astuces simples et douces :
                
                • Gardez quelques biscuits secs ou biscottes sur la table de chevet à croquer avant de vous lever.
                • Fractionnez vos repas : 5 à 6 petites collations valent mieux que 3 gros repas.
                • Hydratez-vous régulièrement par petites gorgées (eau citronnée, infusion au gingembre, pastèque).
                • Ne culpabilisez pas si votre appétit varie : bébé puise d'abord dans vos réserves existantes.
            """.trimIndent()
        ),
        KnowledgeArticle(
            id = "art_3",
            category = "Post-partum",
            title = "Récupération Post-partum & Le 4e Trimestre",
            readTime = "5 min de lecture",
            content = """
                Votre corps a besoin de douceur et de repos après la naissance :
                
                • Les lochies (saignements post-accouchement) durent généralement 3 à 6 semaines et s'éclaircissent progressivement.
                • Soin du périnée : privilégiez l'eau tiède après chaque passage aux toilettes et séchez délicatement.
                • Reposez-vous dès que bébé dort ; évitez de porter des charges plus lourdes que votre bébé.
                • Le baby-blues survient souvent entre le 3e et le 5e jour ; si la tristesse persiste au-delà de 2 semaines, parlez-en à votre sage-femme ou médecin.
            """.trimIndent()
        ),
        KnowledgeArticle(
            id = "art_4",
            category = "3e Trimestre",
            title = "Signes du Travail & La Règle du 5-1-1",
            readTime = "3 min de lecture",
            content = """
                Reconnaître les vraies contractions de travail par rapport aux contractions de Braxton Hicks :
                
                • Vraies contractions : douleurs rythmées partant souvent du bas du dos vers le bas-ventre, devenant plus longues, intenses et rapprochées.
                • Règle du 5-1-1 : contractions toutes les 5 minutes, durant environ 1 minute chacune, pendant au moins 1 heure.
                • Perte des eaux : écoulement clair (filet continu ou flot) ; notez l'heure et l'aspect du liquide.
                • Contactez immédiatement la maternité en cas de saignements rouge vif, diminution nette des mouvements de bébé ou douleur abdominale intense continue.
            """.trimIndent()
        )
    )
}
