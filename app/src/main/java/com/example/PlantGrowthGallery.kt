package com.example

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.PlantPhotoEntity
import com.example.viewmodel.PlantViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PlantGrowthGallery(
    plantId: Long,
    viewModel: PlantViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val photos by remember(plantId) {
        viewModel.getPhotosForPlant(plantId)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPhotoForAnnotations by remember { mutableStateOf<PlantPhotoEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("plant_growth_gallery_container"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📸 Growth Progress Gallery",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Document visual growth and foliage stages over time (tap card for milestones)",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("btn_log_growth_photo")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add progress", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (photos.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🌱 No Visual Progress Yet",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Take snapshot entries at different cycles to observe how your greenery thrives.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    PhotoGalleryItem(
                        photo = photo,
                        onDeleteClick = {
                            viewModel.deletePlantPhoto(photo)
                            Toast.makeText(context, "Progress photo deleted", Toast.LENGTH_SHORT).show()
                        },
                        onClick = {
                            selectedPhotoForAnnotations = photo
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddGrowthPhotoDialog(
            onDismiss = { showAddDialog = false },
            onSave = { presetName, notes ->
                viewModel.addPlantPhoto(plantId, "preset:$presetName", notes)
                showAddDialog = false
                Toast.makeText(context, "Progress photo saved!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (selectedPhotoForAnnotations != null) {
        PhotoAnnotationsDialog(
            photo = selectedPhotoForAnnotations!!,
            viewModel = viewModel,
            onDismiss = { selectedPhotoForAnnotations = null }
        )
    }
}

@Composable
fun PhotoGalleryItem(
    photo: PlantPhotoEntity,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    val dateStr = remember(photo.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(photo.timestamp))
    }

    Card(
        modifier = Modifier
            .width(130.dp)
            .clickable(onClick = onClick)
            .testTag("growth_gallery_card_${photo.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Vector graphic representation based on selected preset
                when (photo.imageUriOrBase64) {
                    "preset:seedling" -> SproutGrowthGraphic(modifier = Modifier.size(54.dp))
                    "preset:branching" -> BranchingGrowthGraphic(modifier = Modifier.size(54.dp))
                    "preset:budding" -> BloomGrowthGraphic(modifier = Modifier.size(54.dp))
                    "preset:mature" -> CanopyGrowthGraphic(modifier = Modifier.size(54.dp))
                    else -> SproutGrowthGraphic(modifier = Modifier.size(54.dp))
                }

                // Delete handle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                            .size(24.dp)
                            .testTag("btn_delete_growth_photo_${photo.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete snapshot",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (photo.note.isEmpty()) "Healthy progress" else photo.note,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    modifier = Modifier.testTag("growth_photo_note")
                )
            }
        }
    }
}

// Dialog to add visual growth details
@Composable
fun AddGrowthPhotoDialog(
    onDismiss: () -> Unit,
    onSave: (presetName: String, notes: String) -> Unit
) {
    var notesInput by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf("seedling") } // seedling, branching, budding, mature

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📸 Log Growth Milestone",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, "close")
                    }
                }

                Text(
                    text = "Select the plant's current physiological stage to document in the gallery history:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // High fidelity preset selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PresetSelectorBox(
                        title = "Seedling",
                        tag = "seedling",
                        isSelected = selectedPreset == "seedling",
                        icon = { SproutGrowthGraphic(modifier = Modifier.size(24.dp)) },
                        onClick = { selectedPreset = "seedling" }
                    )
                    PresetSelectorBox(
                        title = "Branching",
                        tag = "branching",
                        isSelected = selectedPreset == "branching",
                        icon = { BranchingGrowthGraphic(modifier = Modifier.size(24.dp)) },
                        onClick = { selectedPreset = "branching" }
                    )
                    PresetSelectorBox(
                        title = "Budding",
                        tag = "budding",
                        isSelected = selectedPreset == "budding",
                        icon = { BloomGrowthGraphic(modifier = Modifier.size(24.dp)) },
                        onClick = { selectedPreset = "budding" }
                    )
                    PresetSelectorBox(
                        title = "Mature Canopy",
                        tag = "mature",
                        isSelected = selectedPreset == "mature",
                        icon = { CanopyGrowthGraphic(modifier = Modifier.size(24.dp)) },
                        onClick = { selectedPreset = "mature" }
                    )
                }

                // Growth Notes Input
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Foliage Logging Notes", fontSize = 11.sp) },
                    placeholder = { Text("E.g., Sprouting first leaves! Or blooming flower...", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("growth_notes_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onSave(selectedPreset, notesInput.trim())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_save_growth_photo")
                    ) {
                        Text("Add Snapshot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PresetSelectorBox(
    title: String,
    tag: String,
    isSelected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            )
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("growth_preset_btn_$tag"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            icon()
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 8.sp,
                lineHeight = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- Custom Vector Graphic Shaders (Guaranteed to render sharp & look gorgeous) ---

@Composable
fun SproutGrowthGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        // Draw soil pot
        val potWidth = size.width * 0.45f
        val potHeight = size.height * 0.25f
        val potPath = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.95f)
            lineTo(size.width * 0.7f, size.height * 0.95f)
            lineTo(size.width * 0.78f, size.height * 0.72f)
            lineTo(size.width * 0.22f, size.height * 0.72f)
            close()
        }
        drawPath(path = potPath, color = Color(0xFF8D6E63)) // Terracotta color

        // Draw Sprout Stem
        val stemPath = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.73f)
            cubicTo(
                size.width * 0.5f, size.height * 0.55f,
                size.width * 0.43f, size.height * 0.42f,
                size.width * 0.55f, size.height * 0.3f
            )
        }
        drawPath(
            path = stemPath,
            color = Color(0xFF4CAF50),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )

        // Draw Sprout Leaves
        drawPath(
            path = Path().apply {
                moveTo(size.width * 0.55f, size.height * 0.3f)
                cubicTo(
                    size.width * 0.7f, size.height * 0.22f,
                    size.width * 0.65f, size.height * 0.43f,
                    size.width * 0.55f, size.height * 0.3f
                )
            },
            color = Color(0xFF81C784)
        )
        drawPath(
            path = Path().apply {
                moveTo(size.width * 0.48f, size.height * 0.48f)
                cubicTo(
                    size.width * 0.28f, size.height * 0.45f,
                    size.width * 0.38f, size.height * 0.6f,
                    size.width * 0.48f, size.height * 0.48f
                )
            },
            color = Color(0xFF81C784)
        )
    }
}

