package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChildFriendly
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.ChildFriendly
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.viewmodel.ScreenTab

@Composable
fun MamanBottomBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("floating_pill_bottom_dock"),
        color = Color.White,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val navItems = listOf(
                Triple(ScreenTab.TODAY, androidx.compose.ui.res.stringResource(id = com.example.R.string.tab_home), Pair(Icons.Default.Home, Icons.Outlined.Home)),
                Triple(ScreenTab.CHECKLIST, androidx.compose.ui.res.stringResource(id = com.example.R.string.tab_plan), Pair(Icons.Default.Checklist, Icons.Outlined.Checklist)),
                Triple(ScreenTab.STATS, androidx.compose.ui.res.stringResource(id = com.example.R.string.tab_stats), Pair(Icons.Default.BarChart, Icons.Outlined.BarChart)),
                Triple(ScreenTab.HEALTH_LOG, androidx.compose.ui.res.stringResource(id = com.example.R.string.tab_log), Pair(Icons.Default.EditNote, Icons.Outlined.EditNote)),
                Triple(ScreenTab.ASSISTANT, androidx.compose.ui.res.stringResource(id = com.example.R.string.tab_ai), Pair(Icons.Default.AutoAwesome, Icons.Outlined.AutoAwesome))
            )

            navItems.forEach { (tab, label, icons) ->
                val isSelected = currentTab == tab
                val bg by animateColorAsState(
                    targetValue = if (isSelected) DeepEspressoPrimary else Color.Transparent,
                    label = "pillBgColor"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else TextDarkEspresso,
                    label = "pillContentColor"
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(bg)
                        .clickable { onTabSelected(tab) }
                        .padding(
                            horizontal = if (isSelected) 14.dp else 10.dp,
                            vertical = 8.dp
                        )
                        .testTag("nav_pill_${tab.name.lowercase()}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second,
                        contentDescription = label,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = contentColor
                            )
                        )
                    }
                }
            }
        }
    }
}
