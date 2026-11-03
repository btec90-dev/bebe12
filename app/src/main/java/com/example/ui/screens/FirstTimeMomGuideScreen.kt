package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground

data class GuideStep(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val bulletPoints: List<String>,
    val expertTip: String
)

@Composable
fun FirstTimeMomGuideScreen(
    onBack: () -> Unit
) {
    val steps = listOf(
        GuideStep(
            stepNumber = 1,
            title = "Les 2 premières heures (Golden Hour)",
            subtitle = "La rencontre en salle de naissance",
            icon = Icons.Default.Favorite,
            bulletPoints = listOf(
                "Peau-à-peau immédiat : Placez bébé nu contre la poitrine nue de Fatou (ou de Bala), recouvert d'une couverture chaude pour réguler sa température et son rythme cardiaque.",
                "Clampage tardif du cordon : Attendre 1 à 3 minutes après la naissance permet un transfert optimal des réserves de fer et de sang maternel.",
                "La première tétée d'accueil : Bébé rampe instinctivement vers le sein (breast crawl) pour recevoir les premières gouttes précieuses de colostrum, concentré en anticorps."
            ),
            expertTip = "Conseil de sage-femme : Limitez les visites pendant ces 2 premières heures pour favoriser l'attachement et le démarrage serein de l'allaitement."
        ),
        GuideStep(
            stepNumber = 2,
            title = "Les premières 24 heures de vie",
            subtitle = "Adaptation & surveillance en maternité",
            icon = Icons.Default.Timer,
            bulletPoints = listOf(
                "Émission du méconium : Les premières selles noires et épaisses de bébé doivent survenir dans les 24 premières heures, signe que le transit digestif fonctionne.",
                "Thermorégulation : Un nouveau-né se refroidit vite. Gardez-lui son bonnet et une brassière en laine pendant les premières 24 heures.",
                "Tétées fréquentes : Proposez le sein ou le biberon dès les premiers signes d'éveil (mouvements de succion, mains à la bouche), environ 8 à 12 fois par 24h."
            ),
            expertTip = "Note pour Bala : Prenez le relais en peau-à-peau pour que Fatou puisse dormir et récupérer de l'accouchement."
        ),
        GuideStep(
            stepNumber = 3,
            title = "Alimentation & Roits après chaque repas",
            subtitle = "Techniques de tétée, biberon et digestion",
            icon = Icons.Default.Restaurant,
            bulletPoints = listOf(
                "Bonne prise du sein : La bouche de bébé doit être grandement ouverte et englober une large part de l'aréole inférieure, les lèvres retroussées comme une ventouse.",
                "Biberon à l'horizontale (pacing) : Tenez le biberon presque à l'horizontale avec une tétine débit lent pour que bébé régule son rythme sans avaler trop d'air.",
                "Faire faire le rot : Maintenez bébé bien droit contre votre épaule ou assis sur vos genoux en soutenant son menton, tapotez doucement son dos de bas en haut pendant 5-10 minutes."
            ),
            expertTip = "Astuce rots : Si bébé s'endort paisiblement sans roter et sans gémir, ne vous inquiétez pas, il n'a peut-être pas avalé d'air !"
        ),
        GuideStep(
            stepNumber = 4,
            title = "Soins du cordon ombilical",
            subtitle = "Hygiène douce & séchage méticuleux",
            icon = Icons.Default.Healing,
            bulletPoints = listOf(
                "Nettoyage simple : Nettoyez la base du cordon 1 à 2 fois par jour à l'aide d'une compresse stérile imbibée de sérum physiologique.",
                "Garder au sec (Primordial) : Séchez délicatement en tamponnant avec une compresse sèche. Repliez le bord supérieur de la couche vers l'extérieur pour ne pas frotter le moignon.",
                "Chute naturelle : Le cordon sèche et tombe spontanément entre le 7ème et le 15ème jour. Ne jamais tirer dessus."
            ),
            expertTip = "Alerte : Consultez si la base du cordon devient très rouge, gonflée ou dégage une odeur anormale."
        ),
        GuideStep(
            stepNumber = 5,
            title = "La toilette & le premier bain",
            subtitle = "Douceur pour la peau fragile du nourrisson",
            icon = Icons.Default.Bathtub,
            bulletPoints = listOf(
                "Reporter le 1er bain : Il est recommandé d'attendre 48h à 72h après la naissance pour laisser le vernix protecteur pénétrer la peau de bébé.",
                "Température idéale : Eau du bain à 37°C vérifiée au thermomètre, et pièce chauffée à 22-24°C.",
                "Toilette du visage et des yeux : Utilisez une dosette de sérum physiologique différente pour chaque œil, du coin externe vers le coin interne."
            ),
            expertTip = "Astuce bain apaisé : Enveloppez bébé dans un lange doux en mousseline et plongez-le avec dans l'eau tiède pour le sécuriser."
        ),
        GuideStep(
            stepNumber = 6,
            title = "Le change & prévention des érythèmes",
            subtitle = "Changer bébé en toute sécurité",
            icon = Icons.Default.Spa,
            bulletPoints = listOf(
                "Fréquence : Changez la couche avant ou après chaque repas, soit 6 à 8 fois par jour, et immédiatement après une selle.",
                "Nettoyage doux : Privilégiez de l'eau tiède ou du liniment oléo-calcaire sur un coton bio. Évitez les lingettes parfumées au début.",
                "Séchage des plis : Séchez soigneusement chaque pli de l'aine et des fesses en tamponnant sans frotter pour éviter les macérations."
            ),
            expertTip = "Sécurité absolue : Gardez toujours une main posée sur bébé lorsqu'il est sur la table à langer, ne vous retournez jamais."
        ),
        GuideStep(
            stepNumber = 7,
            title = "Sommeil sécurisé & prévention de la MSN",
            subtitle = "Les règles d'or pour des nuits sereines",
            icon = Icons.Default.Bed,
            bulletPoints = listOf(
                "Couchage sur le dos : Couchez toujours bébé strictement sur le dos, sur un matelas ferme et plat adapté aux dimensions du lit.",
                "Lit vide de tout objet : Jamais d'oreiller, de couette, de couverture, de tour de lit épais ou de peluches volumineuses dans le lit.",
                "Gigoteuse / Turbulette : Utilisez une gigoteuse adaptée à sa taille (TOG 1 à 2) et maintenez la chambre entre 18°C et 20°C.",
                "Chambre partagée (Cododo) : Faire dormir bébé dans la chambre des parents dans son propre berceau pendant les 6 premiers mois réduit de 50% les risques."
            ),
            expertTip = "Rappel vital : Ne vous endormez jamais avec bébé sur un canapé ou un fauteuil."
        ),
        GuideStep(
            stepNumber = 8,
            title = "Quand consulter le pédiatre en urgence ?",
            subtitle = "Les signes qui doivent vous alerter",
            icon = Icons.Default.LocalHospital,
            bulletPoints = listOf(
                "Fièvre : Toute température rectale égale ou supérieure à 38°C chez un bébé de moins de 3 mois nécessite une consultation médicale immédiate (ou urgences pédiatriques).",
                "Difficultés respiratoires : Respiration très rapide, battement des narines, ou creux sous les côtes à chaque inspiration (tirage).",
                "Comportement anormal : Bébé très mou, apathique, difficile à réveiller, ou au contraire pleurs inconsolables stridents.",
                "Déshydratation : Absence de couche mouillée pendant plus de 8 heures, lèvres sèches, fontanelle creusée."
            ),
            expertTip = "Faites confiance à votre instinct parental : Si vous trouvez que votre bébé n'est 'pas comme d'habitude', n'hésitez jamais à appeler le 15 ou les urgences pédiatriques."
        )
    )

    var expandedStepIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground)
    ) {
        // Top White Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_mom_guide_back")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = DeepEspressoPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Guide Premier Bébé (First-Time Mom)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "Étape par étape : de la naissance aux premières semaines",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DeepEspressoPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(steps) { index, step ->
                val isExpanded = expandedStepIndex == index

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { expandedStepIndex = if (isExpanded) -1 else index }
                        .testTag("guide_step_${step.stepNumber}"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 3.dp else 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isExpanded) DeepEspressoPrimary else SoftToffeeSecondary.copy(alpha = 0.35f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = step.icon,
                                        contentDescription = null,
                                        tint = if (isExpanded) Color.White else DeepEspressoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Étape ${step.stepNumber} • ${step.title}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkEspresso,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Text(
                                        text = step.subtitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextWarmTaupe,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Réduire" else "Dérouler",
                                tint = DeepEspressoPrimary
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                step.bulletPoints.forEach { point ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "• ",
                                            color = DeepEspressoPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = point,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextDarkEspresso,
                                                lineHeight = 18.sp,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFFF7ED))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = step.expertTip,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DeepEspressoPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
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