@Composable
fun BranchingGrowthGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val potPath = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.95f)
            lineTo(size.width * 0.7f, size.height * 0.95f)
            lineTo(size.width * 0.78f, size.height * 0.72f)
            lineTo(size.width * 0.22f, size.height * 0.72f)
            close()
        }
        drawPath(path = potPath, color = Color(0xFF8D6E63))

        // Center branch
        drawLine(
            color = Color(0xFF4CAF50),
            start = Offset(size.width * 0.5f, size.height * 0.73f),
            end = Offset(size.width * 0.5f, size.height * 0.25f),
            strokeWidth = 4.dp.toPx()
        )

        // Side branches
        drawLine(
            color = Color(0xFF4CAF50),
            start = Offset(size.width * 0.5f, size.height * 0.55f),
            end = Offset(size.width * 0.3f, size.height * 0.42f),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = Color(0xFF4CAF50),
            start = Offset(size.width * 0.5f, size.height * 0.45f),
            end = Offset(size.width * 0.72f, size.height * 0.33f),
            strokeWidth = 3.dp.toPx()
        )

        // Leaves clusters
        drawCircle(color = Color(0xFF4CAF50), radius = 8.dp.toPx(), center = Offset(size.width * 0.3f, size.height * 0.42f))
        drawCircle(color = Color(0xFF81C784), radius = 7.dp.toPx(), center = Offset(size.width * 0.72f, size.height * 0.33f))
        drawCircle(color = Color(0xFF66BB6A), radius = 9.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.22f))
    }
}

@Composable
fun BloomGrowthGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val potPath = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.95f)
            lineTo(size.width * 0.7f, size.height * 0.95f)
            lineTo(size.width * 0.78f, size.height * 0.72f)
            lineTo(size.width * 0.22f, size.height * 0.72f)
            close()
        }
        drawPath(path = potPath, color = Color(0xFF8D6E63))

        // Main stem
        drawLine(
            color = Color(0xFF388E3C),
            start = Offset(size.width * 0.5f, size.height * 0.73f),
            end = Offset(size.width * 0.5f, size.height * 0.35f),
            strokeWidth = 4.dp.toPx()
        )

        // Leaves
        drawCircle(color = Color(0xFF4CAF50), radius = 6.dp.toPx(), center = Offset(size.width * 0.4f, size.height * 0.55f))
        drawCircle(color = Color(0xFF4CAF50), radius = 6.dp.toPx(), center = Offset(size.width * 0.6f, size.height * 0.48f))

        // First Bloom petals (5-petal floral ring)
        val bloomCenter = Offset(size.width * 0.5f, size.height * 0.32f)
        val petalRadius = 6.dp.toPx()
        drawCircle(color = Color(0xFFFF8A80), radius = petalRadius, center = Offset(bloomCenter.x - 5.dp.toPx(), bloomCenter.y - 5.dp.toPx()))
        drawCircle(color = Color(0xFFFF8A80), radius = petalRadius, center = Offset(bloomCenter.x + 5.dp.toPx(), bloomCenter.y - 5.dp.toPx()))
        drawCircle(color = Color(0xFFFF8A80), radius = petalRadius, center = Offset(bloomCenter.x - 5.dp.toPx(), bloomCenter.y + 5.dp.toPx()))
        drawCircle(color = Color(0xFFFF8A80), radius = petalRadius, center = Offset(bloomCenter.x + 5.dp.toPx(), bloomCenter.y + 5.dp.toPx()))
        drawCircle(color = Color(0xFFFFCC80), radius = 5.dp.toPx(), center = bloomCenter) // center pistil
    }
}

