package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.PeachHeaderBackground
import com.example.ui.theme.TextDarkEspresso

@Composable
fun UserProfileHeader(
    name: String,
    photoUri: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val effectivePhoto = remember(photoUri) {
        if (photoUri.isNotBlank()) {
            photoUri
        } else {
            val localAvatar = java.io.File(context.filesDir, "user_profile_avatar.jpg")
            if (localAvatar.exists() && localAvatar.length() > 0) {
                android.net.Uri.fromFile(localAvatar).toString()
            } else ""
        }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(CrispWhiteSurface)
            .border(1.5.dp, DeepEspressoPrimary, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("user_profile_header_component"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PeachHeaderBackground)
                .border(1.2.dp, DeepEspressoPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (effectivePhoto.isNotBlank()) {
                AsyncImage(
                    model = effectivePhoto,
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = if (name.isNotBlank()) name.take(1).uppercase() else "M",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepEspressoPrimary,
                        fontSize = 13.sp
                    )
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (name.isNotBlank()) name else "Maman",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextDarkEspresso,
                fontSize = 12.sp
            ),
            maxLines = 1
        )
    }
}
