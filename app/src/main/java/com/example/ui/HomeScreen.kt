package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.RomanticMusicManager
import com.example.data.local.PreferencesManager
import com.example.data.local.RomanticQuotesRepository
import com.example.ui.components.FloatingHeartsCanvas
import com.example.ui.components.LoveCountdownCard
import com.example.ui.components.RomanticAudioBar
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.PlumBorder
import com.example.ui.theme.RoseGold
import com.example.ui.theme.RubyCrimson

@Composable
fun HomeScreen(
    prefs: PreferencesManager,
    musicManager: RomanticMusicManager,
    isPlayingMusic: Boolean,
    currentTrackIndex: Int,
    memoriesCount: Int,
    milestonesCount: Int,
    lettersCount: Int,
    onNavigateToGallery: () -> Unit,
    onNavigateToTimeline: () -> Unit,
    onNavigateToLetters: () -> Unit,
    onLockApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentQuote by remember { mutableStateOf(RomanticQuotesRepository.getRandomQuote()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Floating subtle hearts in the background
        FloatingHeartsCanvas(particleCount = 14)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "حكايتنا الغالية ❤️",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = "${prefs.myName} 💍 ${prefs.partnerName}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    IconButton(
                        onClick = onLockApp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("home_lock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock App",
                            tint = RubyCrimson
                        )
                    }
                }
            }

            // Hero Romantic Banner
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("romantic_hero_banner_card"),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, PlumBorder)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.romantic_hero_banner),
                            contentDescription = "Romantic Couple Under Stars",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient Overlay for smooth romantic text integration
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0x991A0412),
                                            Color(0xEA1A0412)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "كل ثانية معك هي نبضٌ جديد للحياة",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "ملاذنا الآمن وسر حكايتنا الجميلة 🌹",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ChampagneGold
                                )
                            )
                        }
                    }
                }
            }

            // Romantic Audio Bar (Music Player)
            item {
                RomanticAudioBar(
                    musicManager = musicManager,
                    isPlaying = isPlayingMusic,
                    currentTrackIndex = currentTrackIndex
                )
            }

            // Love Countdown Card
            item {
                LoveCountdownCard(
                    partner1Name = prefs.myName,
                    partner2Name = prefs.partnerName,
                    anniversaryMillis = prefs.anniversaryDateMillis
                )
            }

            // Romantic Whisper / Quote of the Day
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("daily_quote_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PlumBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "همسة العشق اليومية",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ChampagneGold
                                    )
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        currentQuote = RomanticQuotesRepository.getRandomQuote()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "New Quote",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Love Quote", currentQuote.text)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "تم نسخ الهمسة الرومانسية بنجاح 💖", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Quote",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "“${currentQuote.text}”",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "— ${currentQuote.author}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = RubyCrimson,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            // Quick Access Sections Cards
            item {
                Text(
                    text = "أقسام ملاذنا الرومانسي",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickFeatureCard(
                        title = "ألبوم الصور والفيديو",
                        count = "$memoriesCount ذكرى",
                        icon = Icons.Default.PhotoLibrary,
                        color = RubyCrimson,
                        onClick = onNavigateToGallery,
                        modifier = Modifier.weight(1f),
                        testTag = "home_goto_gallery"
                    )

                    QuickFeatureCard(
                        title = "حكايتنا ومحطاتنا",
                        count = "$milestonesCount محطة",
                        icon = Icons.Default.Timeline,
                        color = RoseGold,
                        onClick = onNavigateToTimeline,
                        modifier = Modifier.weight(1f),
                        testTag = "home_goto_timeline"
                    )

                    QuickFeatureCard(
                        title = "رسائل وكلام حلو",
                        count = "$lettersCount رسالة",
                        icon = Icons.Default.Mail,
                        color = ChampagneGold,
                        onClick = onNavigateToLetters,
                        modifier = Modifier.weight(1f),
                        testTag = "home_goto_letters"
                    )
                }
            }
        }
    }
}

@Composable
fun QuickFeatureCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, PlumBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = count,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