@Composable
fun CanopyGrowthGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val potPath = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.95f)
            lineTo(size.width * 0.7f, size.height * 0.95f)
            lineTo(size.width * 0.78f, size.height * 0.72f)
            lineTo(size.width * 0.22f, size.height * 0.72f)
            close()
        }
        drawPath(path = potPath, color = Color(0xFF8D6E63))

        // Main wood trunk
        drawLine(
            color = Color(0xFF5D4037),
            start = Offset(size.width * 0.5f, size.height * 0.73f),
            end = Offset(size.width * 0.5f, size.height * 0.42f),
            strokeWidth = 6.dp.toPx()
        )

        // Magnificent large canopy bushes
        drawCircle(color = Color(0xFF1B5E20), radius = 18.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.34f))
        drawCircle(color = Color(0xFF2E7D32), radius = 14.dp.toPx(), center = Offset(size.width * 0.34f, size.height * 0.38f))
        drawCircle(color = Color(0xFF2E7D32), radius = 14.dp.toPx(), center = Offset(size.width * 0.66f, size.height * 0.38f))
        drawCircle(color = Color(0xFF4CAF50), radius = 12.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.24f))
    }
}

@Composable
fun PhotoAnnotationsDialog(
    photo: PlantPhotoEntity,
    viewModel: PlantViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val annotations by remember(photo.id) {
        viewModel.getAnnotationsForPhoto(photo.id)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    var annotationInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("photo_annotations_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🏷️ Snapshot Milestones",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Add date-stamped annotations to this image",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, "close", modifier = Modifier.size(18.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (photo.imageUriOrBase64) {
                            "preset:seedling" -> SproutGrowthGraphic(modifier = Modifier.size(36.dp))
                            "preset:branching" -> BranchingGrowthGraphic(modifier = Modifier.size(36.dp))
                            "preset:budding" -> BloomGrowthGraphic(modifier = Modifier.size(36.dp))
                            "preset:mature" -> CanopyGrowthGraphic(modifier = Modifier.size(36.dp))
                            else -> SproutGrowthGraphic(modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            val originalDate = remember(photo.timestamp) {
                                SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(photo.timestamp))
                            }
                            Text("Snapshot of $originalDate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(if (photo.note.isEmpty()) "Healthy progress" else photo.note, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Text("Chronicle Milestones:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                
                if (annotations.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No milestones logged. Use quick tags or type below to mark significant events like pruning, repotting, etc.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 120.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        annotations.forEach { annotation ->
                            val timeStr = remember(annotation.timestamp) {
                                SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault()).format(Date(annotation.timestamp))
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = timeStr, fontSize = 8.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text(text = annotation.note, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                IconButton(
                                    onClick = { viewModel.deletePhotoAnnotation(annotation) },
                                    modifier = Modifier.size(24.dp).testTag("btn_delete_annotation_${annotation.id}")
                                ) {
                                    Icon(Icons.Default.Delete, "Delete annotation", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }

                Text("Quick Milestones:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickTags = listOf("🪴 Repotted", "✂️ Pruned", "🌸 Bloomed", "🧪 Fertilized")
                    quickTags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                                .clickable {
                                    viewModel.addPhotoAnnotation(photo.id, tag)
                                    Toast.makeText(context, "Added milestone: $tag", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 6.dp)
                                .testTag("chip_milestone_${tag.lowercase().replace(" ", "_").replace("🪴", "repotted").replace("✂️", "pruned").replace("🌸", "bloomed").replace("🧪", "fertilized")}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(tag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                OutlinedTextField(
                    value = annotationInput,
                    onValueChange = { annotationInput = it },
                    placeholder = { Text("Type custom milestone details...", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_milestone"),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                )

                Button(
                    onClick = {
                        if (annotationInput.trim().isNotEmpty()) {
                            viewModel.addPhotoAnnotation(photo.id, annotationInput.trim())
                            annotationInput = ""
                            Toast.makeText(context, "Milestone note added!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("btn_add_custom_milestone"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Add Custom Milestone", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
