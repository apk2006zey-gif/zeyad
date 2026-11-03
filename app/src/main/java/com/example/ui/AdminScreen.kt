package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.RomanticMusicManager
import com.example.data.local.PreferencesManager
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.LightChampagne
import com.example.ui.theme.PlumBorder
import com.example.ui.theme.RoseGold
import com.example.ui.theme.RubyCrimson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    prefs: PreferencesManager,
    musicManager: RomanticMusicManager,
    memoriesCount: Int,
    milestonesCount: Int,
    lettersCount: Int,
    onResetData: () -> Unit,
    onClearAllMemories: () -> Unit,
    onClearAllMilestones: () -> Unit,
    onClearAllLetters: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var myName by remember { mutableStateOf(prefs.myName) }
    var partnerName by remember { mutableStateOf(prefs.partnerName) }
    var secretPassword by remember { mutableStateOf(prefs.secretPassword) }
    var adminPin by remember { mutableStateOf(prefs.adminPin) }
    var customQuote by remember { mutableStateOf(prefs.customQuote) }
    var daysCount by remember {
        val days = (System.currentTimeMillis() - prefs.anniversaryDateMillis) / (24L * 60 * 60 * 1000)
        mutableStateOf(days.coerceAtLeast(0).toString())
    }

    var showClearMemoriesDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ChampagneGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "لوحة تحكم الأدمن 👑",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Admin Welcome Banner
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = RubyCrimson.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, ChampagneGold.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "صلاحيات الأدمن والتحكم الكامل",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ChampagneGold
                            )
                        )
                        Text(
                            text = "إدارة بيانات زياد ومريومه، كلمة السر، وقاعدة البيانات بالكامل.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Database Statistics Grid
            Text(
                text = "إحصائيات النظام وقاعدة البيانات 📊",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "الصور والفيديوهات",
                    value = "$memoriesCount",
                    icon = Icons.Default.PhotoLibrary,
                    color = RubyCrimson,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "محطات الحكاية",
                    value = "$milestonesCount",
                    icon = Icons.Default.Timeline,
                    color = RoseGold,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "رسائل الحب",
                    value = "$lettersCount",
                    icon = Icons.Default.Mail,
                    color = ChampagneGold,
                    modifier = Modifier.weight(1f)
                )
            }

            // Master Couple Credentials
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlumBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إدارة الأسماء وكلمة السر 🔐",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ChampagneGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "البيانات النشطة",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = myName,
                            onValueChange = { myName = it },
                            label = { Text("الاسم الأول (زياد)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_my_name")
                        )

                        OutlinedTextField(
                            value = partnerName,
                            onValueChange = { partnerName = it },
                            label = { Text("اسم الحبيبة (مريومه)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_partner_name")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = secretPassword,
                        onValueChange = { secretPassword = it },
                        label = { Text("كلمة السر الخاصة (الافتراضية: 18/12)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = RubyCrimson) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_password")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = adminPin,
                        onValueChange = { adminPin = it },
                        label = { Text("رمز دخول الأدمن (PIN)") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = ChampagneGold) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_field")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = daysCount,
                        onValueChange = { daysCount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("عداد الأيام معاً (الأيام منذ البداية)") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = RoseGold) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customQuote,
                        onValueChange = { customQuote = it },
                        label = { Text("العبارة الرومانسية المخصصة") },
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            prefs.myName = myName.ifBlank { "زياد" }
                            prefs.partnerName = partnerName.ifBlank { "مريومه" }
                            prefs.secretPassword = secretPassword.ifBlank { "18/12" }
                            prefs.adminPin = adminPin.ifBlank { "18/12" }
                            prefs.customQuote = customQuote
                            val d = daysCount.toLongOrNull() ?: 365L
                            prefs.anniversaryDateMillis = System.currentTimeMillis() - (d * 24L * 60 * 60 * 1000)
                            Toast.makeText(context, "تم حفظ بيانات الأدمن بنجاح 👑", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RubyCrimson),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_save_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ التعديلات في النظام", color = Color.White)
                    }
                }
            }

            // Quick Data Actions
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlumBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "إجراءات قاعدة البيانات السريعة ⚡",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            prefs.resetToDefaults()
                            myName = "زياد"
                            partnerName = "مريومه"
                            secretPassword = "18/12"
                            adminPin = "18/12"
                            onResetData()
                            Toast.makeText(context, "تمت استعادة الإعدادات الأصلية لـ زياد ومريومه (18/12) ❤️", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("استعادة بيانات زياد ومريومه الافتراضية")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showClearMemoriesDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حذف وتفريغ كل الصور والفيديوهات")
                    }
                }
            }

            // Music Synthesizer & Control
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlumBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "محاكي الموسيقى الرومانسية 🎵",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "المقطوعة الحالية: ${musicManager.getTrackName(musicManager.currentTrack.collectAsState().value)}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = ChampagneGold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { musicManager.toggleMusic() },
                            colors = ButtonDefaults.buttonColors(containerColor = RubyCrimson),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تشغيل / إيقاف")
                        }

                        Button(
                            onClick = { musicManager.nextTrack() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("المقطوعة التالية", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showClearMemoriesDialog) {
        AlertDialog(
            onDismissRequest = { showClearMemoriesDialog = false },
            title = { Text("تأكيد حذف ألبوم الوسائط") },
            text = { Text("هل أنت متأكد من تفريغ كل الصور والفيديوهات من التطبيق؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllMemories()
                        showClearMemoriesDialog = false
                        Toast.makeText(context, "تم تفريغ ألبوم الصور والفيديوهات", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("نعم، احذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearMemoriesDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, PlumBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1
            )
        }
    }
}
