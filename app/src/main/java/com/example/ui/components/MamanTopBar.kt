package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.CaramelPrimary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.viewmodel.ScreenTab

@Composable
fun MamanTopBar(
    currentWeek: Int,
    currentTab: ScreenTab,
    profileName: String,
    profileAge: Int,
    profilePhotoUri: String,
    titleColorHex: String,
    onWeekClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPublicShareClick: () -> Unit = {}
) {
    val parsedTitleColor = remember(titleColorHex) {
        try {
            Color(android.graphics.Color.parseColor(titleColorHex))
        } catch (_: Exception) {
            Color.White
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CaramelPrimary,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Prominent Maman & Bébé Logo + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onWeekClick() }
                    .testTag("app_branding_title")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_maman_bebe_logo),
                    contentDescription = "Maman & Bébé Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(1.8.dp, Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = stringResource(id = R.string.app_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Semaine $currentWeek",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Top Right: Reusable UserProfileHeader Component + Settings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                UserProfileHeader(
                    name = profileName,
                    photoUri = profilePhotoUri,
                    onClick = onProfileClick
                )

                // Public Share Icon
                IconButton(
                    onClick = onPublicShareClick,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("btn_top_public_share")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Partager l'application",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Settings Icon
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("btn_top_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Paramètres",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
