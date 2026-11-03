package com.example.data

import com.example.data.model.ChecklistItem
import com.example.data.model.PrenatalAgendaItem

object ChecklistData {
    val defaultItems = listOf(
        // Valise Maternité - Maman (Fatou)
        ChecklistItem(id = 1, category = "Valise Maternité (Maman)", title = "Dossier médical complet, carte Vitale, carte mutuelle, livret de famille"),
        ChecklistItem(id = 2, category = "Valise Maternité (Maman)", title = "Chemises de nuit ouvertes pour allaitement ou pyjamas amples"),
        ChecklistItem(id = 3, category = "Valise Maternité (Maman)", title = "Chaussons confortables antidérapants et chaussettes chaudes"),
        ChecklistItem(id = 4, category = "Valise Maternité (Maman)", title = "Culottes jetables en filet ou coton et serviettes hygiéniques post-partum"),
        ChecklistItem(id = 5, category = "Valise Maternité (Maman)", title = "Soutiens-gorge d'allaitement et coussinets absorbants"),
        ChecklistItem(id = 6, category = "Valise Maternité (Maman)", title = "Crème apaisante à la lanoline pour mamelons et brumisateur d'eau"),
        ChecklistItem(id = 7, category = "Valise Maternité (Maman)", title = "Trousse de toilette personnelle, élastiques, baume à lèvres"),
        ChecklistItem(id = 8, category = "Valise Maternité (Maman)", title = "Coussin d'allaitement et chargeur de téléphone extra-long"),

        // Valise Maternité - Bébé (Jour J & Séjour)
        ChecklistItem(id = 9, category = "Valise Maternité (Bébé - Jour J)", title = "Tenue de naissance salle d'accouchement (body croisé, pyjama chaud velours, bonnet, chaussons)"),
        ChecklistItem(id = 10, category = "Valise Maternité (Bébé - Jour J)", title = "6 bodies en coton (ouverture croisée / cache-cœur, taille 1 mois)"),
        ChecklistItem(id = 11, category = "Valise Maternité (Bébé - Jour J)", title = "6 pyjamas doux et confortables en velours"),
        ChecklistItem(id = 12, category = "Valise Maternité (Bébé - Jour J)", title = "2 brassières ou gilets en laine / tricot chaud"),
        ChecklistItem(id = 13, category = "Valise Maternité (Bébé - Jour J)", title = "2 bonnets en coton et 3 paires de chaussettes"),
        ChecklistItem(id = 14, category = "Valise Maternité (Bébé - Jour J)", title = "4 langes doux en mousseline de coton et 3 bavoirs naissance"),
        ChecklistItem(id = 15, category = "Valise Maternité (Bébé - Jour J)", title = "2 capes de bain éponge et gigoteuse / turbulette (0-6M)"),
        ChecklistItem(id = 16, category = "Valise Maternité (Bébé - Jour J)", title = "Siège auto homologué groupe 0+ installé pour la sortie"),

        // Valise Accompagnant - Papa (Bala)
        ChecklistItem(id = 17, category = "Valise Accompagnant (Papa)", title = "Pièce d'identité de Bala & reconnaissance anticipée"),
        ChecklistItem(id = 18, category = "Valise Accompagnant (Papa)", title = "T-shirt propre pour le peau-à-peau en salle de naissance"),
        ChecklistItem(id = 19, category = "Valise Accompagnant (Papa)", title = "En-cas énergétiques, gourde d'eau et monnaie pour machine à café"),
        ChecklistItem(id = 20, category = "Valise Accompagnant (Papa)", title = "Appareil photo / smartphone chargé pour immortaliser les premiers instants"),

        // Choses à Acheter Avant la Naissance
        ChecklistItem(id = 21, category = "À Acheter Avant la Naissance", title = "Lit bébé ou berceau cododo aux normes de sécurité"),
        ChecklistItem(id = 22, category = "À Acheter Avant la Naissance", title = "Matelas ferme adapté au lit et alèse imperméable respirante"),
        ChecklistItem(id = 23, category = "À Acheter Avant la Naissance", title = "Poussette adaptée dès la naissance avec nacelle / cosy"),
        ChecklistItem(id = 24, category = "À Acheter Avant la Naissance", title = "Table ou matelas à langer avec rebords sécurisés"),
        ChecklistItem(id = 25, category = "À Acheter Avant la Naissance", title = "Thermomètre médical rectal souple et thermomètre de bain"),
        ChecklistItem(id = 26, category = "À Acheter Avant la Naissance", title = "Baignoire pour bébé ergonomique et gel lavant corps & cheveux surgras"),
        ChecklistItem(id = 27, category = "À Acheter Avant la Naissance", title = "Paquet de couches taille 1 (2-5 kg) et liniment oléo-calcaire / coton bio"),
        ChecklistItem(id = 28, category = "À Acheter Avant la Naissance", title = "Sérum physiologique en unidoses et compresses stériles"),

        // Choses à Acheter Après la Naissance
        ChecklistItem(id = 29, category = "À Acheter Après la Naissance", title = "Tire-lait électrique ou manuel (selon choix et confort d'allaitement)"),
        ChecklistItem(id = 30, category = "À Acheter Après la Naissance", title = "Biberons anti-coliques supplémentaires et goupillon de nettoyage"),
        ChecklistItem(id = 31, category = "À Acheter Après la Naissance", title = "Vêtements taille 3 mois selon le rythme de croissance réel de bébé"),
        ChecklistItem(id = 32, category = "À Acheter Après la Naissance", title = "Écharpe de portage ou porte-bébé physiologique testé avec bébé"),
        ChecklistItem(id = 33, category = "À Acheter Après la Naissance", title = "Tapis d'éveil d'activité pour les temps sur le ventre (tummy time)"),
        ChecklistItem(id = 34, category = "À Acheter Après la Naissance", title = "Veilleuse douce et babyphone caméra")
    )
}

