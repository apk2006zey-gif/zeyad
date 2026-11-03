package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RubyCrimson

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Ensure natural RTL layout for Arabic
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isUnlocked by viewModel.isUnlocked.collectAsStateWithLifecycle()
    val isPlayingMusic by viewModel.isPlayingMusic.collectAsStateWithLifecycle()
    val currentTrackIndex by viewModel.currentTrackIndex.collectAsStateWithLifecycle()

    val memories by viewModel.memories.collectAsStateWithLifecycle()
    val milestones by viewModel.milestones.collectAsStateWithLifecycle()
    val letters by viewModel.letters.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isUnlocked && currentScreen != AppScreen.LOCK && currentScreen != AppScreen.SETUP && currentScreen != AppScreen.ADMIN) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RubyCrimson,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.GALLERY,
                        onClick = { viewModel.navigateTo(AppScreen.GALLERY) },
                        icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "الألبوم") },
                        label = { Text("الألبوم") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RubyCrimson,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_gallery")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.TIMELINE,
                        onClick = { viewModel.navigateTo(AppScreen.TIMELINE) },
                        icon = { Icon(Icons.Default.Timeline, contentDescription = "حكايتنا") },
                        label = { Text("حكايتنا") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RubyCrimson,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_timeline")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.LETTERS,
                        onClick = { viewModel.navigateTo(AppScreen.LETTERS) },
                        icon = { Icon(Icons.Default.Mail, contentDescription = "رسائلنا") },
                        label = { Text("رسائلنا") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RubyCrimson,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_letters")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "الإعدادات") },
                        label = { Text("الإعدادات") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RubyCrimson,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition",
            modifier = Modifier.padding(innerPadding)
        ) { screen ->
            when (screen) {
                AppScreen.LOCK -> {
                    LockScreen(
                        defaultPartner1 = viewModel.prefs.myName,
                        defaultPartner2 = viewModel.prefs.partnerName,
                        onUnlockSuccess = { viewModel.unlockApp() },
                        onGoToSetup = { viewModel.navigateTo(AppScreen.SETUP) },
                        onGoToAdmin = { viewModel.navigateTo(AppScreen.ADMIN) },
                        onValidateCredentials = { name, pass ->
                            viewModel.prefs.validateCredentials(name, pass)
                        },
                        onValidateAdminPin = { pin ->
                            viewModel.prefs.validateAdminPin(pin)
                        }
                    )
                }

                AppScreen.SETUP -> {
                    BackHandler {
                        viewModel.navigateTo(if (isUnlocked) AppScreen.HOME else AppScreen.LOCK)
                    }
                    SetupCoupleScreen(
                        prefs = viewModel.prefs,
                        onSaveCompleted = { viewModel.unlockApp() },
                        onCancel = {
                            viewModel.navigateTo(if (isUnlocked) AppScreen.HOME else AppScreen.LOCK)
                        }
                    )
                }

                AppScreen.HOME -> {
                    HomeScreen(
                        prefs = viewModel.prefs,
                        musicManager = viewModel.musicManager,
                        isPlayingMusic = isPlayingMusic,
                        currentTrackIndex = currentTrackIndex,
                        memoriesCount = memories.size,
                        milestonesCount = milestones.size,
                        lettersCount = letters.size,
                        onNavigateToGallery = { viewModel.navigateTo(AppScreen.GALLERY) },
                        onNavigateToTimeline = { viewModel.navigateTo(AppScreen.TIMELINE) },
                        onNavigateToLetters = { viewModel.navigateTo(AppScreen.LETTERS) },
                        onLockApp = { viewModel.lockApp() }
                    )
                }

                AppScreen.GALLERY -> {
                    BackHandler { viewModel.navigateTo(AppScreen.HOME) }
                    GalleryScreen(
                        memories = memories,
                        onAddMemory = { uri, type, title, desc, date, mood ->
                            viewModel.addMemory(uri, type, title, desc, date, mood)
                        },
                        onToggleFavorite = { memory ->
                            viewModel.toggleMemoryFavorite(memory)
                        },
                        onDeleteMemory = { memory ->
                            viewModel.deleteMemory(memory)
                        }
                    )
                }

                AppScreen.TIMELINE -> {
                    BackHandler { viewModel.navigateTo(AppScreen.HOME) }
                    StoryTimelineScreen(
                        milestones = milestones,
                        onAddMilestone = { title, date, story, photo ->
                            viewModel.addMilestone(title, date, story, photo)
                        },
                        onDeleteMilestone = { milestone ->
                            viewModel.deleteMilestone(milestone)
                        }
                    )
                }

                AppScreen.LETTERS -> {
                    BackHandler { viewModel.navigateTo(AppScreen.HOME) }
                    LoveLettersScreen(
                        letters = letters,
                        partner1Name = viewModel.prefs.myName,
                        partner2Name = viewModel.prefs.partnerName,
                        onAddLetter = { sender, recipient, title, body, date ->
                            viewModel.addLetter(sender, recipient, title, body, date)
                        },
                        onDeleteLetter = { letter ->
                            viewModel.deleteLetter(letter)
                        }
                    )
                }

                AppScreen.SETTINGS -> {
                    BackHandler { viewModel.navigateTo(AppScreen.HOME) }
                    SettingsScreen(
                        prefs = viewModel.prefs,
                        musicManager = viewModel.musicManager,
                        onNavigateToAdmin = { viewModel.navigateTo(AppScreen.ADMIN) },
                        onLockApp = { viewModel.lockApp() }
                    )
                }

                AppScreen.ADMIN -> {
                    BackHandler {
                        viewModel.navigateTo(if (isUnlocked) AppScreen.SETTINGS else AppScreen.LOCK)
                    }
                    AdminScreen(
                        prefs = viewModel.prefs,
                        musicManager = viewModel.musicManager,
                        memoriesCount = memories.size,
                        milestonesCount = milestones.size,
                        lettersCount = letters.size,
                        onResetData = { viewModel.seedInitialRomanticDataIfNeeded(force = true) },
                        onClearAllMemories = { viewModel.clearAllMemories() },
                        onClearAllMilestones = { viewModel.clearAllMilestones() },
                        onClearAllLetters = { viewModel.clearAllLetters() },
                        onBack = {
                            viewModel.navigateTo(if (isUnlocked) AppScreen.SETTINGS else AppScreen.LOCK)
                        }
                    )
                }
            }
        }
    }
}
