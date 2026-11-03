package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.PregnancyViewModel

enum class AssistantColorTheme(
    val title: String,
    val emoji: String,
    val primary: Color,
    val onPrimary: Color,
    val headerStart: Color,
    val headerEnd: Color,
    val canvasBg: Color,
    val bubbleUser: Color,
    val bubbleAi: Color,
    val border: Color,
    val badgeBg: Color
) {
    ROSE(
        title = "Rose Blush",
        emoji = "🌸",
        primary = Color(0xFFE26D77),
        onPrimary = Color.White,
        headerStart = Color(0xFFFFECEF),
        headerEnd = Color(0xFFFDE0E4),
        canvasBg = Color(0xFFFFF9FA),
        bubbleUser = Color(0xFFE26D77),
        bubbleAi = Color.White,
        border = Color(0xFFF5CCD2),
        badgeBg = Color(0xFFFFEEF0)
    ),
    SAGE(
        title = "Gentle Sage",
        emoji = "🌿",
        primary = Color(0xFF4D7C66),
        onPrimary = Color.White,
        headerStart = Color(0xFFEAF5EE),
        headerEnd = Color(0xFFDCEDE2),
        canvasBg = Color(0xFFF7FAF8),
        bubbleUser = Color(0xFF4D7C66),
        bubbleAi = Color.White,
        border = Color(0xFFC7E2D3),
        badgeBg = Color(0xFFEDF7F1)
    ),
    LAVENDER(
        title = "Soft Lavender",
        emoji = "💜",
        primary = Color(0xFF8A68B0),
        onPrimary = Color.White,
        headerStart = Color(0xFFF3EDFA),
        headerEnd = Color(0xFFE8DEF5),
        canvasBg = Color(0xFFFAF8FD),
        bubbleUser = Color(0xFF8A68B0),
        bubbleAi = Color.White,
        border = Color(0xFFDFCFF5),
        badgeBg = Color(0xFFF5EFFF)
    ),
    PEACH(
        title = "Warm Sunset",
        emoji = "🌅",
        primary = Color(0xFFE67E44),
        onPrimary = Color.White,
        headerStart = Color(0xFFFEF0E4),
        headerEnd = Color(0xFFFDE4D0),
        canvasBg = Color(0xFFFFFBF7),
        bubbleUser = Color(0xFFE67E44),
        bubbleAi = Color.White,
        border = Color(0xFFFAD7BC),
        badgeBg = Color(0xFFFFF3E8)
    )
}

