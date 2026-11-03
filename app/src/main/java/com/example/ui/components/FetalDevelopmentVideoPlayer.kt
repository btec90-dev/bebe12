package com.example.ui.components

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.R
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.PeachHeaderBackground
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import kotlinx.coroutines.delay

// Default public high-quality streaming sample / fetal development video URL
// Can be customized dynamically in UI or via React Native CDN asset URL:
// "https://your-cdn.com/assets/fetal_development_3d.mp4"
const val DEFAULT_FETAL_VIDEO_URL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"

@OptIn(UnstableApi::class)
@Composable
fun FetalDevelopmentVideoPlayer(
    currentWeek: Int,
    modifier: Modifier = Modifier,
    initialVideoUri: String = DEFAULT_FETAL_VIDEO_URL
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var videoUriString by remember { mutableStateOf(initialVideoUri) }
    var isPlaying by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var isCoverMode by remember { mutableStateOf(true) } // resizeMode="cover"
    var isFullscreen by remember { mutableStateOf(false) }
    var showUrlDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var controlsVisible by remember { mutableStateOf(true) }

    // ExoPlayer creation & lifecycle management (safe try-catch to prevent codec/headless crashes)
    val exoPlayer = remember(context) {
        try {
            ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                playWhenReady = true
            }
        } catch (_: Exception) {
            null
        }
    }

    // Set video source when URI changes
    LaunchedEffect(videoUriString, exoPlayer) {
        val player = exoPlayer ?: run {
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUriString))
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            isPlaying = true
        } catch (_: Exception) {
            isLoading = false
        }
    }

    // Player events listener
    DisposableEffect(exoPlayer) {
        val player = exoPlayer
        if (player == null) {
            isLoading = false
            onDispose { }
        } else {
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }

                override fun onPlaybackStateChanged(state: Int) {
                    isLoading = (state == Player.STATE_BUFFERING)
                    if (state == Player.STATE_READY) {
                        totalDurationMs = player.duration.coerceAtLeast(0L)
                    }
                }
            }
            player.addListener(listener)

            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> player.pause()
                    Lifecycle.Event.ON_RESUME -> {
                        // Resume if needed
                    }
                    Lifecycle.Event.ON_DESTROY -> player.release()
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                player.removeListener(listener)
                player.release()
            }
        }
    }

    // Position updater ticker
    LaunchedEffect(isPlaying, exoPlayer) {
        val player = exoPlayer ?: return@LaunchedEffect
        while (isPlaying) {
            currentPositionMs = player.currentPosition.coerceAtLeast(0L)
            totalDurationMs = player.duration.coerceAtLeast(0L)
            delay(500)
        }
    }

    // Toggle mute
    LaunchedEffect(isMuted, exoPlayer) {
        exoPlayer?.volume = if (isMuted) 0f else 1f
    }

    // Main Player Layout (Embedded in Card)
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    controlsVisible = !controlsVisible
                }
                .testTag("fetal_video_player_box"),
            contentAlignment = Alignment.Center
        ) {
            // AndroidView hosting Media3 PlayerView
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = if (isCoverMode) {
                            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        } else {
                            AspectRatioFrameLayout.RESIZE_MODE_FIT
                        }
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { view ->
                    view.resizeMode = if (isCoverMode) {
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    } else {
                        AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Loading Spinner
            if (isLoading) {
                CircularProgressIndicator(
                    color = PeachHeaderBackground,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Top Status Badges (Always clear or subtle overlay)
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Fetal 3D Video Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = PeachHeaderBackground,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Week $currentWeek 3D Video",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                // Quick Action Buttons (Edit URL, ResizeMode Cover, Fullscreen)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit CDN URL button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .clickable { showUrlDialog = true }
                            .testTag("btn_edit_video_url"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Video URL",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // ResizeMode Cover toggle
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (isCoverMode) DeepEspressoPrimary else Color.Black.copy(alpha = 0.65f))
                            .clickable { isCoverMode = !isCoverMode }
                            .testTag("btn_toggle_cover_mode"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = "Toggle Cover Mode",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Fullscreen button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .clickable { isFullscreen = true }
                            .testTag("btn_fullscreen_video"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Center Play/Pause button when tapped
            androidx.compose.animation.AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(DeepEspressoPrimary.copy(alpha = 0.85f))
                        .clickable {
                            if (exoPlayer?.isPlaying == true) {
                                exoPlayer?.pause()
                            } else {
                                exoPlayer?.play()
                            }
                        }
                        .testTag("btn_center_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Bottom Player Control Bar (Progress Scrubber & Controls)
            androidx.compose.animation.AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Scrubber Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTimeMs(currentPositionMs),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Slider(
                            value = if (totalDurationMs > 0) (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f) else 0f,
                            onValueChange = { frac ->
                                if (totalDurationMs > 0) {
                                    val seekPos = (frac * totalDurationMs).toLong()
                                    exoPlayer?.seekTo(seekPos)
                                    currentPositionMs = seekPos
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(24.dp)
                                .padding(horizontal = 6.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = PeachHeaderBackground,
                                activeTrackColor = PeachHeaderBackground,
                                inactiveTrackColor = Color.White.copy(alpha = 0.35f)
                            )
                        )
                        Text(
                            text = formatTimeMs(totalDurationMs),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 9.sp
                            )
                        )
                    }

                    // Bottom Buttons (Play/Pause, Mute, Replay, Mode readout)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (exoPlayer?.isPlaying == true) exoPlayer?.pause() else exoPlayer?.play()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = if (isMuted) "Unmute" else "Mute",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { exoPlayer?.seekTo(0) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Replay",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isCoverMode) "Mode: Cover" else "Mode: Fit",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset video / stream options pill row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val presets = listOf(
                "3D Ultrasound" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                "Womb Movement" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                "Heartbeat 3D" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
            )

            presets.forEach { (name, url) ->
                val isSelected = videoUriString == url
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) DeepEspressoPrimary else SquircleItemBg)
                        .clickable {
                            videoUriString = url
                        }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextDarkEspresso,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }

    // Fullscreen Dialog Player
    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = true
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Exit Fullscreen Button
                    IconButton(
                        onClick = { isFullscreen = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // Custom Video URL Edit Dialog
    if (showUrlDialog) {
        var tempUrl by remember { mutableStateOf(videoUriString) }
        AlertDialog(
            onDismissRequest = { showUrlDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.video_custom_url),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(id = R.string.video_enter_url),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempUrl,
                        onValueChange = { tempUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://your-cdn.com/assets/fetal_development_3d.mp4") },
                        singleLine = false,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Example: https://your-cdn.com/assets/fetal_development_3d.mp4",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextWarmTaupe, fontSize = 10.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempUrl.isNotBlank()) {
                            videoUriString = tempUrl.trim()
                        }
                        showUrlDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                ) {
                    Text(stringResource(id = R.string.save), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUrlDialog = false }) {
                    Text(stringResource(id = R.string.cancel), color = TextDarkEspresso)
                }
            },
            containerColor = CrispWhiteSurface,
            shape = RoundedCornerShape(22.dp)
        )
    }
}

private fun formatTimeMs(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
}
