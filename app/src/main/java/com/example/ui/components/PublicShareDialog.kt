package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.LatteBorderSoft
import com.example.ui.theme.SoftPeachCard
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe

const val PUBLIC_SHARED_APP_URL = "https://ais-pre-kt3sdklat2jmtws76kqso4-533662333695.us-west1.run.app"

@Composable
fun PublicShareDialog(
    currentWeek: Int = 24,
    momName: String = "Fatou",
    dadName: String = "Bala",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    fun copyLinkToClipboard(textToCopy: String, toastMsg: String = "Lien public copié dans le presse-papiers !") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Maman & Bébé App Link", textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
    }

    fun launchShareChooser(message: String, chooserTitle: String = "Partager Maman & Bébé") {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Maman & Bébé • $momName & $dadName")
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur de partage: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openInBrowser(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Impossible d'ouvrir le navigateur", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CrispWhiteSurface,
        shape = RoundedCornerShape(26.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SoftToffeeSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = DeepEspressoPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Partager l'Application",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Lien Public & Invitation Famille",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Official Public Link Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SquircleItemBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LatteBorderSoft, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = DeepEspressoPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "URL Publique de votre App",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepEspressoPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepEspressoPrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "En ligne • Public",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DeepEspressoPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = PUBLIC_SHARED_APP_URL,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkEspresso,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.8f))
                                .padding(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    copyLinkToClipboard(PUBLIC_SHARED_APP_URL)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_copy_public_url")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Copier",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    launchShareChooser(
                                        message = "👶 Suivez notre grossesse sur Maman & Bébé ($momName & $dadName) !\nLien public : $PUBLIC_SHARED_APP_URL",
                                        chooserTitle = "Partager le lien public"
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SoftToffeeSecondary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_share_public_url")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = DeepEspressoPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Envoyer",
                                    color = DeepEspressoPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = LatteBorderSoft.copy(alpha = 0.5f))

                // 2. Quick Share Scenarios
                Text(
                    text = "Messages Prêts à Envoyer",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextWarmTaupe,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )

                // Co-Parent / Partner Invitation
                QuickShareItem(
                    emoji = "💑",
                    title = "Inviter Papa $dadName",
                    subtitle = "Accès conjoint aux rendez-vous, contractions et prénoms",
                    onClick = {
                        val msg = "Coucou $dadName ❤️ ! Rejoins notre espace couple sur notre application Maman & Bébé pour suivre la grossesse ensemble, chronométrer les contractions et choisir nos prénoms préférés :\n$PUBLIC_SHARED_APP_URL"
                        launchShareChooser(msg, "Inviter $dadName")
                    },
                    testTag = "share_partner_invite"
                )

                // Family & Close Friends
                QuickShareItem(
                    emoji = "👨‍👩‍👧",
                    title = "Partager avec la Famille",
                    subtitle = "Partager l'avancée de la grossesse avec les grands-parents",
                    onClick = {
                        val msg = "✨ Nous partageons avec vous notre aventure de grossesse pour bébé ($momName & $dadName) ! Vous pouvez découvrir l'application ici :\n$PUBLIC_SHARED_APP_URL"
                        launchShareChooser(msg, "Partager avec la famille")
                    },
                    testTag = "share_family_invite"
                )

                // Week Progress Update
                QuickShareItem(
                    emoji = "👶",
                    title = "Partager l'Étape de la Semaine $currentWeek",
                    subtitle = "Semaine $currentWeek sur 40 • Évolution de bébé",
                    onClick = {
                        val msg = "🌸 Bilan Semaine $currentWeek sur 40 : Bébé grandit à merveille ! Suivez nos étapes sur Maman & Bébé :\n$PUBLIC_SHARED_APP_URL"
                        launchShareChooser(msg, "Partager la semaine $currentWeek")
                    },
                    testTag = "share_week_milestone"
                )

                // Helpful Info Tip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftPeachCard.copy(alpha = 0.4f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Vos proches peuvent ouvrir ce lien public directement depuis n'importe quel smartphone, tablette ou ordinateur.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkEspresso,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                modifier = Modifier.testTag("btn_close_public_share_dialog")
            ) {
                Text("Fermer", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun QuickShareItem(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
            .border(1.dp, LatteBorderSoft.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SquircleItemBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextWarmTaupe,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = DeepEspressoPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
