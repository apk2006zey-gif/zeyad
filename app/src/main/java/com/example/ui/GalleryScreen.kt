package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.MemoryItem
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.PlumBorder
import com.example.ui.theme.RoseGold
import com.example.ui.theme.RubyCrimson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    memories: List<MemoryItem>,
    onAddMemory: (uri: String, type: String, title: String, description: String, date: String, mood: String) -> Unit,
    onToggleFavorite: (MemoryItem) -> Unit,
    onDeleteMemory: (MemoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "IMAGE", "VIDEO", "FAVORITE"
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMediaItem by remember { mutableStateOf<MemoryItem?>(null) }

    // Picker state for Add Dialog
    var pickedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var pickedMediaType by remember { mutableStateOf("IMAGE") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Take persistent permission if possible
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}

            pickedMediaUri = uri
            val mime = context.contentResolver.getType(uri) ?: ""
            pickedMediaType = if (mime.startsWith("video")) "VIDEO" else "IMAGE"
            showAddDialog = true
        }
    }

    val filteredList = remember(memories, selectedFilter) {
        when (selectedFilter) {
            "IMAGE" -> memories.filter { it.mediaType == "IMAGE" }
            "VIDEO" -> memories.filter { it.mediaType == "VIDEO" }
            "FAVORITE" -> memories.filter { it.isFavorite }
            else -> memories
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ألبوم الصور والفيديوهات 📸",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = "كل لحظة حب جمعتنا محفوظة للأبد",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )
                },
                containerColor = RubyCrimson,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("add_memory_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Photo or Video")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("الكل (${memories.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RubyCrimson,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedFilter == "IMAGE",
                    onClick = { selectedFilter = "IMAGE" },
                    label = { Text("صور 📸") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RubyCrimson,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedFilter == "VIDEO",
                    onClick = { selectedFilter = "VIDEO" },
                    label = { Text("فيديوهات 🎬") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RubyCrimson,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedFilter == "FAVORITE",
                    onClick = { selectedFilter = "FAVORITE" },
                    label = { Text("المفضلة ❤️") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RubyCrimson,
                        selectedLabelColor = Color.White
                    )
                )
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(RubyCrimson.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = RubyCrimson,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "لا توجد ذكريات في هذا القسم بعد",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اضغط على زر (+) في الأسفل لاختيار صور وفيديوهات حكايتكم من الهاتف 🌹",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        MemoryGridCard(
                            item = item,
                            onClick = { selectedMediaItem = item },
                            onToggleFavorite = { onToggleFavorite(item) }
                        )
                    }
                }
            }
        }
    }

    // Add Memory Dialog
    if (showAddDialog && pickedMediaUri != null) {
        AddMemoryDialog(
            uri = pickedMediaUri.toString(),
            mediaType = pickedMediaType,
            onDismiss = {
                showAddDialog = false
                pickedMediaUri = null
            },
            onSave = { title, desc, date, mood ->
                onAddMemory(pickedMediaUri.toString(), pickedMediaType, title, desc, date, mood)
                showAddDialog = false
                pickedMediaUri = null
                Toast.makeText(context, "تم حفظ الذكرى بنجاح في ألبومكما 💖", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Media Viewer Fullscreen Dialog
    if (selectedMediaItem != null) {
        MediaViewerDialog(
            item = selectedMediaItem!!,
            onDismiss = { selectedMediaItem = null },
            onToggleFavorite = {
                onToggleFavorite(selectedMediaItem!!)
                selectedMediaItem = selectedMediaItem!!.copy(isFavorite = !selectedMediaItem!!.isFavorite)
            },
            onDelete = {
                onDeleteMemory(selectedMediaItem!!)
                selectedMediaItem = null
            }
        )
    }
}

@Composable
fun MemoryGridCard(
    item: MemoryItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clickable { onClick() }
            .testTag("memory_card_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PlumBorder)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.uri,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0x66000000),
                                Color(0xEE12030D)
                            )
                        )
                    )
            )

            // Video badge if VIDEO
            if (item.mediaType == "VIDEO") {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x99000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = "Video",
                        tint = RubyCrimson,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Favorite button top right
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (item.isFavorite) RubyCrimson else Color.White
                )
            }

            // Title & Details at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.dateString.isNotBlank()) {
                    Text(
                        text = item.dateString,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AddMemoryDialog(
    uri: String,
    mediaType: String,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, date: String, mood: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("اليوم") }
    var selectedMood by remember { mutableStateOf("حب وسعادة ❤️") }

    val moods = listOf("حب وسعادة ❤️", "أول مرة 🌹", "ضحكة من القلب 😊", "ذكرى لا تُنسى ✨", "رحلة سوا 🏖️")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, PlumBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (mediaType == "VIDEO") "إضافة فيديو جديد 🎬" else "إضافة صورة جديدة 📸",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, PlumBorder, RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الذكرى (مثال: أول لقاء سوا)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("memory_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("كلمات حب تعبر عن الصورة...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("memory_desc_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dateString,
                    onValueChange = { dateString = it },
                    label = { Text("تاريخ الذكرى أو مناسبتها") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalTitle = title.ifBlank { "ذكرى غالية" }
                            onSave(finalTitle, description, dateString, selectedMood)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RubyCrimson),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("save_memory_button")
                    ) {
                        Text("حفظ في الألبوم", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MediaViewerDialog(
    item: MemoryItem,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.94f))
        ) {
            AsyncImage(
                model = item.uri,
                contentDescription = item.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 60.dp)
            )

            // Top bar controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Row {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) RubyCrimson else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                }
            }

            // Bottom info card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xCC000000), Color(0xFA000000))
                        )
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                if (item.dateString.isNotBlank()) {
                    Text(
                        text = item.dateString,
                        style = MaterialTheme.typography.bodySmall.copy(color = ChampagneGold)
                    )
                }

                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFE2E2E2))
                    )
                }

                if (item.mediaType == "VIDEO") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(Uri.parse(item.uri), "video/*")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "تعذر تشغيل الفيديو", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RubyCrimson),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayCircleFilled, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تشغيل الفيديو في مشغل الهاتف")
                    }
                }
            }
        }
    }
}