@Composable
fun MamanAssistantScreen(
    viewModel: PregnancyViewModel,
    bottomBarPadding: Dp = 80.dp,
    onOpenLanguageDialog: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isAssistantThinking.collectAsState()
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") }
    var selectedTheme by remember { mutableStateOf(AssistantColorTheme.ROSE) }
    var showThemeMenu by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val isKeyboardOpen = imeBottomPx > 0
    val keyboardHeightDp = with(density) { imeBottomPx.toDp() }

    // When keyboard is open, imePadding() smoothly handles insets; when closed, lift above bottom bar
    val activeBottomPadding = if (isKeyboardOpen) 0.dp else bottomBarPadding

    val listState = rememberLazyListState()

    // Auto-scroll chat to latest message whenever new messages arrive or keyboard opens
    LaunchedEffect(messages.size, isThinking, isKeyboardOpen) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val suggestedQuestions = listOf(
        "Is heartburn normal in week $currentWeek?",
        "How many kicks should I feel daily?",
        "Safe exercises and stretches",
        "What to pack in my hospital bag?",
        "Tips to soothe newborn colic"
    )

    val flagEmoji = when (currentLanguage) {
        "fr" -> "🇫🇷 FR"
        "es" -> "🇪🇸 ES"
        "pt" -> "🇵🇹 PT"
        "it" -> "🇮🇹 IT"
        "ar" -> "🇸🇦 AR"
        else -> "🇺🇸 EN"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(selectedTheme.canvasBg)
    ) {
        // Streamlined, Color-Themed Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(selectedTheme.headerStart, selectedTheme.headerEnd)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button + Logo + Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("assistant_header_brand")
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("btn_assistant_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = selectedTheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            painter = painterResource(id = R.drawable.img_maman_bebe_logo),
                            contentDescription = "Maman & Bébé Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, selectedTheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.assistant_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E140C),
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "Week $currentWeek • Prenatal AI Companion",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF6E5647),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Action buttons: Color Theme Picker & Language
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Color theme toggle button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(CrispWhiteSurface)
                                .border(1.dp, selectedTheme.border, RoundedCornerShape(14.dp))
                                .clickable {
                                    val all = AssistantColorTheme.values()
                                    val nextIndex = (selectedTheme.ordinal + 1) % all.size
                                    selectedTheme = all[nextIndex]
                                    Toast.makeText(context, "Theme: ${selectedTheme.title}", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("assistant_theme_toggle")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = selectedTheme.emoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Change Color",
                                    tint = selectedTheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        // In-App Language Selector Pill Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(CrispWhiteSurface)
                                .border(1.dp, selectedTheme.border, RoundedCornerShape(14.dp))
                                .clickable { onOpenLanguageDialog() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("assistant_lang_selector")
                        ) {
                            Text(
                                text = flagEmoji,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E140C),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Quick Color Theme Selector Strip if user tapped theme or for rapid discovery
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Theme:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = Color(0xFF7A6458),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    AssistantColorTheme.values().forEach { theme ->
                        val isSelected = theme == selectedTheme
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) theme.primary else CrispWhiteSurface)
                                .border(1.dp, if (isSelected) theme.primary else theme.border, RoundedCornerShape(12.dp))
                                .clickable { selectedTheme = theme }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = theme.emoji, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = theme.title.substringBefore(" "),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else Color(0xFF1E140C),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Suggested Prompts Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            items(suggestedQuestions) { question ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CrispWhiteSurface)
                        .border(1.dp, selectedTheme.border, RoundedCornerShape(16.dp))
                        .clickable {
                            inputText = question
                            viewModel.askAssistant(question)
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("chip_suggested_${question.take(8)}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = question,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E140C),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Chat Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                ChatBubbleWarm(
                    msg = msg,
                    theme = selectedTheme,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Maman Guidance", msg.text))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = selectedTheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Maman Assistant is preparing personalized guidance…",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6E5647),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Bottom Input Card with Send Button & Elevated above Keyboard
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(bottom = activeBottomPadding),
            color = selectedTheme.canvasBg,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                // Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        textStyle = TextStyle(
                            color = Color(0xFF1E140C),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.assistant_placeholder),
                                fontSize = 12.sp,
                                color = Color(0xFF8A7568)
                            )
                        },
                        trailingIcon = {
                            if (inputText.isNotEmpty()) {
                                IconButton(
                                    onClick = { inputText = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color(0xFF8A7568),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("assistant_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        minLines = 1,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E140C),
                            unfocusedTextColor = Color(0xFF1E140C),
                            focusedPlaceholderColor = Color(0xFF8A7568),
                            unfocusedPlaceholderColor = Color(0xFF8A7568),
                            cursorColor = selectedTheme.primary,
                            focusedBorderColor = selectedTheme.primary,
                            unfocusedBorderColor = selectedTheme.border,
                            focusedContainerColor = CrispWhiteSurface,
                            unfocusedContainerColor = CrispWhiteSurface
                        )
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.askAssistant(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(selectedTheme.primary)
                            .testTag("btn_send_assistant")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(id = R.string.send),
                            tint = selectedTheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Medical disclaimer note
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = selectedTheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(id = R.string.assistant_disclaimer),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = Color(0xFF7A6458)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleWarm(
    msg: ChatMessage,
    theme: AssistantColorTheme,
    onCopy: () -> Unit
) {
    val isUser = msg.isUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(theme.badgeBg)
                    .border(1.dp, theme.border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = theme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(
                    elevation = if (isUser) 2.dp else 3.dp,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    ),
                    spotColor = Color.Black.copy(alpha = 0.05f)
                ),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) theme.bubbleUser else theme.bubbleAi
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isUser) Color.White else Color(0xFF1E140C),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = theme.primary.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onCopy() }
                        )
                    }
                }
            }
        }
    }
}
