package com.example.data

import com.example.data.model.BabyMilestone
import com.example.data.model.KnowledgeArticle

object MilestoneRoadmapData {
    val milestones24Months = listOf(
        // 0 - 2 Months
        BabyMilestone(
            id = "m_0_1",
            monthRange = "0–2 Months",
            targetMonth = 1,
            domain = "Physical",
            title = "Lifts head briefly during tummy time",
            description = "Can raise head for a few seconds while lying on stomach."
        ),
        BabyMilestone(
            id = "m_0_2",
            monthRange = "0–2 Months",
            targetMonth = 2,
            domain = "Social",
            title = "First social smile",
            description = "Smiles in response to mother's voice and gentle facial expressions."
        ),
        BabyMilestone(
            id = "m_0_3",
            monthRange = "0–2 Months",
            targetMonth = 2,
            domain = "Cognitive",
            title = "Tracks faces with eyes",
            description = "Follows caregiver's movement across midline (~180 degrees)."
        ),

        // 3 - 4 Months
        BabyMilestone(
            id = "m_4_1",
            monthRange = "3–4 Months",
            targetMonth = 4,
            domain = "Physical",
            title = "Pushes up on elbows & reaches for toys",
            description = "Maintains head control steadily and bats at hanging objects."
        ),
        BabyMilestone(
            id = "m_4_2",
            monthRange = "3–4 Months",
            targetMonth = 4,
            domain = "Social",
            title = "Chuckles and belly laughs",
            description = "Spontaneous giggles and squeals of delight during play."
        ),

        // 6 Months
        BabyMilestone(
            id = "m_6_1",
            monthRange = "5–6 Months",
            targetMonth = 6,
            domain = "Physical",
            title = "Rolls both ways (tummy to back)",
            description = "Rolls back to tummy and tummy to back; sits with support."
        ),
        BabyMilestone(
            id = "m_6_2",
            monthRange = "5–6 Months",
            targetMonth = 6,
            domain = "Cognitive",
            title = "Passes toy between hands",
            description = "Transfers objects from one hand to the other and explores with mouth."
        ),

        // 9 Months
        BabyMilestone(
            id = "m_9_1",
            monthRange = "7–9 Months",
            targetMonth = 9,
            domain = "Physical",
            title = "Sits unsupported & crawls",
            description = "Sturdy sitting balance; begins commando or cross-crawling."
        ),
        BabyMilestone(
            id = "m_9_2",
            monthRange = "7–9 Months",
            targetMonth = 9,
            domain = "Social",
            title = "Responds to own name",
            description = "Turns head consistently when name is spoken; stranger awareness."
        ),

        // 12 Months
        BabyMilestone(
            id = "m_12_1",
            monthRange = "10–12 Months",
            targetMonth = 12,
            domain = "Physical",
            title = "Pulls to stand & cruises furniture",
            description = "Pulls self up and takes first independent steps."
        ),
        BabyMilestone(
            id = "m_12_2",
            monthRange = "10–12 Months",
            targetMonth = 12,
            domain = "Cognitive",
            title = "Says first meaningful words ('Mama', 'Dada')",
            description = "Uses simple gestures like waving bye-bye and pointing."
        ),

        // 18 Months
        BabyMilestone(
            id = "m_18_1",
            monthRange = "13–18 Months",
            targetMonth = 18,
            domain = "Physical",
            title = "Walks confidently & climbs stairs with aid",
            description = "Can carry toys while walking; scribbles with crayons."
        ),
        BabyMilestone(
            id = "m_18_2",
            monthRange = "13–18 Months",
            targetMonth = 18,
            domain = "Cognitive",
            title = "Understands 1-step commands",
            description = "Points to familiar objects in a book when asked."
        ),

        // 24 Months
        BabyMilestone(
            id = "m_24_1",
            monthRange = "19–24 Months",
            targetMonth = 24,
            domain = "Physical",
            title = "Runs, kicks a ball, and jumps with both feet",
            description = "Builds towers of 4+ blocks; uses a spoon independently."
        ),
        BabyMilestone(
            id = "m_24_2",
            monthRange = "19–24 Months",
            targetMonth = 24,
            domain = "Cognitive",
            title = "2-word sentences ('more milk', 'big dog')",
            description = "Vocabulary expands past 50+ words; imitates everyday routines."
        )
    )

    val knowledgeArticles = listOf(
        KnowledgeArticle(
            id = "art_1",
            category = "Vaccines",
            title = "CDC & AAP Childhood Immunization Schedule",
            readTime = "4 min read",
            content = """
                Protecting your newborn with timely vaccinations creates lifelong immune protection:
                
                • Birth: Hepatitis B (HepB) dose 1.
                • 2 Months: DTaP, Polio (IPV), Hib, Pneumococcal (PCV15), Rotavirus (RV), HepB dose 2.
                • 4 Months: DTaP, IPV, Hib, PCV15, RV.
                • 6 Months: DTaP, IPV, Hib, PCV15, RV, HepB dose 3, Annual Influenza.
                • 12–15 Months: MMR (Measles, Mumps, Rubella), Varicella (Chickenpox), HepA dose 1.
                
                Always review with your pediatrician at each well-child visit.
            """.trimIndent()
        ),
        KnowledgeArticle(
            id = "art_2",
            category = "1st Trimester",
            title = "First Trimester Nutrition & Nausea Management",
            readTime = "3 min read",
            content = """
                Navigating morning sickness with clinical tips:
                
                • Keep crackers or dry toast on your bedside table to eat before standing up.
                • Vitamin B6 (25mg 3x daily) paired with doxylamine provides safe, effective nausea relief.
                • Stay hydrated with electrolytes, sparkling water with lemon, or watermelon cubes.
                • Do not worry if your diet is simple right now; baby draws on your pre-existing nutrient stores.
            """.trimIndent()
        ),
        KnowledgeArticle(
            id = "art_3",
            category = "Postpartum",
            title = "Postpartum Recovery & The 4th Trimester",
            readTime = "5 min read",
            content = """
                Your body needs intentional healing after birth:
                
                • Lochia (postpartum bleeding) lasts 3-6 weeks, transitioning from bright red to pink and yellowish-white.
                • Perineal care: Use a peri-bottle with warm water, witch hazel pads, and cold packs.
                • Rest whenever the baby sleeps; minimize lifting anything heavier than your baby.
                • Baby blues peak around days 3-5 due to plummeting hormones; if sadness persists past 2 weeks, speak with your doctor about postpartum depression.
            """.trimIndent()
        ),
        KnowledgeArticle(
            id = "art_4",
            category = "3rd Trimester",
            title = "Signs of Labor & The 5-1-1 Rule",
            readTime = "3 min read",
            content = """
                How to recognize true labor versus Braxton Hicks:
                
                • True contractions: Feel like deep menstrual cramps wrapping from back to front, becoming progressively longer, stronger, and closer together.
                • 5-1-1 Rule: Contractions occurring every 5 minutes, lasting 1 full minute, continuing for 1 hour.
                • Water breaking: Clear fluid release (trickle or gush); note the time, color, and odor.
                • Call labor triage immediately if you notice bright red bleeding, decreased fetal movement, or continuous severe abdominal pain.
            """.trimIndent()
        )
    )
}
