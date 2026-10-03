package com.example.prism

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.prism.data.MusicScanner
import com.example.prism.data.Song
import com.example.prism.data.Storage
import com.example.prism.playback.PlayerManager
import com.example.prism.ui.component.BottomNavBar
import com.example.prism.ui.component.MiniPlayer
import com.example.prism.ui.component.NavTab
import com.example.prism.ui.screen.*
import com.example.prism.ui.settings.*
import com.example.prism.ui.theme.PlayerStyle
import com.example.prism.ui.theme.ThemeState

enum class MainOverlay {
    NONE,
    PLAYER, QUEUE, LYRICS, EQUALIZER, PLAYLIST,
    APPEARANCE, ANIMATION, HOME_SETTINGS, LIBRARY_SETTINGS,
    PLAYLIST_SETTINGS, PLAYBACK_SETTINGS, LYRICS_SETTINGS,
    SEARCH_SETTINGS, EXTENSIONS, WIDGET_SETTINGS, STORAGE, LAB
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PrismApp() }
    }
}

@Composable
fun PrismApp() {
    val context = LocalContext.current

    // 加载持久化设置
    LaunchedEffect(Unit) {
        ThemeState.isDark = Storage.isDark()
        ThemeState.accent = Color(Storage.getAccentValue())
        ThemeState.accentValue = Storage.getAccentValue()
        ThemeState.presetId = Storage.getAccentId()
        ThemeState.glassEnabled = Storage.isGlassEnabled()
        ThemeState.glassIntensity = Storage.getGlassIntensity()
        ThemeState.playerStyle = if (Storage.getPlayerStyle() == "SALT")
            PlayerStyle.SALT else PlayerStyle.IOS26
        ThemeState.playerStyleChosen = Storage.isPlayerStyleChosen()
        ThemeState.fontFollowSystem = Storage.isFontFollowSystem()
    }

    var showOnboarding by remember { mutableStateOf(Storage.isFirstLaunch()) }
    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var hasPermission by remember { mutableStateOf(hasAudioPermission(context)) }
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var overlay by remember { mutableStateOf(MainOverlay.NONE) }
    var showSearchFull by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { hasPermission = it }

    val notifPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            val perm = if (Build.VERSION.SDK_INT >= 33)
                Manifest.permission.READ_MEDIA_AUDIO
            else
                Manifest.permission.READ_EXTERNAL_STORAGE
            permissionLauncher.launch(perm)
        }
        if (Build.VERSION.SDK_INT >= 33) {
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            songs = MusicScanner.scan(context)
            PlayerManager.setAllSongs(songs)
        }
    }

    // 返回手势
    BackHandler(enabled = true) {
        when {
            showSearchFull -> showSearchFull = false
            overlay == MainOverlay.LYRICS -> overlay = MainOverlay.PLAYER
            overlay == MainOverlay.QUEUE -> overlay = MainOverlay.PLAYER
            overlay == MainOverlay.EQUALIZER -> overlay = MainOverlay.PLAYER
            overlay == MainOverlay.PLAYER -> overlay = MainOverlay.NONE
            overlay != MainOverlay.NONE -> overlay = MainOverlay.NONE
        }
    }

    if (showOnboarding) {
        OnboardingScreen(onFinish = {
            showOnboarding = false
            Storage.markLaunched()
        })
        return
    }

    // ★ 字体跟随系统
    val systemDensity = LocalDensity.current
    val effectiveFontScale = if (ThemeState.fontFollowSystem) {
        systemDensity.fontScale
    } else {
        ThemeState.fontScale
    }

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = systemDensity.density,
            fontScale = effectiveFontScale
        )
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(ThemeState.bgStart, ThemeState.bgMid, ThemeState.bgEnd)
                )
            )
        ) {
            Column(Modifier.fillMaxSize().systemBarsPadding()) {
                Box(Modifier.weight(1f)) {
                    if (!hasPermission) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("需要音频权限", color = ThemeState.text, fontSize = 15.sp)
                        }
                    } else {
                        when (currentTab) {
                            NavTab.HOME -> HomeScreen(
                                allSongs = songs,
                                onSongClick = {
                                    PlayerManager.playSong(it, songs)
                                    overlay = MainOverlay.PLAYER
                                },
                                onOpenSearch = { showSearchFull = true }
                            )
                            NavTab.LIBRARY -> LibraryScreen(
                                allSongs = songs,
                                onSongClick = {
                                    PlayerManager.playSong(it, songs)
                                    overlay = MainOverlay.PLAYER
                                },
                                onOpenPlaylists = { overlay = MainOverlay.PLAYLIST }
                            )
                            NavTab.SEARCH -> SearchScreen(
                                allSongs = songs,
                                onSongClick = {
                                    PlayerManager.playSong(it, songs)
                                    overlay = MainOverlay.PLAYER
                                }
                            )
                            NavTab.SETTINGS -> SettingsScreen(onNavigate = { overlay = it })
                        }
                    }
                }
                MiniPlayer(
                    onExpand = { overlay = MainOverlay.PLAYER },
                    onOpenQueue = { overlay = MainOverlay.QUEUE }
                )
                BottomNavBar(
                    current = currentTab,
                    onSelect = { currentTab = it },
                    onOpenSearchOverlay = { showSearchFull = true }
                )
            }

            AnimatedVisibility(
                visible = overlay != MainOverlay.NONE,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            listOf(ThemeState.bgStart, ThemeState.bgMid, ThemeState.bgEnd)
                        )
                    )
                ) {
                    val close = { overlay = MainOverlay.NONE }
                    val backToPlayer = { overlay = MainOverlay.PLAYER }
                    when (overlay) {
                        MainOverlay.PLAYER -> PlayerScreen(
                            onBack = close,
                            onOpenLyrics = { overlay = MainOverlay.LYRICS },
                            onOpenQueue = { overlay = MainOverlay.QUEUE },
                            onOpenEqualizer = { overlay = MainOverlay.EQUALIZER }
                        )
                        MainOverlay.LYRICS -> {
                            val s = PlayerManager.currentSong.collectAsState().value
                            if (s != null) LyricsScreen(song = s, onBack = backToPlayer)
                            else LaunchedEffect(Unit) { backToPlayer() }
                        }
                        MainOverlay.QUEUE -> QueueScreen(onBack = backToPlayer)
                        MainOverlay.EQUALIZER -> EqualizerScreen(onBack = backToPlayer)
                        MainOverlay.PLAYLIST -> PlaylistScreen(onBack = close)
                        MainOverlay.APPEARANCE -> AppearanceSettings(onBack = close)
                        MainOverlay.ANIMATION -> AnimationSettings(onBack = close)
                        MainOverlay.HOME_SETTINGS -> HomeSettings(onBack = close)
                        MainOverlay.LIBRARY_SETTINGS -> LibrarySettings(onBack = close)
                        MainOverlay.PLAYLIST_SETTINGS -> PlaylistSettings(onBack = close)
                        MainOverlay.PLAYBACK_SETTINGS -> PlaybackSettings(onBack = close)
                        MainOverlay.LYRICS_SETTINGS -> LyricsSettings(onBack = close)
                        MainOverlay.SEARCH_SETTINGS -> SearchSettings(onBack = close)
                        MainOverlay.EXTENSIONS -> ExtensionsSettings(onBack = close)
                        MainOverlay.WIDGET_SETTINGS -> WidgetSettings(onBack = close)
                        MainOverlay.STORAGE -> StorageSettings(onBack = close)
                        MainOverlay.LAB -> LabSettings(onBack = close)
                        MainOverlay.NONE -> {}
                    }
                }
            }

            AnimatedVisibility(
                visible = showSearchFull,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SearchOverlayScreen(
                    allSongs = songs,
                    onSongClick = {
                        PlayerManager.playSong(it, songs)
                        showSearchFull = false
                        overlay = MainOverlay.PLAYER
                    },
                    onClose = { showSearchFull = false }
                )
            }
        }
    }
}

private fun hasAudioPermission(context: android.content.Context): Boolean {
    val perm = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_AUDIO
    else
        Manifest.permission.READ_EXTERNAL_STORAGE
    return ContextCompat.checkSelfPermission(context, perm) ==
            PackageManager.PERMISSION_GRANTED
}

@Composable
private fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    val dispatcher = (context as? ComponentActivity)?.onBackPressedDispatcher
    DisposableEffect(enabled) {
        if (!enabled || dispatcher == null) return@DisposableEffect onDispose { }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { onBack() }
        }
        dispatcher.addCallback(callback)
        onDispose { callback.remove() }
    }
}