object PrenatalAgendaData {
    val defaultAgenda = listOf(
        PrenatalAgendaItem(
            id = 1,
            title = "First Prenatal Visit & Dating Ultrasound",
            weekRange = "Week 8–10",
            targetWeek = 8,
            description = "Confirm gestational age, detect heartbeat, complete baseline maternal blood panels.",
            isCrucial = true
        ),
        PrenatalAgendaItem(
            id = 2,
            title = "First Trimester Screening (NIPT / NT Scan)",
            weekRange = "Week 11–13",
            targetWeek = 12,
            description = "Cell-free DNA blood test for chromosomal health and optional early gender reveal.",
            isCrucial = false
        ),
        PrenatalAgendaItem(
            id = 3,
            title = "Detailed Mid-Pregnancy Anatomy Ultrasound",
            weekRange = "Week 18–22",
            targetWeek = 20,
            description = "Comprehensive head-to-toe structural survey of brain, heart, spine, kidneys, and placenta.",
            isCrucial = true
        ),
        PrenatalAgendaItem(
            id = 4,
            title = "Gestational Diabetes Glucose Screening",
            weekRange = "Week 24–28",
            targetWeek = 26,
            description = "1-hour glucola test to screen for gestational diabetes and check iron levels for anemia.",
            isCrucial = true
        ),
        PrenatalAgendaItem(
            id = 5,
            title = "Tdap Vaccination & Rhogam Shot",
            weekRange = "Week 27–36",
            targetWeek = 28,
            description = "Maternal whooping cough immunization passes antibodies to baby; Rhogam if Rh-negative.",
            isCrucial = true
        ),
        PrenatalAgendaItem(
            id = 6,
            title = "Group B Strep (GBS) Screening Swab",
            weekRange = "Week 35–37",
            targetWeek = 36,
            description = "Quick routine swab to determine if IV antibiotics are needed during active labor.",
            isCrucial = true
        ),
        PrenatalAgendaItem(
            id = 7,
            title = "Weekly Full-Term Prenatal Checkups",
            weekRange = "Week 36–40",
            targetWeek = 38,
            description = "Monitor cervical effacement, blood pressure, fetal position, and kick counts until delivery.",
            isCrucial = true
        )
    )
}
