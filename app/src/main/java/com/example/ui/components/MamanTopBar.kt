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
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.PeachHeaderBackground
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
            DeepEspressoPrimary
        }
    }

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
                .padding(horizontal = 14.dp, vertical = 4.dp),
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.8.dp, DeepEspressoPrimary, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = stringResource(id = R.string.app_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = parsedTitleColor,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "Week $currentWeek",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DeepEspressoPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
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
                        tint = DeepEspressoPrimary,
                        modifier = Modifier.size(18.dp)
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
                        contentDescription = "Settings",
                        tint = DeepEspressoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
