package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.PreferencesManager
import com.example.ui.theme.PlumBorder
import com.example.ui.theme.RubyCrimson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupCoupleScreen(
    prefs: PreferencesManager,
    onSaveCompleted: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var myName by remember { mutableStateOf(prefs.myName) }
    var partnerName by remember { mutableStateOf(prefs.partnerName) }
    var password by remember { mutableStateOf(prefs.secretPassword) }
    var daysAgoInput by remember { mutableStateOf("365") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إعداد بيانات الحبيبين والسر") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlumBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "تفاصيل العلاقة وكلمة السر المشتركة",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "هذه البيانات هي التي تحمي التطبيق وتربط كل الذكريات بينكما ❤️",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = myName,
                        onValueChange = { myName = it },
                        label = { Text("اسمك أنت") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RubyCrimson) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_my_name"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = partnerName,
                        onValueChange = { partnerName = it },
                        label = { Text("اسم حبيبتك / شريكة روحك") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RubyCrimson) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_partner_name"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة السر الخاصة بكما") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = RubyCrimson) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_password"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = daysAgoInput,
                        onValueChange = { daysAgoInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("منذ متى أنتم معاً؟ (عدد الأيام)") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = RubyCrimson) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_days_count"),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val days = daysAgoInput.toLongOrNull() ?: 365L
                    val anniversaryTime = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000)
                    prefs.setupProfile(
                        myName = myName,
                        partnerName = partnerName,
                        password = password,
                        anniversaryMillis = anniversaryTime
                    )
                    onSaveCompleted()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("setup_save_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RubyCrimson)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "حفظ ودخول إلى التطبيق",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
