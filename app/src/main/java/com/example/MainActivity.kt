package com.example

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.api.PlantCareResponse
import com.example.data.PlantEntity
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AnalysisState
import com.example.viewmodel.PlantViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: com.example.viewmodel.PlantViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val isDarkMode by viewModel.isDarkModeEnabled.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

// --- Custom Beautiful Vector Icons Drawn via Canvas (Guaranteed to compile & extremely sharp) ---

@Composable
fun CanvasLeafIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.1f)
            cubicTo(
                size.width * 0.85f, size.height * 0.15f,
                size.width * 0.9f, size.height * 0.65f,
                size.width * 0.5f, size.height * 0.9f
            )
            cubicTo(
                size.width * 0.1f, size.height * 0.65f,
                size.width * 0.15f, size.height * 0.15f,
                size.width * 0.5f, size.height * 0.1f
            )
        }
        drawPath(path = path, color = color)
        
        // Leaf Center Vein
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(size.width * 0.5f, size.height * 0.18f),
            end = Offset(size.width * 0.5f, size.height * 0.85f),
            strokeWidth = 3f * density
        )
        // Leaf Side Veins
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(size.width * 0.5f, size.height * 0.4f),
            end = Offset(size.width * 0.72f, size.height * 0.3f),
            strokeWidth = 2f * density
        )
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(size.width * 0.5f, size.height * 0.4f),
            end = Offset(size.width * 0.28f, size.height * 0.3f),
            strokeWidth = 2f * density
        )
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(size.width * 0.5f, size.height * 0.6f),
            end = Offset(size.width * 0.7f, size.height * 0.52f),
            strokeWidth = 2f * density
        )
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(size.width * 0.5f, size.height * 0.6f),
            end = Offset(size.width * 0.3f, size.height * 0.52f),
            strokeWidth = 2f * density
        )
    }
}

@Composable
fun CanvasWaterDropIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF29B6F6)) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.1f)
            cubicTo(
                size.width * 0.82f, size.height * 0.48f,
                size.width * 0.85f, size.height * 0.85f,
                size.width * 0.5f, size.height * 0.9f
            )
            cubicTo(
                size.width * 0.15f, size.height * 0.85f,
                size.width * 0.18f, size.height * 0.48f,
                size.width * 0.5f, size.height * 0.1f
            )
        }
        drawPath(path = path, color = color)
        
        // Highlight shine
        drawCircle(
            color = Color.White.copy(alpha = 0.35f),
            radius = size.width * 0.08f,
            center = Offset(size.width * 0.42f, size.height * 0.58f)
        )
    }
}

@Composable
fun CanvasSunIcon(modifier: Modifier = Modifier, color: Color = Color(0xFFFFB300)) {
    Canvas(modifier = modifier) {
        drawCircle(color = color, radius = size.minDimension * 0.22f, center = center)
        for (i in 0 until 8) {
            val angle = i * Math.PI / 4
            val startRadius = size.minDimension * 0.30f
            val endRadius = size.minDimension * 0.44f
            val startX = center.x + Math.cos(angle).toFloat() * startRadius
            val startY = center.y + Math.sin(angle).toFloat() * startRadius
            val endX = center.x + Math.cos(angle).toFloat() * endRadius
            val endY = center.y + Math.sin(angle).toFloat() * endRadius
            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.5f * density
            )
        }
    }
}

@Composable
fun CanvasMoonIcon(modifier: Modifier = Modifier, color: Color = Color(0xFFFFD54F)) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.7f, size.height * 0.15f)
            cubicTo(
                size.width * 0.2f, size.height * 0.2f,
                size.width * 0.2f, size.height * 0.8f,
                size.width * 0.7f, size.height * 0.85f
            )
            cubicTo(
                size.width * 0.35f, size.height * 0.75f,
                size.width * 0.35f, size.height * 0.25f,
                size.width * 0.7f, size.height * 0.15f
            )
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun CanvasSproutIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier) {
        // Soil base line
        drawLine(
            color = color.copy(alpha = 0.4f),
            start = Offset(size.width * 0.2f, size.height * 0.85f),
            end = Offset(size.width * 0.8f, size.height * 0.85f),
            strokeWidth = 3f * density
        )
        
        // Main stalk
        val stalkPath = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.85f)
            cubicTo(
                size.width * 0.5f, size.height * 0.55f,
                size.width * 0.45f, size.height * 0.35f,
                size.width * 0.35f, size.height * 0.3f
            )
        }
        drawPath(path = stalkPath, color = color, style = Stroke(width = 3.5f * density))

        // Left Leaf
        val leaf1Path = Path().apply {
            moveTo(size.width * 0.35f, size.height * 0.3f)
            cubicTo(
                size.width * 0.2f, size.height * 0.25f,
                size.width * 0.15f, size.height * 0.4f,
                size.width * 0.32f, size.height * 0.45f
            )
            cubicTo(
                size.width * 0.38f, size.height * 0.45f,
                size.width * 0.36f, size.height * 0.35f,
                size.width * 0.35f, size.height * 0.3f
            )
        }
        drawPath(path = leaf1Path, color = color)

        // Right Leaf sprouting up
        val leaf2Path = Path().apply {
            moveTo(size.width * 0.47f, size.height * 0.55f)
            cubicTo(
                size.width * 0.65f, size.height * 0.45f,
                size.width * 0.72f, size.height * 0.6f,
                size.width * 0.55f, size.height * 0.65f
            )
            cubicTo(
                size.width * 0.48f, size.height * 0.65f,
                size.width * 0.46f, size.height * 0.58f,
                size.width * 0.47f, size.height * 0.55f
            )
        }
        drawPath(path = leaf2Path, color = color)
    }
}

@Composable
fun CanvasFertilizerIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF4CAF50)) {
    Canvas(modifier = modifier) {
        // Draw a cute leaf/sprout with tiny feeding pellets/nutrition stars
        val path = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.9f)
            quadraticTo(size.width * 0.85f, size.height * 0.45f, size.width * 0.72f, size.height * 0.15f)
            quadraticTo(size.width * 0.45f, size.height * 0.3f, size.width * 0.5f, size.height * 0.9f)
        }
        drawPath(path = path, color = color)
        
        // Leaf stem line
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(size.width * 0.5f, size.height * 0.9f),
            end = Offset(size.width * 0.65f, size.height * 0.35f),
            strokeWidth = 2f
        )
        
        // Draw tiny fertilizer/nutrition sparkles
        drawCircle(
            color = Color(0xFFFFCA28), // Golden nutrition nugget 1
            radius = size.width * 0.08f,
            center = Offset(size.width * 0.25f, size.height * 0.35f)
        )
        drawCircle(
            color = Color(0xFF26A69A), // Teal nutrition nugget 2
            radius = size.width * 0.06f,
            center = Offset(size.width * 0.85f, size.height * 0.28f)
        )
        drawCircle(
            color = Color(0xFF8D6E63), // Organic soil pellet
            radius = size.width * 0.07f,
            center = Offset(size.width * 0.3f, size.height * 0.72f)
        )
    }
}

@Composable
fun CanvasCameraIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Draw camera body (rounded rect)
        val bodyPath = Path().apply {
            // Lens flash mount top (small nub)
            moveTo(width * 0.35f, height * 0.25f)
            lineTo(width * 0.4f, height * 0.15f)
            lineTo(width * 0.6f, height * 0.15f)
            lineTo(width * 0.65f, height * 0.25f)
            
            // Body outline
            lineTo(width * 0.9f, height * 0.25f)
            cubicTo(width * 0.95f, height * 0.25f, width * 0.95f, height * 0.3f, width * 0.95f, height * 0.35f)
            lineTo(width * 0.95f, height * 0.8f)
            cubicTo(width * 0.95f, height * 0.85f, width * 0.9f, height * 0.85f, width * 0.85f, height * 0.85f)
            lineTo(width * 0.15f, height * 0.85f)
            cubicTo(width * 0.05f, height * 0.85f, width * 0.05f, height * 0.8f, width * 0.05f, height * 0.75f)
            lineTo(width * 0.05f, height * 0.35f)
            cubicTo(width * 0.05f, height * 0.25f, width * 0.12f, height * 0.25f, width * 0.15f, height * 0.25f)
            close()
        }
        drawPath(path = bodyPath, color = color)
        
        // Draw lens (circle in center)
        drawCircle(
            color = Color.White,
            radius = width * 0.22f,
            center = Offset(width * 0.5f, height * 0.55f)
        )
        drawCircle(
            color = color,
            radius = width * 0.16f,
            center = Offset(width * 0.5f, height * 0.55f)
        )
        // Lens reflection dot
        drawCircle(
            color = Color.White,
            radius = width * 0.05f,
            center = Offset(width * 0.44f, height * 0.49f)
        )
    }
}

// --- Main Composable Layout ---

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: PlantViewModel = viewModel()
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Identify, 1: My Garden, 2: Care Manual
    var selectedPlantEntityForDetail by remember { mutableStateOf<PlantEntity?>(null) }
    var plantForWaterLogging by remember { mutableStateOf<PlantEntity?>(null) }
    var plantForFertilizerLogging by remember { mutableStateOf<PlantEntity?>(null) }
    val allPlants by viewModel.allPlants.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Custom Elegant Top Header ---
        val isDarkMode by viewModel.isDarkModeEnabled.collectAsStateWithLifecycle()
        ElevatedHeader(
            isDark = isDarkMode,
            onDarkModeToggle = { viewModel.toggleDarkMode() },
            onQuickGuideClick = { activeTab = 2 },
            onBackupClick = { viewModel.exportEntireCollectionToJSON(context) }
        )

        // --- Main Screen Switching Contents ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                0 -> IdentifyTabContent(
                    viewModel = viewModel,
                    onNavigateToGarden = { activeTab = 1 }
                )
                1 -> GardenTabContent(
                    allPlants = allPlants,
                    viewModel = viewModel,
                    onWaterClick = { plantId -> plantForWaterLogging = allPlants.find { it.id == plantId } },
                    onFertilizeClick = { plantId -> plantForFertilizerLogging = allPlants.find { it.id == plantId } },
                    onFavClick = { viewModel.toggleFavorite(it) },
                    onDeleteClick = { viewModel.deletePlant(it) },
                    onCardClick = { selectedPlantEntityForDetail = it },
                    onNavigateToScanner = { activeTab = 0 },
                    onSnoozeClick = { viewModel.snoozePlantWatering(it) },
                    onUpdateSchedule = { plant, isRemEnabled, hour, min, interval ->
                        viewModel.updatePlantSchedule(plant, isRemEnabled, hour, min, interval)
                    }
                )
                2 -> CareManualTabContent(viewModel = viewModel)
            }
        }

        // --- Bottom Navigation Menu ---
        BotanicalNavigationBar(
            activeTab = activeTab,
            onTabSelected = { activeTab = it }
        )
    }

    // --- Dynamic Care Sheet Dialog ---
    if (selectedPlantEntityForDetail != null) {
        PlantDetailDialog(
            plant = selectedPlantEntityForDetail!!,
            onDismiss = { selectedPlantEntityForDetail = null },
            onWaterNow = { id ->
                plantForWaterLogging = allPlants.find { it.id == id }
            },
            onFertilizeNow = { id ->
                plantForFertilizerLogging = allPlants.find { it.id == id }
            },
            viewModel = viewModel
        )
    }

    // --- Customizable Water Logging Dialog ---
    if (plantForWaterLogging != null) {
        LogWateringDialog(
            plant = plantForWaterLogging!!,
            onDismiss = { plantForWaterLogging = null },
            onConfirmWatering = { id, timestamp ->
                viewModel.waterPlant(id, timestamp)
                val formattedTime = try {
                    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                    sdf.format(Date(timestamp))
                } catch (e: Exception) {
                    "custom date"
                }
                Toast.makeText(context, "${plantForWaterLogging?.commonName} watering logged for $formattedTime!", Toast.LENGTH_SHORT).show()
                plantForWaterLogging = null
                selectedPlantEntityForDetail = null // Dismiss details to sync UI
            }
        )
    }

    // --- Customizable Fertilization Logging Dialog ---
    if (plantForFertilizerLogging != null) {
        LogFertilizingDialog(
            plant = plantForFertilizerLogging!!,
            onDismiss = { plantForFertilizerLogging = null },
            onConfirmFertilizing = { id, timestamp, interval ->
                viewModel.fertilizePlant(id, timestamp)
                if (interval > 0) {
                    viewModel.updatePlantFertilizationInterval(id, interval)
                }
                val formattedTime = try {
                    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                    sdf.format(Date(timestamp))
                } catch (e: Exception) {
                    "custom date"
                }
                val intervalMsg = if (interval > 0) " (frequency: $interval days)" else ""
                Toast.makeText(context, "${plantForFertilizerLogging?.commonName} fertilization logged for $formattedTime$intervalMsg!", Toast.LENGTH_SHORT).show()
                plantForFertilizerLogging = null
                selectedPlantEntityForDetail = null // Dismiss details to sync UI
            }
        )
    }
}

// --- Custom Top Header ---
@Composable
fun ElevatedHeader(
    isDark: Boolean,
    onDarkModeToggle: () -> Unit,
    onQuickGuideClick: () -> Unit,
    onBackupClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CanvasLeafIcon(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            }
            Column {
                Text(
                    text = "Gardening Assistant",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                )
                Text(
                    text = "FloraScan Engine",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onBackupClick,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .size(44.dp)
                    .testTag("btn_global_backup_json")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Export Backup (JSON)",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onDarkModeToggle,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .size(44.dp)
                    .testTag("btn_dark_mode_toggle")
            ) {
                if (isDark) {
                    CanvasSunIcon(modifier = Modifier.size(20.dp), color = Color(0xFFFFB300))
                } else {
                    CanvasMoonIcon(modifier = Modifier.size(20.dp), color = Color(0xFF673AB7))
                }
            }

            IconButton(
                onClick = onQuickGuideClick,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Manual Guide Info",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// --- Custom Botanical Bottom Bar ---
@Composable
fun BotanicalNavigationBar(
    activeTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column {
        // Precise 1dp top border representing border-t border-[#DDE6D3]
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.tertiary
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height(82.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Tab 0: Identify
                BotanicalTabItem(
                    isActive = activeTab == 0,
                    label = "Identify",
                    icon = { CanvasSproutIcon(modifier = Modifier.size(24.dp), color = it) },
                    onClick = { onTabSelected(0) },
                    testTag = "tab_identify"
                )

                // Tab 1: Garden
                BotanicalTabItem(
                    isActive = activeTab == 1,
                    label = "My Garden",
                    icon = { CanvasLeafIcon(modifier = Modifier.size(24.dp), color = it) },
                    onClick = { onTabSelected(1) },
                    testTag = "tab_garden"
                )

                // Tab 2: Care Manual
                BotanicalTabItem(
                    isActive = activeTab == 2,
                    label = "Care Manual",
                    icon = { CanvasSunIcon(modifier = Modifier.size(24.dp), color = it) },
                    onClick = { onTabSelected(2) },
                    testTag = "tab_manual"
                )
            }
        }
    }
}

@Composable
fun RowScope.BotanicalTabItem(
    isActive: Boolean,
    label: String,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    val color = if (isActive) activeColor else inactiveColor

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isActive) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                icon(color)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
                color = color
            )
        }
    }
}

// ==================== TAB 0: IDENTIFY SCREEN ====================

@Composable
fun IdentifyTabContent(
    viewModel: PlantViewModel,
    onNavigateToGarden: () -> Unit
) {
    val context = LocalContext.current
    val analysisState by viewModel.analysisState.collectAsStateWithLifecycle()

    // Helper Decoders
    fun Uri.toBitmap(appComp: Application): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(appComp.contentResolver, this)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(appComp.contentResolver, this)
            }
        } catch (e: Exception) {
            Log.e("IdentifyTab", "Uri to Bitmap conversion failing", e)
            null
        }
    }

    // Interactive Photo Picker Activity launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = uri.toBitmap(context.applicationContext as Application)
            if (bitmap != null) {
                viewModel.analyzePlant(bitmap)
            } else {
                Toast.makeText(context, "Failed to load selected picture.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Snapshot Launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.analyzePlant(bitmap)
        } else {
            Toast.makeText(context, "No photo captured.", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera Runtime Permission Request Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                takePictureLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(context, "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Camera permission is required to capture photos of plants.", Toast.LENGTH_LONG).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Main Scanner Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CanvasSproutIcon(
                            modifier = Modifier.size(40.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Identify Any Plant with AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Take or choose a photo of leaves, foliage, or flowers, and let our botanical scouts construct a comprehensive watering and care blueprint.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    // Trigger buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_select_gallery"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add image", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPermission) {
                                    try {
                                        takePictureLauncher.launch(null)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_capture_camera"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            CanvasCameraIcon(
                                modifier = Modifier.size(18.dp),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Use Camera", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // State Machine Renderers
        item {
            AnimatedContent(
                targetState = analysisState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "ScanningTransition"
            ) { state ->
                when (state) {
                    is AnalysisState.Idle -> {
                        // Quick-test cards: "No photo yet? Tap on helper plants to simulate!"
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Or Test Instantly Offline",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val simulatedPlants = listOf(
                                    Triple("Swiss Cheese Plant", "Monstera Deliciosa • Araceae", 0),
                                    Triple("Snake Plant", "Sansevieria Trifasciata • Asparagaceae", 1),
                                    Triple("Fiddle-Leaf Fig", "Ficus Lyrata • Moraceae", 2),
                                    Triple("Aloe Vera", "Aloe Barbadensis • Asphodelaceae", 3)
                                )
                                simulatedPlants.forEach { (name, subtitle, index) ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(1.dp, RoundedCornerShape(12.dp))
                                            .clickable { viewModel.loadMockPlant(index) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .background(
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CanvasLeafIcon(
                                                    modifier = Modifier.size(24.dp),
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(14.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = name,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = subtitle,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Test",
                                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is AnalysisState.Analyzing -> {
                        FoliageLoadingIndicator()
                    }
                    is AnalysisState.Success -> {
                        SuccessfullyAnalyzedCard(
                            plantResult = state.plant,
                            customBitmap = state.bitmap,
                            onSave = { isReminderEnabled, reminderHour, customInterval ->
                                viewModel.saveAnalyzedPlant(
                                    plant = state.plant,
                                    bitmap = state.bitmap,
                                    isReminderEnabled = isReminderEnabled,
                                    reminderHour = reminderHour,
                                    reminderMinute = 0,
                                    customInterval = customInterval
                                )
                                Toast.makeText(context, "${state.plant.commonName} saved with reminders enabled!", Toast.LENGTH_SHORT).show()
                                onNavigateToGarden()
                            },
                            onDiscard = { viewModel.resetAnalysis() }
                        )
                    }
                    is AnalysisState.Error -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning error",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Analysis Limit or Configuration Issue",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = state.message,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f),
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Tip: You can use our instant simulated plants listed above. To connect custom scans, please ensure a valid GEMINI_API_KEY is configured in AI Studio's Secrets panel.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.75f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { viewModel.resetAnalysis() }) {
                                        Text("Acknowledge", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Success Analysis Card ---
@Composable
fun SuccessfullyAnalyzedCard(
    plantResult: PlantCareResponse,
    customBitmap: Bitmap?,
    onSave: (isReminderEnabled: Boolean, reminderHour: Int, customInterval: Int) -> Unit,
    onDiscard: () -> Unit
) {
    var isReminderEnabled by remember { mutableStateOf(true) }
    var customIntervalDays by remember { mutableStateOf(plantResult.wateringIntervalDays) }
    var reminderHour by remember { mutableStateOf(9) } // Default 9 AM

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(24.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Image Box with exact 4:3 Aspect Ratio, thick 4.dp white border, and shadow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .padding(12.dp)
                    .shadow(3.dp, RoundedCornerShape(24.dp))
                    .border(BorderStroke(4.dp, Color.White), RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (customBitmap != null) {
                    Image(
                        bitmap = customBitmap.asImageBitmap(),
                        contentDescription = "Scanned Plant",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CanvasSproutIcon(modifier = Modifier.size(64.dp), color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Matching Botanical Silhouette",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Discard Close Button on Top Right
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = onDiscard,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Discard result", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Identified Title & Scientific Classification
                Text(
                    text = plantResult.commonName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "${plantResult.scientificName} • ${plantResult.family}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Description
                Text(
                    text = plantResult.description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(14.dp))

                // Key Vital Details Grid (Matching beautiful centered layout)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val iconColor = MaterialTheme.colorScheme.primary
                    VitalElementBox(
                        modifier = Modifier.weight(1f),
                        title = "WATER",
                        value = "Every ${plantResult.wateringIntervalDays} days",
                        icon = { CanvasWaterDropIcon(modifier = Modifier.size(22.dp)) }
                    )
                    VitalElementBox(
                        modifier = Modifier.weight(1f),
                        title = "LIGHT",
                        value = plantResult.sunlightRequirements,
                        icon = { CanvasSunIcon(modifier = Modifier.size(22.dp), color = iconColor) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VitalElementBox(
                        modifier = Modifier.weight(1f),
                        title = "TEMP",
                        value = plantResult.optimalTemperature,
                        icon = { Icon(Icons.Default.Refresh, contentDescription = "temp", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                    )
                    VitalElementBox(
                        modifier = Modifier.weight(1f),
                        title = "SAFETY",
                        value = plantResult.toxicity,
                        icon = { Icon(Icons.Default.Warning, contentDescription = "safety", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Care instructions highlighted box styled like the Tailwind bg-[#E1EAD7] rounded-3xl p-5
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Soil Preference",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = plantResult.soilPreference,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Botanical Quick Tips list
                        Text(
                            text = "Care Instructions",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            plantResult.quickTips.forEach { tip ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(text = "•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
                                    Text(text = tip, fontSize = 12.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                // Reminders and schedule setups card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Schedule Reminders",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Receive notifications for care tasks",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            Switch(
                                checked = isReminderEnabled,
                                onCheckedChange = { isReminderEnabled = it },
                                modifier = Modifier.testTag("switch_reminder_enabled")
                            )
                        }
                        
                        if (isReminderEnabled) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // Customize Watering Frequency Slider
                            Text(
                                text = "Watering Frequency: Every $customIntervalDays days",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Recommended cycle based on species care instructions inside.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Slider(
                                value = customIntervalDays.toFloat(),
                                onValueChange = { customIntervalDays = it.toInt() },
                                valueRange = 1f..31f,
                                steps = 30,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("slider_interval_days")
                            )
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // Preferred notification hour quick selector
                            Text(
                                text = "Preferred Notification Time",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val times = listOf(
                                    9 to "Morning (9 AM)",
                                    14 to "Afternoon (2 PM)",
                                    18 to "Evening (6 PM)"
                                )
                                times.forEach { (hour, label) ->
                                    val isSelected = reminderHour == hour
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { reminderHour = hour }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save Action with dynamic parameters
                Button(
                    onClick = { onSave(isReminderEnabled, reminderHour, customIntervalDays) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_garden"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save check", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Collection & Set Reminder", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VitalElementBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(0.5.dp, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Text(
                text = title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==================== TAB 1: MY GARDEN SCREEN ====================

@Composable
fun GardenTabContent(
    allPlants: List<PlantEntity>,
    viewModel: com.example.viewmodel.PlantViewModel,
    onWaterClick: (Long) -> Unit,
    onFertilizeClick: (Long) -> Unit,
    onFavClick: (PlantEntity) -> Unit,
    onDeleteClick: (PlantEntity) -> Unit,
    onCardClick: (PlantEntity) -> Unit,
    onNavigateToScanner: () -> Unit,
    onSnoozeClick: (PlantEntity) -> Unit,
    onUpdateSchedule: (PlantEntity, Boolean, Int, Int, Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // --- LIVE GARDENING WEATHER METRIC CARD ---
        LiveWeatherCard(viewModel = viewModel)
        Spacer(modifier = Modifier.height(12.dp))

        if (allPlants.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CanvasLeafIcon(modifier = Modifier.size(54.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Your Green Sanctuary is Empty",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Capture or simulate a plant inside the Identify page to start organizing watering triggers and keeping tabs on leaf diagnostics.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNavigateToScanner,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Scan", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Identify Plant Now", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            var viewMode by remember { mutableStateOf(0) } // 0: Grid, 1: Schedule Reminders, 2: Wishlist
            var selectedFilterTag by remember { mutableStateOf("All") }
            var isBulkMode by remember { mutableStateOf(false) }
            val selectedPlantsIds = remember { mutableStateListOf<Long>() }

            val activePlants = remember(allPlants) { allPlants.filter { !it.isWishlist } }
            val wishlistPlants = remember(allPlants) { allPlants.filter { it.isWishlist } }

            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Sleek sub-selector bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (viewMode == 0) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { viewMode = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "My Foliage Gallery",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (viewMode == 0) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (viewMode == 1) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { viewMode = 1 }
                        .padding(vertical = 10.dp)
                        .testTag("tab_watering_schedule"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Watering Schedule",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (viewMode == 1) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (viewMode == 2) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { viewMode = 2 }
                        .padding(vertical = 10.dp)
                        .testTag("tab_wishlist_trigger"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "My Wishlist",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (viewMode == 2) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Centralized dynamic care notification indicators
            val overduePlants = remember(activePlants) {
                activePlants.filter { plant ->
                    val interval = if (plant.customWateringIntervalDays > 0) plant.customWateringIntervalDays else plant.wateringIntervalDays
                    val daysSinceWatered = (System.currentTimeMillis() - plant.lastWateredTime) / (1000 * 60 * 60 * 24)
                    (interval - daysSinceWatered) <= 0
                }
            }

            if (overduePlants.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .testTag("hydration_alert_banner"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Care notifications",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CARE NOTIFICATION: WATER DUE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (overduePlants.size == 1) {
                                    "${overduePlants[0].commonName} is extremely dry and needs watering!"
                                } else {
                                    "${overduePlants.size} of your beautiful plants need watering now!"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            if (viewMode == 0) {
                // Compute distinct tags in collection
                val allDistinctTags = remember(activePlants) {
                    activePlants.flatMap { plant ->
                        if (plant.tags.isBlank()) emptyList()
                        else plant.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    }.distinct().sorted()
                }

                val filteredPlants = remember(activePlants, selectedFilterTag) {
                    if (selectedFilterTag == "All") {
                        activePlants
                    } else {
                        activePlants.filter { plant ->
                            plant.tags.split(",").map { it.trim().lowercase() }.contains(selectedFilterTag.lowercase())
                        }
                    }
                }

                // --- CATEGORY TAGS FILTER ROW ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isAllSelected = selectedFilterTag == "All"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedFilterTag = "All" }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("tag_filter_all"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "All (${activePlants.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    allDistinctTags.forEach { tag ->
                        val isSelected = selectedFilterTag.equals(tag, ignoreCase = true)
                        val count = activePlants.count { p -> p.tags.split(",").map { it.trim().lowercase() }.contains(tag.lowercase()) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedFilterTag = tag }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("tag_filter_${tag.lowercase().replace(" ", "_")}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$tag ($count)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Section Header with Multi-Select toggles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedFilterTag == "All") "Garden Folios (${filteredPlants.size})" else "Category: $selectedFilterTag (${filteredPlants.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Button to toggle Bulk Selection Mode
                    Button(
                        onClick = {
                            isBulkMode = !isBulkMode
                            selectedPlantsIds.clear() // Clear selections when toggled
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBulkMode) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = if (isBulkMode) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_toggle_bulk_mode")
                    ) {
                        Icon(
                            imageVector = if (isBulkMode) Icons.Default.Close else Icons.Default.Build,
                            contentDescription = if (isBulkMode) "Cancel Bulk Mode" else "Bulk Mode",
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBulkMode) "Cancel Select" else "Bulk Action",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isBulkMode) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("bulk_actions_panel"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚡ Bulk Actions: ${selectedPlantsIds.size} selected",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Select All button
                                    TextButton(
                                        onClick = {
                                            selectedPlantsIds.clear()
                                            selectedPlantsIds.addAll(filteredPlants.map { it.id })
                                        },
                                        modifier = Modifier.height(28.dp).testTag("btn_bulk_select_all"),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Select All", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    // Clear selection
                                    TextButton(
                                        onClick = { selectedPlantsIds.clear() },
                                        modifier = Modifier.height(28.dp).testTag("btn_bulk_deselect_all"),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Clear", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Bulk Water Button
                                Button(
                                    onClick = {
                                        if (selectedPlantsIds.isEmpty()) {
                                            Toast.makeText(context, "Select at least one plant to water!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.bulkWaterPlants(selectedPlantsIds.toList())
                                            Toast.makeText(context, "Marked ${selectedPlantsIds.size} plants as watered!", Toast.LENGTH_SHORT).show()
                                            isBulkMode = false
                                            selectedPlantsIds.clear()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .testTag("btn_bulk_water_submit"),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    CanvasWaterDropIcon(modifier = Modifier.size(12.dp), color = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Bulk Water", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                // Bulk Fertilize Button
                                Button(
                                    onClick = {
                                        if (selectedPlantsIds.isEmpty()) {
                                            Toast.makeText(context, "Select at least one plant to fertilise!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.bulkFertilizePlants(selectedPlantsIds.toList())
                                            Toast.makeText(context, "Marked ${selectedPlantsIds.size} plants as fertilised!", Toast.LENGTH_SHORT).show()
                                            isBulkMode = false
                                            selectedPlantsIds.clear()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .testTag("btn_bulk_fertilize_submit"),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    CanvasFertilizerIcon(modifier = Modifier.size(12.dp), color = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Bulk Feed", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPlants) { plant ->
                        val isSelected = selectedPlantsIds.contains(plant.id)
                        GardenPlantGridItem(
                            plant = plant,
                            onWaterClick = { onWaterClick(plant.id) },
                            onFertilizeClick = { onFertilizeClick(plant.id) },
                            onFavClick = { onFavClick(plant) },
                            onDeleteClick = { onDeleteClick(plant) },
                            onClick = { onCardClick(plant) },
                            isBulkMode = isBulkMode,
                            isSelected = isSelected,
                            onSelectToggle = {
                                if (isSelected) {
                                    selectedPlantsIds.remove(plant.id)
                                } else {
                                    selectedPlantsIds.add(plant.id)
                                }
                            }
                        )
                    }
                }
            } else if (viewMode == 1) {
                WateringScheduleTimeline(
                    allPlants = activePlants,
                    onWaterClick = onWaterClick,
                    onSnoozeClick = onSnoozeClick,
                    onUpdateSchedule = onUpdateSchedule
                )
            } else {
                MyWishlistScreen(
                    wishlistPlants = wishlistPlants,
                    onCardClick = onCardClick,
                    onPromote = { viewModel.promoteWishlistToGarden(it) },
                    onDelete = { viewModel.deletePlant(it) }
                )
            }
        }
    }
    }
}

@Composable
fun GardenPlantGridItem(
    plant: PlantEntity,
    onWaterClick: () -> Unit,
    onFertilizeClick: () -> Unit,
    onFavClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
    isBulkMode: Boolean = false,
    isSelected: Boolean = false,
    onSelectToggle: () -> Unit = {}
) {
    // Decode stored photo if present
    val decodedBitmap = remember(plant.customImageUri) {
        if (!plant.customImageUri.isNullOrEmpty()) {
            try {
                val decodedBytes = android.util.Base64.decode(plant.customImageUri, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    // Dynamic hydration calculations
    val interval = if (plant.customWateringIntervalDays > 0) plant.customWateringIntervalDays else plant.wateringIntervalDays
    val millisecondsSinceWatered = System.currentTimeMillis() - plant.lastWateredTime
    val daysSinceWatered = millisecondsSinceWatered / (1000 * 60 * 60 * 24)
    val daysRemaining = (interval - daysSinceWatered)
    val percentLeft = (1f - (daysSinceWatered.toFloat() / interval.toFloat())).coerceIn(0f, 1f)

    // Dynamic color coding for watering
    val progressColor = when {
        percentLeft > 0.5f -> MaterialTheme.colorScheme.primary
        percentLeft > 0.2f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    // Dynamic fertilization calculations
    val fertInterval = if (plant.customFertilizingIntervalDays > 0) plant.customFertilizingIntervalDays else plant.fertilizingIntervalDays
    val millisecondsSinceFertilized = System.currentTimeMillis() - plant.lastFertilizedTime
    val daysSinceFertilized = millisecondsSinceFertilized / (1000 * 60 * 60 * 24)
    val fertDaysRemaining = (fertInterval - daysSinceFertilized)
    val fertPercentLeft = (1f - (daysSinceFertilized.toFloat() / fertInterval.toFloat())).coerceIn(0f, 1f)

    // Dynamic color coding for fertilizing
    val fertProgressColor = when {
        fertPercentLeft > 0.5f -> Color(0xFF4CAF50) // Healthy green
        fertPercentLeft > 0.2f -> Color(0xFFFF9800) // Warning orange
        else -> Color(0xFFF44336) // Overdue red
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isSelected) 3.dp else 0.5.dp, RoundedCornerShape(16.dp))
            .border(
                BorderStroke(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                ),
                RoundedCornerShape(16.dp)
            )
            .clickable {
                if (isBulkMode) {
                    onSelectToggle()
                } else {
                    onClick()
                }
            }
            .testTag("garden_plant_${plant.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thumbnail container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                if (decodedBitmap != null) {
                    Image(
                        bitmap = decodedBitmap.asImageBitmap(),
                        contentDescription = plant.commonName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        CanvasLeafIcon(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
                    }
                }

                // NEED WATER dynamic notification indicator overlay
                if (daysRemaining <= 0L) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("water_needed_badge_${plant.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Water alert",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "DRY ALERT",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Heart & trash overlay actions OR selection checkbox
                Box(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    if (isBulkMode) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .border(1.5.dp, Color.White, CircleShape)
                                .testTag("bulk_checkbox_${plant.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            IconButton(
                                onClick = onDeleteClick,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                                    .size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                            IconButton(
                                onClick = onFavClick,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (plant.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Fav",
                                    tint = if (plant.isFavorite) MaterialTheme.colorScheme.primary else Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = plant.commonName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = plant.scientificName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Hydration progress bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dueText = when {
                            daysRemaining < 0L -> "Overdue by ${-daysRemaining}d!"
                            daysRemaining == 0L -> "Water Due Today!"
                            daysRemaining == 1L -> "Due tomorrow"
                            else -> "Due in ${daysRemaining}d"
                        }
                        Text(
                            text = dueText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = progressColor
                        )
                        Text(
                            text = "${(percentLeft * 100).toInt()}%",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { percentLeft },
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Fertilization progress bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val fertDueText = when {
                            fertDaysRemaining < 0L -> "Feed Overdue by ${-fertDaysRemaining}d!"
                            fertDaysRemaining == 0L -> "Feed Due Today!"
                            fertDaysRemaining == 1L -> "Feed tomorrow"
                            else -> "Feed in ${fertDaysRemaining}d"
                        }
                        Text(
                            text = fertDueText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = fertProgressColor
                        )
                        Text(
                            text = "${(fertPercentLeft * 100).toInt()}%",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { fertPercentLeft },
                        color = fertProgressColor,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape)
                    )
                }

                if (!isBulkMode) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Log Water & Feed side-by-side split action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = onWaterClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .testTag("btn_log_water_${plant.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CanvasWaterDropIcon(modifier = Modifier.size(10.dp), color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Water", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        IconButton(
                            onClick = onFertilizeClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .testTag("btn_log_fertilize_${plant.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CanvasFertilizerIcon(modifier = Modifier.size(10.dp), color = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Feed", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 2: DETAILED CARE MANUAL ====================

@Composable
fun CareManualTabContent(viewModel: com.example.viewmodel.PlantViewModel) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("All") }
    var selectedPlant by remember { mutableStateOf<PlantCareResponse?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var activeSubTab by remember { mutableStateOf(0) } // 0: Encyclopedia, 1: AI Troubleshooter
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle(initialValue = emptyList())
    var filterSunlight by remember { mutableStateOf("All") }
    var filterLocation by remember { mutableStateOf("All") }
    var filterToxicity by remember { mutableStateOf("All") }
    var showAdvancedFilters by remember { mutableStateOf(false) }
    var nurseryQuery by remember { mutableStateOf("") }

    val commonPlantsDatabase = remember {
        listOf(
            PlantCareResponse(
                commonName = "Swiss Cheese Plant",
                scientificName = "Monstera Deliciosa",
                family = "Araceae",
                description = "Iconic indoor climber famous for its massive, perforated tropical split leaves.",
                wateringIntervalDays = 7,
                wateringInstructions = "Water thoroughly when the top 2-3 inches of soil feel dry. Typically once every 7-9 days. Avoid soggy soil to prevent root rot.",
                sunlightRequirements = "Bright Indirect Sunlight",
                optimalTemperature = "18°C - 30°C (64°F - 86°F)",
                toxicity = "Toxic to Cats and Dogs (calcium oxalate crystals)",
                soilPreference = "Organically rich, well-aerated soil mix with peat-moss and perlite",
                quickTips = listOf(
                    "Wipe the massive leaves weekly with a damp cloth to promote photosynthesis.",
                    "Provides excellent vertical styling as it grows; support with a sturdy moss pole.",
                    "Misting surrounding air or using a humidifier keeps leaf tips crisp-free."
                )
            ),
            PlantCareResponse(
                commonName = "Snake Plant",
                scientificName = "Sansevieria Trifasciata",
                family = "Asparagaceae",
                description = "Robust succulent with upright sword-like variegated leaves. Practically indestructible.",
                wateringIntervalDays = 21,
                wateringInstructions = "Allow soil to dry out completely between waterings. Under-watering is always safer than over-watering.",
                sunlightRequirements = "Adaptable (Low Light to Direct Sun)",
                optimalTemperature = "15°C - 27°C (59°F - 80°F)",
                toxicity = "Mildly Toxic to Pets (saponins which make them spit)",
                soilPreference = "Sandy, extremely sharp well-draining cactus/succulent potting mix",
                quickTips = listOf(
                    "Perfect beginner plant, it purifies indoor air and converts CO2 to oxygen at night.",
                    "Keep in tight, well-fitted terracotta pots to prevent soil from water-logging.",
                    "Do not pour water directly into the center leaf rosette, water around the outer rim."
                )
            ),
            PlantCareResponse(
                commonName = "Fiddle-Leaf Fig",
                scientificName = "Ficus Lyrata",
                family = "Moraceae",
                description = "Stately evergreen indoor tree with dramatic, heavily veined violin-shaped leaves.",
                wateringIntervalDays = 10,
                wateringInstructions = "Water when the top 2 inches of soil are dry. Pour water slowly until it drains out of the bottom. Consistent watering is crucial.",
                sunlightRequirements = "Consistent Bright Indirect Light",
                optimalTemperature = "18°C - 24°C (64°F - 75°F)",
                toxicity = "Toxic to pets, sap can irritate skin and cause mouth swelling",
                soilPreference = "Well-draining, highly porous premium peat-based option",
                quickTips = listOf(
                    "Dislikes being moved! Find a bright spot with zero drafts and keep it there.",
                    "Rotate the pot 90 degrees every month to promote uniform vertical foliage.",
                    "If leaves begin drying or turning dark brown, check for excess moisture or drafts."
                )
            ),
            PlantCareResponse(
                commonName = "Aloe Vera",
                scientificName = "Aloe Barbadensis Miller",
                family = "Asphodelaceae",
                description = "Gorgeous succulent with thick fleshy leaves filled with soothing therapeutic gel.",
                wateringIntervalDays = 14,
                wateringInstructions = "Water deeply, but very sparingly. Let soil compile and dry out entirely. Reduce watering down to 4-week sequences in freezing winters.",
                sunlightRequirements = "Full Bright Direct Sunlight",
                optimalTemperature = "15°C - 28°C (59°F - 82°F)",
                toxicity = "Mildly toxic to pets due to saponins; therapeutic for skin burns",
                soilPreference = "Highly porous succulent/cactus gritty soil mix",
                quickTips = listOf(
                    "Requires excellent drainage; never leave sitting in standing water.",
                    "Sparsely harvest mature outer leaves to extract therapeutic aloe gel.",
                    "Can be placed on warm, south-facing windowsills for maximum solar health."
                )
            ),
            PlantCareResponse(
                commonName = "Peace Lily",
                scientificName = "Spathiphyllum Wallisii",
                family = "Araceae",
                description = "Elegant dark green leafy foliage with beautiful white hooded spade flowers. Famous for signaling thirst clearly.",
                wateringIntervalDays = 5,
                wateringInstructions = "Keep soil consistently moist but never soggy. Will droop dramatically to show it's thirsty, rebounding hours after watering.",
                sunlightRequirements = "Medium to Low Indirect Light",
                optimalTemperature = "18°C - 26°C (64°F - 79°F)",
                toxicity = "Toxic to cats and dogs (insoluble calcium oxalates)",
                soilPreference = "Moisture-retaining yet well-aerated potting mix high in compost",
                quickTips = listOf(
                    "Dislikes chlorine; let tap water sit overnight before watering or use distilled water.",
                    "Foliage thrives in high humidity; great choice for bathrooms or humid kitchens.",
                    "Wipe leaves to prevent dust; keep away from cold AC vents or heaters."
                )
            ),
            PlantCareResponse(
                commonName = "Golden Pothos",
                scientificName = "Epipremnum Aureum",
                family = "Araceae",
                description = "Hanging trail vine featuring heart-shaped leaves with beautiful yellow and green variegation. Extremely hardy.",
                wateringIntervalDays = 8,
                wateringInstructions = "Water once the top half of soil displays total dryness. Foliage will droop slightly when thirsty.",
                sunlightRequirements = "Bright to Low Indirect Light",
                optimalTemperature = "15°C - 29°C (59°F - 84°F)",
                toxicity = "Toxic to dogs and cats (oxalate crystals)",
                soilPreference = "Standard indoor potting soil with good drainage",
                quickTips = listOf(
                    "Great for hanging baskets, high shelves, or trained to climb walls.",
                    "Highly tolerant of occasional under-watering or neglect.",
                    "Take vine clippings and place in water to propagate new baby roots easily."
                )
            ),
            PlantCareResponse(
                commonName = "Spider Plant",
                scientificName = "Chlorophytum Comosum",
                family = "Asparagaceae",
                description = "Fountain-like clumps of slender variegated green-and-white striped leaves that produce hanging baby spiderettes.",
                wateringIntervalDays = 6,
                wateringInstructions = "Water thoroughly when the top inch of soil feels dry. Appreciates consistent hydration in active spring/summer months.",
                sunlightRequirements = "Adaptable Bright Indirect Light",
                optimalTemperature = "13°C - 24°C (55°F - 75°F)",
                toxicity = "Completely Safe & Non-Toxic to Cats and Dogs!",
                soilPreference = "Loose, loamy, perfectly-aerated general potting mix",
                quickTips = listOf(
                    "Fantastic for homes with pets as it is entirely non-toxic.",
                    "Will grow small white blossoms followed by miniature runner plantlets.",
                    "Snip healthy plantlets to root in fresh soil and expand your collection."
                )
            ),
            PlantCareResponse(
                commonName = "Boston Fern",
                scientificName = "Nephrolepis Exaltata",
                family = "Nephrolepidaceae",
                description = "Lush, feather-like arching fronds that bring a soft, abundant cloud of green indoors.",
                wateringIntervalDays = 4,
                wateringInstructions = "Keep soil moist at all times. Do not let soil dry out. Water when the top surface begins to feel dry to touch.",
                sunlightRequirements = "Bright Filtered or Medium Indirect Light",
                optimalTemperature = "16°C - 24°C (60°F - 75°F)",
                toxicity = "Completely Safe & Non-Toxic to Cats and Dogs!",
                soilPreference = "Humus-rich, moisture-retaining peaty compost mix",
                quickTips = listOf(
                    "Extremely high humidity requirements; mist daily or use a humidity tray.",
                    "Shedding fronds are natural, but crispy browning indicates air is too dry.",
                    "Thrives in bathrooms where shower steam provides a natural sauna."
                )
            )
        )
    }

    val filteredPlantsByQuery = remember(searchQuery, activeFilter, filterSunlight, filterLocation, filterToxicity) {
        commonPlantsDatabase.filter { plant ->
            val matchesQuery = searchQuery.isEmpty() ||
                    plant.commonName.contains(searchQuery, ignoreCase = true) ||
                    plant.scientificName.contains(searchQuery, ignoreCase = true) ||
                    plant.family.contains(searchQuery, ignoreCase = true)
            
            val matchesFilter = when (activeFilter) {
                "All" -> true
                "Beginner Friendly" -> plant.wateringIntervalDays >= 10 || plant.commonName in listOf("Snake Plant", "Aloe Vera", "Golden Pothos", "Spider Plant")
                "Low Light" -> plant.sunlightRequirements.contains("Low", ignoreCase = true) || plant.sunlightRequirements.contains("Adaptable", ignoreCase = true)
                "Safe for Pets" -> plant.toxicity.contains("Safe", ignoreCase = true) || plant.toxicity.contains("Non-Toxic", ignoreCase = true)
                else -> true
            }

            val matchesAdvancedSunlight = when (filterSunlight) {
                "All" -> true
                "Full Sun" -> plant.sunlightRequirements.contains("Full", ignoreCase = true) || plant.sunlightRequirements.contains("Direct", ignoreCase = true)
                "Bright Indirect" -> plant.sunlightRequirements.contains("Indirect", ignoreCase = true) || plant.sunlightRequirements.contains("Bright", ignoreCase = true)
                "Low/Medium" -> plant.sunlightRequirements.contains("Low", ignoreCase = true) || plant.sunlightRequirements.contains("Medium", ignoreCase = true) || plant.sunlightRequirements.contains("Adaptable", ignoreCase = true)
                else -> true
            }

            val matchesAdvancedLocation = when (filterLocation) {
                "All" -> true
                "Indoor Only" -> plant.description.contains("indoor", ignoreCase = true) || plant.commonName in listOf("Swiss Cheese Plant", "Snake Plant", "Fiddle-Leaf Fig", "Peace Lily", "Golden Pothos", "Spider Plant", "Boston Fern")
                "Outdoor/Adaptable" -> plant.commonName in listOf("Aloe Vera", "Boston Fern", "Snake Plant") || !plant.description.lowercase().contains("only")
                else -> true
            }

            val matchesAdvancedToxicity = when (filterToxicity) {
                "All" -> true
                "Pet-Safe" -> plant.toxicity.contains("Safe", ignoreCase = true) || plant.toxicity.contains("Non-Toxic", ignoreCase = true)
                "Toxic Warning" -> !plant.toxicity.contains("Safe", ignoreCase = true) && !plant.toxicity.contains("Non-Toxic", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter && matchesAdvancedSunlight && matchesAdvancedLocation && matchesAdvancedToxicity
        }
    }

    if (selectedPlant == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant Top Segmented Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == 0) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { activeSubTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CanvasSproutIcon(modifier = Modifier.size(13.dp), color = if (activeSubTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Manuals",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeSubTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == 1) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { activeSubTab = 1 }
                        .padding(vertical = 10.dp)
                        .testTag("tab_troubleshoot_trigger"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Troubleshoot icon",
                            tint = if (activeSubTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Troubleshoot",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeSubTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == 2) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { activeSubTab = 2 }
                        .padding(vertical = 10.dp)
                        .testTag("tab_climate_trigger"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Climate icon",
                            tint = if (activeSubTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Climate",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeSubTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == 3) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { activeSubTab = 3 }
                        .padding(vertical = 10.dp)
                        .testTag("tab_nurseries_trigger"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Nurseries icon",
                            tint = if (activeSubTab == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Nurseries",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeSubTab == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (activeSubTab == 0) {
                // --- PLANT DATABASE DIRECTORY VIEW ---
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("database_directory_list"),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        // Header Welcome Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CanvasSproutIcon(modifier = Modifier.size(48.dp), color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Botanical Encyclopedia",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Search & access expert watering, solar, soil, and environment care guidelines.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { 
                                searchQuery = it 
                                if (it.trim().length >= 3) {
                                    viewModel.recordSearchOrCheck(it.trim())
                                }
                            },
                            placeholder = { Text("Search common houseplants (e.g. Monstera)...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("database_search_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().animateContentSize(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showAdvancedFilters = !showAdvancedFilters }
                                        .testTag("btn_toggle_advanced_filters"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "⚙️ Advanced Filtering Controls",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (filterSunlight != "All" || filterLocation != "All" || filterToxicity != "All") {
                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 6.dp)
                                                    .size(6.dp)
                                                    .background(Color.Red, CircleShape)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (showAdvancedFilters) "Hide filters ▲" else "Expand filters ▼",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (showAdvancedFilters) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // --- Sunlight Intensity ---
                                    Text("🔆 SUNLIGHT INTENSITY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("All", "Full Sun", "Bright Indirect", "Low/Medium").forEach { value ->
                                            val isSelected = filterSunlight == value
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                                    .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                    .clickable { filterSunlight = value }
                                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                                    .testTag("adv_sunlight_${value.lowercase().replace("/", "_")}")
                                            ) {
                                                Text(value, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // --- Indoor vs Outdoor ---
                                    Text("🏡 DOMESTIC ADAPTABILITY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("All", "Indoor Only", "Outdoor/Adaptable").forEach { value ->
                                            val isSelected = filterLocation == value
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                                    .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                    .clickable { filterLocation = value }
                                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                                    .testTag("adv_location_${value.lowercase().replace("/", "_").replace(" ", "_")}")
                                            ) {
                                                Text(value, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // --- Toxicity Levels ---
                                    Text("🐾 PET & TOXICITY WARNINGS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("All", "Pet-Safe", "Toxic Warning").forEach { value ->
                                            val isSelected = filterToxicity == value
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                                    .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                    .clickable { filterToxicity = value }
                                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                                    .testTag("adv_toxicity_${value.lowercase().replace("/", "_").replace(" ", "_")}")
                                            ) {
                                                Text(value, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = "Reset Advanced Filters",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clickable {
                                                    filterSunlight = "All"
                                                    filterLocation = "All"
                                                    filterToxicity = "All"
                                                }
                                                .testTag("btn_reset_adv_filters")
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (recentSearches.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🕒 Recent Searches",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Clear History",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clickable { viewModel.clearRecentSearches() }
                                            .testTag("btn_clear_recent_searches")
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    recentSearches.take(6).forEach { recent ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                .clickable { 
                                                    searchQuery = recent.query 
                                                    viewModel.recordSearchOrCheck(recent.query)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                                .testTag("recent_search_chip_${recent.query.lowercase().replace(" ", "_")}"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = "SearchIcon",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = recent.query,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Filter chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp)
                                .testTag("database_filter_row"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Beginner Friendly", "Low Light", "Safe for Pets").forEach { filter ->
                                val isSelected = activeFilter == filter
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                        )
                                        .clickable { activeFilter = filter }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                        .testTag("filter_chip_${filter.lowercase().replace(" ", "_")}")
                                        ) {
                                    Text(
                                        text = filter,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (filteredPlantsByQuery.isNotEmpty()) {
                        items(filteredPlantsByQuery) { plant ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        selectedPlant = plant 
                                        viewModel.recordSearchOrCheck(plant.commonName)
                                    }
                                    .testTag("common_plant_card_${plant.commonName.replace(" ", "_")}"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CanvasLeafIcon(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = plant.commonName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${plant.scientificName} • ${plant.family}",
                                            fontSize = 11.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Watering interval
                                            Box(
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Every ${plant.wateringIntervalDays} days",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }

                                            // Pet safety
                                            val isSafe = plant.toxicity.contains("Safe", ignoreCase = true) || plant.toxicity.contains("Non-Toxic", ignoreCase = true)
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (isSafe) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                                        RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (isSafe) "🐾 Pet Safe" else "🐾 Caution",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSafe) Color(0xFF2E7D32) else Color(0xFFE65100)
                                                )
                                            }
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Open profile details",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CanvasSproutIcon(modifier = Modifier.size(48.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No plants match search rules",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Try searching for simpler names or check active filters.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            } else if (activeSubTab == 1) {
                AITroubleshooterScreen(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
            } else if (activeSubTab == 2) {
                ClimateCareTipsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
            } else {
                GardenNurseryFinderScreen(
                    modifier = Modifier.weight(1f),
                    nurseryQuery = nurseryQuery,
                    onQueryChange = { nurseryQuery = it }
                )
            }
        }
    } else {
        // --- DETAILED SELECTED PROFILE VIEW CONTAINER ---
        val plant = selectedPlant!!

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("database_plant_details_${plant.commonName.replace(" ", "_")}"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                // Header Back Button & Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { selectedPlant = null },
                        modifier = Modifier
                            .size(34.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), CircleShape)
                            .testTag("btn_back_to_directory")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "Species Catalog File",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.width(34.dp))
                }
            }

            item {
                // Herbarium Identity card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CanvasLeafIcon(modifier = Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = plant.commonName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = plant.scientificName,
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Family: ${plant.family}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            item {
                // Short Botanical Description
                Text(
                    text = plant.description,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }

            // --- MAIN SPECIFICATIONS CARDS ---
            item {
                // 1. Sunlight requirements
                CareManualDetailCardItem(
                    title = "☀️ SOLAR INTENSITY REQUIREMENTS",
                    content = plant.sunlightRequirements,
                    badgeText = if (plant.sunlightRequirements.contains("Direct", ignoreCase = true)) "High Exposure" else "Filter Tolerant",
                    badgeColor = Color(0xFFFFF3E0),
                    textColor = Color(0xFFE65100),
                    testTagSuffix = "sunlight"
                )
            }

            item {
                // 2. Watering specs
                CareManualDetailCardItem(
                    title = "💧 MOISTURE CYCLE & HYDRATION",
                    content = "Suggested standard cycle: every ${plant.wateringIntervalDays} days.\n" + plant.wateringInstructions,
                    badgeText = "Recommended Interval: ${plant.wateringIntervalDays}d",
                    badgeColor = Color(0xFFE1F5FE),
                    textColor = Color(0xFF0288D1),
                    testTagSuffix = "watering"
                )
            }

            item {
                // 3. Optimal Temp range
                CareManualDetailCardItem(
                    title = "🌡️ OPTIMAL AMBIENT TEMPERATURE",
                    content = plant.optimalTemperature,
                    badgeText = "Comfort Zone",
                    badgeColor = Color(0xFFFBE9E7),
                    textColor = Color(0xFFD84315),
                    testTagSuffix = "temperature"
                )
            }

            item {
                // 4. Soil type
                CareManualDetailCardItem(
                    title = "🌱 SOIL PREFERENCE",
                    content = plant.soilPreference,
                    badgeText = "Substrate Rule",
                    badgeColor = Color(0xFFEFEBE9),
                    textColor = Color(0xFF4E342E),
                    testTagSuffix = "soil"
                )
            }

            item {
                // 5. Toxicity / Compatibility
                val isSafe = plant.toxicity.contains("Safe", ignoreCase = true) || plant.toxicity.contains("Non-Toxic", ignoreCase = true)
                CareManualDetailCardItem(
                    title = "🐾 PET & DOMESTIC TOXICITY COMPATIBILITY",
                    content = plant.toxicity,
                    badgeText = if (isSafe) "PET HEALTHY SAFE" else "CAUTION ADVISORY",
                    badgeColor = if (isSafe) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    textColor = if (isSafe) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                    testTagSuffix = "toxicity"
                )
            }

            // --- EXPERT GREEN THUMB PRO-TIPS ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💡 PROFESSIONAL CARE TIPS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        plant.quickTips.forEachIndexed { idx, tip ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "✦",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = tip,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // --- ACTION INTEGRATION BUTTON & WISHLIST ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_integrate_plant_to_garden"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add plant Icon", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Integrate into My Garden", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.addPlantToWishlist(plant)
                            Toast.makeText(context, "✨ ${plant.commonName} saved to your Wishlist!", Toast.LENGTH_SHORT).show()
                            selectedPlant = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_save_to_wishlist"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("✨ Add to Wishlist", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Quick alert setup confirmation dialog
        if (showAddDialog) {
            var remindersEnabled by remember { mutableStateOf(true) }
            var customIntervalDays by remember { mutableStateOf(plant.wateringIntervalDays) }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = {
                    Text(
                        text = "Integrate ${plant.commonName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Would you like to add this species to your active home garden workspace? We will configure notifications based on recommended specs.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Reminders active
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Water Reminders",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Switch(
                                checked = remindersEnabled,
                                onCheckedChange = { remindersEnabled = it },
                                modifier = Modifier.testTag("integration_reminders_switch")
                            )
                        }

                        if (remindersEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Configured Interval: $customIntervalDays days",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Slider(
                                value = customIntervalDays.toFloat(),
                                onValueChange = { customIntervalDays = it.toInt() },
                                valueRange = 1f..30f,
                                steps = 29,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("integration_interval_slider")
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.saveAnalyzedPlant(
                                plant = plant,
                                bitmap = null,
                                isReminderEnabled = remindersEnabled,
                                reminderHour = 9,
                                reminderMinute = 0,
                                customInterval = customIntervalDays
                            )
                            Toast.makeText(context, "${plant.commonName} successfully added to garden!", Toast.LENGTH_SHORT).show()
                            showAddDialog = false
                            selectedPlant = null // Go back to directory
                        },
                        modifier = Modifier.testTag("btn_confirm_integration")
                    ) {
                        Text("Add to Garden", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAddDialog = false },
                        modifier = Modifier.testTag("btn_cancel_integration")
                    ) {
                        Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun CareManualDetailCardItem(
    title: String,
    content: String,
    badgeText: String,
    badgeColor: Color,
    textColor: Color,
    testTagSuffix: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("detail_card_${testTagSuffix}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    letterSpacing = 0.8.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = content,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ==================== DYNAMIC DETAILS DIALOG SHEET ====================

@Composable
fun PlantDetailDialog(
    plant: PlantEntity,
    onDismiss: () -> Unit,
    onWaterNow: (Long) -> Unit,
    onFertilizeNow: (Long) -> Unit,
    viewModel: com.example.viewmodel.PlantViewModel
) {
    val context = LocalContext.current
    var customTagInput by remember { mutableStateOf("") }
    val sunlightLogs by remember(plant.id) {
        viewModel.getSunlightLogs(plant.id)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val humidityLogs by remember(plant.id) {
        viewModel.getHumidityLogs(plant.id)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val journals by remember(plant.id) {
        viewModel.getJournals(plant.id)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            val scrollState = rememberScrollState()

            // Decode image if exists
            val localBitmap = remember(plant.customImageUri) {
                if (!plant.customImageUri.isNullOrEmpty()) {
                    try {
                        val decodedBytes = android.util.Base64.decode(plant.customImageUri, android.util.Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    } catch (e: Exception) {
                        null
                    }
                } else null
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                // Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (localBitmap != null) {
                        Image(
                            bitmap = localBitmap.asImageBitmap(),
                            contentDescription = plant.commonName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        CanvasLeafIcon(modifier = Modifier.size(54.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                                .size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = plant.commonName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${plant.scientificName} • ${plant.family}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = plant.description,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    // --- Plant Organization Tagging System Section ---
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "🏷️ Organise & Categorise (Room / Type)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("detail_tags_header")
                    )
                    Text(
                        text = "Assign this specimen to rooms ('Living Room', 'Office') or tag by custom types ('Succulent', 'Fern') to structure your collections.",
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val plantTags = remember(plant.tags) {
                        if (plant.tags.isBlank()) emptyList() else plant.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    }

                    if (plantTags.isEmpty()) {
                        Text(
                            text = "No category tags set. Tap suggested quick tags below or enter custom labels to get started.",
                            fontSize = 10.sp,
                            style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            plantTags.forEach { tag ->
                                Row(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = tag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove tag $tag",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable {
                                                val updated = plantTags.filter { it != tag }.joinToString(", ")
                                                viewModel.updatePlantTags(plant, updated)
                                            }
                                            .testTag("btn_remove_tag_${tag.lowercase().replace(" ", "_")}")
                                    )
                                }
                            }
                        }
                    }

                    // Recommended tag suggestions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val suggestedTags = listOf("Living Room", "Office", "Balcony", "Succulent", "Fern", "Flowering")
                        suggestedTags.forEach { tag ->
                            val isAssigned = plantTags.any { it.equals(tag, ignoreCase = true) }
                            if (!isAssigned) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable {
                                            viewModel.updatePlantTags(plant, (plantTags + tag).joinToString(", "))
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("chip_suggest_tag_${tag.lowercase().replace(" ", "_")}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "+ $tag", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // Custom free-text entry row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customTagInput,
                            onValueChange = { customTagInput = it },
                            placeholder = { Text("Or add custom classification...", fontSize = 10.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("input_custom_tag"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        )
                        Button(
                            onClick = {
                                val trimmed = customTagInput.trim()
                                if (trimmed.isNotEmpty() && !plantTags.any { it.equals(trimmed, ignoreCase = true) }) {
                                    viewModel.updatePlantTags(plant, (plantTags + trimmed).joinToString(", "))
                                    customTagInput = ""
                                }
                            },
                            modifier = Modifier
                                .height(42.dp)
                                .testTag("btn_add_custom_tag"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Add", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Caring Parameters
                    val fertInterval = if (plant.customFertilizingIntervalDays > 0) plant.customFertilizingIntervalDays else plant.fertilizingIntervalDays
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VitalElementBox(
                            modifier = Modifier.weight(1f),
                            title = "Watering Due",
                            value = "Every ${plant.wateringIntervalDays} days",
                            icon = { CanvasWaterDropIcon(modifier = Modifier.size(16.dp)) }
                        )
                        VitalElementBox(
                            modifier = Modifier.weight(1f),
                            title = "Sunlight",
                            value = plant.sunlightRequirements,
                            icon = { CanvasSunIcon(modifier = Modifier.size(16.dp)) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VitalElementBox(
                            modifier = Modifier.weight(1f),
                            title = "Soil Mix",
                            value = plant.soilPreference,
                            icon = { Icon(Icons.Default.Refresh, "soil", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp)) }
                        )
                        VitalElementBox(
                            modifier = Modifier.weight(1f),
                            title = "Safety",
                            value = plant.toxicity,
                            icon = { Icon(Icons.Default.Warning, "toxic", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp)) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    VitalElementBox(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Fertilization Due",
                        value = "Every $fertInterval days",
                        icon = { CanvasFertilizerIcon(modifier = Modifier.size(16.dp)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Watering Strategy Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = plant.wateringInstructions,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Historical Logging",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val dateStr = remember(plant.dateIdentified) {
                        try {
                            val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                            sdf.format(Date(plant.dateIdentified))
                        } catch (e: Exception) {
                            "Unknown"
                        }
                    }
                    Text(
                        text = "Captured on: $dateStr",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HistoricalCareChart(plant = plant)

                    // --- Sunlight Exposure Tracker Section ---
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "☀️ Solar Exposure Tracker",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp).testTag("header_solar_tracker")
                    )

                    Text(
                        text = "Track and log your plant's daily direct vs. indirect sunlight cycles to ensure health requirements are satisfied.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Summary KPI section
                    if (sunlightLogs.isNotEmpty()) {
                        val totalDirect = sunlightLogs.map { it.directHours }.sum()
                        val totalIndirect = sunlightLogs.map { it.indirectHours }.sum()
                        val avgDirect = totalDirect / sunlightLogs.size
                        val avgIndirect = totalIndirect / sunlightLogs.size
                        val avgTotal = avgDirect + avgIndirect

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("kpi_sunlight_summary"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "AVERAGE DAILY INTAKE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 0.8.sp
                                        )
                                        Text(
                                            text = String.format(Locale.getDefault(), "%.1f hrs / day", avgTotal),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${sunlightLogs.size} logs",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Stacked bar visual representation
                                val totalHoursForBar = avgTotal.coerceAtLeast(0.1f)
                                val directPercent = avgDirect / totalHoursForBar
                                val indirectPercent = avgIndirect / totalHoursForBar

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape)
                                ) {
                                    if (avgDirect > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .weight(directPercent.coerceAtLeast(0.01f))
                                                .fillMaxHeight()
                                                .background(Color(0xFFFFA726)) // Bright Amber for direct
                                        )
                                    }
                                    if (avgIndirect > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .weight(indirectPercent.coerceAtLeast(0.01f))
                                                .fillMaxHeight()
                                                .background(Color(0xFFFFF176)) // Light Yellow for indirect
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFA726)))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = String.format(Locale.getDefault(), "Direct: %.1fh (%.0f%%)", avgDirect, directPercent * 100),
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFF176)))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = String.format(Locale.getDefault(), "Indirect: %.1fh (%.0f%%)", avgIndirect, indirectPercent * 100),
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Empty State for Sunlight logs
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("kpi_sunlight_empty"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "No solar cycles logged yet",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "No solar logs yet",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Log custom sunlight sessions below to start tracking actual health guidelines.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Form to add a Sunlight Log entry
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("sunlight_log_form"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "LOG DAILY EXPOSURE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            var directInput by remember { mutableStateOf(4.0f) }
                            var indirectInput by remember { mutableStateOf(4.0f) }
                            var logNote by remember { mutableStateOf("") }

                            // Direct hours control
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(Locale.getDefault(), "Direct light: %.1f hrs", directInput),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = { directInput = (directInput - 0.5f).coerceAtLeast(0.0f) },
                                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.surface, CircleShape).testTag("btn_direct_light_dec")
                                    ) {
                                        Text(text = "-", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    }
                                    IconButton(
                                        onClick = { directInput = (directInput + 0.5f).coerceAtMost(24.0f) },
                                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.surface, CircleShape).testTag("btn_direct_light_inc")
                                    ) {
                                        Text(text = "+", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                            Slider(
                                value = directInput,
                                onValueChange = { directInput = it },
                                valueRange = 0f..16f,
                                steps = 31,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFFFA726),
                                    activeTrackColor = Color(0xFFFFA726)
                                ),
                                modifier = Modifier.testTag("slider_direct_light")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Indirect hours control
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(Locale.getDefault(), "Indirect light: %.1f hrs", indirectInput),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = { indirectInput = (indirectInput - 0.5f).coerceAtLeast(0.0f) },
                                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.surface, CircleShape).testTag("btn_indirect_light_dec")
                                    ) {
                                        Text(text = "-", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    }
                                    IconButton(
                                        onClick = { indirectInput = (indirectInput + 0.5f).coerceAtMost(24.0f) },
                                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.surface, CircleShape).testTag("btn_indirect_light_inc")
                                    ) {
                                        Text(text = "+", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                            Slider(
                                value = indirectInput,
                                onValueChange = { indirectInput = it },
                                valueRange = 0f..16f,
                                steps = 31,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFFFF176),
                                    activeTrackColor = Color(0xFFFFF176)
                                ),
                                modifier = Modifier.testTag("slider_indirect_light")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Weather / Context Quick Choice Chips
                            Text(
                                text = "Lighting Context / Weather",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val weatherPresets = listOf("Sunny Day", "Overcast", "Partial Shade", "Grow Light")
                                weatherPresets.forEach { preset ->
                                    val isSelected = logNote == preset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable { logNote = if (isSelected) "" else preset }
                                            .padding(vertical = 6.dp)
                                            .testTag("btn_preset_${preset.replace(" ", "_")}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = preset,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val resolvedNote = if (logNote.isEmpty()) "Standard exposure" else logNote
                                    viewModel.logSunlight(plant.id, directInput, indirectInput, resolvedNote)
                                    Toast.makeText(context, "Logged ${directInput + indirectInput}h sunlight!", Toast.LENGTH_SHORT).show()
                                    // Reset form values slightly
                                    logNote = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("log_sunlight_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "check", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Sunlight Log", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Historical exposure log entries list
                    if (sunlightLogs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "RECENT SUNLIGHT HISTORY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Show last 5 logs for premium compactness inside detail dialog
                            sunlightLogs.take(5).forEach { log ->
                                val logDateStr = try {
                                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    sdf.format(Date(log.timestamp))
                                } catch (e: Exception) {
                                    "Today"
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("sunlight_log_item_${log.id}"),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = logDateStr,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "☀️ Direct: ${log.directHours}h",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFFFA726),
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "🌤️ Indirect: ${log.indirectHours}h",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFFBC02D),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            if (log.notes.isNotEmpty()) {
                                                Text(
                                                    text = "Context: ${log.notes}",
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.deleteSunlightLog(log)
                                                Toast.makeText(context, "Log removed", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .testTag("btn_delete_sunlight_log_${log.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete log",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- Daily Humidity Log & Drift Alerts Section ---
                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "💧 Relative Humidity & Drift Alerts",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp).testTag("header_humidity_tracker")
                    )

                    Text(
                        text = "Maintain proper foliage transpiration by tracking local daily relative humidity. Check active system alerts if levels drift from safety bounds.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Active Alert Container (If most recent humidity log is out of safe range)
                    if (humidityLogs.isNotEmpty()) {
                        val latestLog = humidityLogs.first()
                        val isDrifting = latestLog.humidityPercent < latestLog.optimalRangeMin || latestLog.humidityPercent > latestLog.optimalRangeMax
                        if (isDrifting) {
                            val alertMsg = if (latestLog.humidityPercent < latestLog.optimalRangeMin) {
                                "Too dry! Increase ambient moisture to prevent dry brown foliage tips."
                            } else {
                                "Too humid! Reduce humidity or enhance ventilation to prevent mold and pest activity."
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("humidity_drift_alert_card"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("⚠️", fontSize = 20.sp)
                                    Column {
                                        Text(
                                            text = "HUMIDITY DRIFT DETECTED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "Current: ${latestLog.humidityPercent.toInt()}% (Target: ${latestLog.optimalRangeMin.toInt()}%-${latestLog.optimalRangeMax.toInt()}%)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            text = alertMsg,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp,
                                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("✅", fontSize = 16.sp)
                                    Column {
                                        Text(
                                            text = "HUMIDITY LEVEL STABLE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "Current registered level is perfectly balanced inside optimal guidelines.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Logging entry form card
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("humidity_log_form"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "LOG CURRENT RELATIVE HUMIDITY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 0.8.sp
                            )

                            var humidityValue by remember { mutableStateOf(50.0f) }
                            var optMin by remember { mutableStateOf(40.0f) }
                            var optMax by remember { mutableStateOf(70.0f) }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Current humidity: ${humidityValue.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    val presets = listOf(
                                        "Arid" to (15f to 30f),
                                        "Modest" to (35f to 55f),
                                        "Tropical" to (60f to 85f)
                                    )
                                    presets.forEach { (name, range) ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f))
                                                .clickable {
                                                    optMin = range.first
                                                    optMax = range.second
                                                }
                                                .padding(horizontal = 6.dp, vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(name, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                }
                            }

                            Slider(
                                value = humidityValue,
                                onValueChange = { humidityValue = it },
                                valueRange = 10f..100f,
                                modifier = Modifier.testTag("slider_humidity_entry")
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Min Target: ${optMin.toInt()}%", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Slider(
                                        value = optMin,
                                        onValueChange = { optMin = it.coerceAtMost(optMax - 5f) },
                                        valueRange = 10f..95f,
                                        modifier = Modifier.testTag("slider_humidity_min")
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Max Target: ${optMax.toInt()}%", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Slider(
                                        value = optMax,
                                        onValueChange = { optMax = it.coerceAtLeast(optMin + 5f) },
                                        valueRange = 15f..100f,
                                        modifier = Modifier.testTag("slider_humidity_max")
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.logHumidity(plant.id, humidityValue.toDouble(), optMin.toDouble(), optMax.toDouble())
                                    Toast.makeText(context, "Humidity session logged successfully!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("btn_save_humidity_log"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save Humidity Entry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Historical humidity list
                    if (humidityLogs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            humidityLogs.take(3).forEach { log ->
                                val dateStr = try {
                                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(log.timestamp))
                                } catch (e: Exception) {
                                    "Log entry"
                                }
                                val outOfBounds = log.humidityPercent < log.optimalRangeMin || log.humidityPercent > log.optimalRangeMax
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(dateStr, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("Registered: ${log.humidityPercent.toInt()}%", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                            Text("Goal: ${log.optimalRangeMin.toInt()}%-${log.optimalRangeMax.toInt()}%", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (outOfBounds) Color(0xFFFFCDD2) else Color(0xFFC8E6C9))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (outOfBounds) "Drift Alert" else "Healthy",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (outOfBounds) Color(0xFFC62828) else Color(0xFF2E7D32)
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteHumidityLog(log) },
                                            modifier = Modifier.size(24.dp).testTag("btn_delete_humidity_log_${log.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- Text-Based Plant Journal Section ---
                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "📝 Botanical Care Journal",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp).testTag("header_journal_log")
                    )

                    Text(
                        text = "Log ongoing chronological growth narratives, foliage milestones, interventions, or any pest sightings in detail.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Adding entry card
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("journal_add_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "NEW JOURNAL OBSERVATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 0.8.sp
                            )

                            var journalInputText by remember { mutableStateOf("") }
                            var selectedCategory by remember { mutableStateOf("Growth") } // Growth, Pest, Blooming, General

                            // Category chips
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val cats = listOf(
                                    "Growth" to "🌱 Growth",
                                    "Pest" to "🐛 Pest/Disease",
                                    "Blooming" to "🌸 Blooming",
                                    "General" to "📝 General"
                                )
                                cats.forEach { (catId, catLabel) ->
                                    val isSelected = selectedCategory == catId
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary 
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable { selectedCategory = catId }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                            .testTag("chip_journal_cat_$catId"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = catLabel,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = journalInputText,
                                onValueChange = { journalInputText = it },
                                placeholder = { Text("E.g., Sprouting secondary stems! Spotted some spider mites on leaves...", fontSize = 11.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(75.dp)
                                    .testTag("input_journal_note"),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            )

                            Button(
                                onClick = {
                                    if (journalInputText.trim().isNotEmpty()) {
                                        viewModel.addJournalEntry(plant.id, journalInputText.trim(), selectedCategory)
                                        journalInputText = ""
                                        Toast.makeText(context, "Journal entry documented!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("btn_save_journal"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Log Botanical Observation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Journal entries list
                    if (journals.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            journals.forEach { entry ->
                                val dateStr = try {
                                    SimpleDateFormat("MMMM dd, yyyy - h:mm a", Locale.getDefault()).format(Date(entry.timestamp))
                                } catch (e: Exception) {
                                    "Date"
                                }
                                val isPestCat = entry.category == "Pest"
                                
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("journal_item_${entry.id}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isPestCat) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                    ),
                                    border = BorderStroke(
                                        width = if (isPestCat) 1.5.dp else 1.dp,
                                        color = if (isPestCat) MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(
                                                            if (isPestCat) MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    val labelText = when (entry.category) {
                                                        "Pest" -> "🐛 Pest Alert"
                                                        "Growth" -> "🌱 Growth"
                                                        "Blooming" -> "🌸 Blooming"
                                                        else -> "📝 Note"
                                                    }
                                                    Text(
                                                        text = labelText,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isPestCat) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                Text(dateStr, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = entry.note,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteJournal(entry) },
                                            modifier = Modifier.size(24.dp).testTag("btn_delete_journal_${entry.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- Plant Growth Progress Gallery Section ---
                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(14.dp))

                    PlantGrowthGallery(plantId = plant.id, viewModel = viewModel)

                    // --- Export CSV Action Option ---
                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "📊 Export Records",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Download a spreadsheet database file (CSV) of this plant's complete multi-parameter care logs.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            val logs = generateHistoricalData(plant)
                            exportPlantHistoryToCSV(context, plant, logs)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_export_csv"),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "CSV", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Care Log History (CSV)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onWaterNow(plant.id) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CanvasWaterDropIcon(modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Water", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Button(
                            onClick = { onFertilizeNow(plant.id) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CanvasFertilizerIcon(modifier = Modifier.size(14.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Feed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("Dismiss", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FoliageLoadingIndicator(modifier: Modifier = Modifier) {
    val steps = listOf(
        "Scanning foliage geometry & contour mapping",
        "Searching taxonomic catalog index",
        "Formulating safe watering intervals",
        "Compiling lighting & pet toxicity guidelines"
    )
    
    var currentStep by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (currentStep < steps.size - 1) {
            kotlinx.coroutines.delay(1800)
            currentStep++
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(24.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant Visual Animation Sphere
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Pulse Ring
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(pulseScale)
                        .alpha(pulseAlpha)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape)
                )
                // Inner Decorative Ring
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CanvasLeafIcon(
                        modifier = Modifier.size(28.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Analyzing Foliage Spectrum",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Securing scientific diagnostic reading from botanical intelligence matrices.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(20.dp))

            // Step diagnostics
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                steps.forEachIndexed { index, stepText ->
                    val isCompleted = index < currentStep
                    val isActive = index == currentStep
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Step Indicator Icon
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> MaterialTheme.colorScheme.primary
                                        isActive -> MaterialTheme.colorScheme.surfaceVariant
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    border = BorderStroke(
                                        width = 1.5.dp,
                                        color = if (isCompleted || isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Step Complete",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else if (isActive) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(14.dp))
                        
                        Text(
                            text = stepText,
                            fontSize = 13.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = when {
                                isCompleted -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                isActive -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Linear overall progress indicator
            val progressAnim = animateFloatAsState(
                targetValue = (currentStep + 1).toFloat() / steps.size.toFloat(),
                animationSpec = tween(600),
                label = "progress"
            )
            LinearProgressIndicator(
                progress = { progressAnim.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun WateringScheduleTimeline(
    allPlants: List<PlantEntity>,
    onWaterClick: (Long) -> Unit,
    onSnoozeClick: (PlantEntity) -> Unit,
    onUpdateSchedule: (PlantEntity, Boolean, Int, Int, Int) -> Unit
) {
    var activeTestNotificationPlant by remember { mutableStateOf<PlantEntity?>(null) }
    val context = LocalContext.current

    if (activeTestNotificationPlant != null) {
        SimulatedNotificationDialog(
            plant = activeTestNotificationPlant!!,
            onDismiss = { activeTestNotificationPlant = null },
            onWaterNow = { id ->
                onWaterClick(id)
                activeTestNotificationPlant = null
                Toast.makeText(context, "Hydration registered!", Toast.LENGTH_SHORT).show()
            },
            onSnoozeNow = { plant ->
                onSnoozeClick(plant)
                activeTestNotificationPlant = null
                Toast.makeText(context, "Snoozed successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    val sortedPlants = remember(allPlants) {
        allPlants.sortedBy { plant ->
            val millisecondsSinceWatered = System.currentTimeMillis() - plant.lastWateredTime
            val daysSinceWatered = millisecondsSinceWatered / (1000 * 60 * 60 * 24)
            val interval = if (plant.customWateringIntervalDays > 0) plant.customWateringIntervalDays else plant.wateringIntervalDays
            (interval - daysSinceWatered)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Reminders are generated dynamically based on species care guides. You can customize, snooze, or run simulation drills.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        items(sortedPlants) { plant ->
            val millisecondsSinceWatered = System.currentTimeMillis() - plant.lastWateredTime
            val daysSinceWatered = millisecondsSinceWatered / (1000 * 60 * 60 * 24)
            val interval = if (plant.customWateringIntervalDays > 0) plant.customWateringIntervalDays else plant.wateringIntervalDays
            val daysRemaining = (interval - daysSinceWatered)
            
            val dueMessage = when {
                daysRemaining < 0 -> "Overdue by ${-daysRemaining} days!"
                daysRemaining == 0L -> "DRY - Hydro Due Today!"
                daysRemaining == 1L -> "Due tomorrow"
                else -> "Due in $daysRemaining days"
            }
            
            val isUrgent = daysRemaining <= 0
            val percentLeft = (1f - (daysSinceWatered.toFloat() / interval.toFloat())).coerceIn(0f, 1f)
            val progressColor = when {
                percentLeft > 0.5f -> MaterialTheme.colorScheme.primary
                percentLeft > 0.2f -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.error
            }

            var isEditingSchedule by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(0.5.dp, RoundedCornerShape(16.dp))
                    .border(
                        border = BorderStroke(
                            width = if (isUrgent) 1.5.dp else 1.dp,
                            color = if (isUrgent) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("schedule_card_${plant.id}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = plant.commonName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${plant.scientificName} • Cycle: Every $interval days",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isUrgent) MaterialTheme.colorScheme.error.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = dueMessage.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Contour Moisture:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.width(110.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(percentLeft)
                                    .background(progressColor, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${(percentLeft * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = progressColor
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Reminder Config:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.width(110.dp)
                        )
                        Text(
                            text = if (plant.isReminderEnabled) {
                                "💡 Enabled, daily preference is ${plant.reminderHour}:00 AM/PM"
                            } else "🚫 Disabled",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (plant.isReminderEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { isEditingSchedule = !isEditingSchedule },
                            modifier = Modifier.testTag("btn_configure_schedule_${plant.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, contentDescription = "Edit Schedule", modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isEditingSchedule) "Close Editor" else "Adjust Schedule", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { activeTestNotificationPlant = plant },
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f), CircleShape)
                                    .testTag("btn_test_reminder_${plant.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Test Notification alarm",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            TextButton(
                                onClick = { onSnoozeClick(plant) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_snooze_${plant.id}")
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Snooze icon", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Snooze 24h", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onWaterClick(plant.id) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_water_log_sch_${plant.id}")
                            ) {
                                CanvasWaterDropIcon(modifier = Modifier.size(11.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Water", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    if (isEditingSchedule) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                var isEnabled by remember { mutableStateOf(plant.isReminderEnabled) }
                                var intervalDays by remember { mutableStateOf(interval) }
                                var hourPref by remember { mutableStateOf(plant.reminderHour) }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Active Notifications", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Switch(
                                        checked = isEnabled,
                                        onCheckedChange = { isEnabled = it },
                                        modifier = Modifier.testTag("edit_switch_reminder_${plant.id}")
                                    )
                                }

                                if (isEnabled) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Interval: every $intervalDays days", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Slider(
                                        value = intervalDays.toFloat(),
                                        onValueChange = { intervalDays = it.toInt() },
                                        valueRange = 1f..31f,
                                        steps = 30,
                                        modifier = Modifier.testTag("edit_slider_interval_${plant.id}")
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Alert Preference Hour:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        val subTimes = listOf(9 to "9 AM", 14 to "2 PM", 18 to "6 PM")
                                        subTimes.forEach { (hr, lb) ->
                                            val isSel = hourPref == hr
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                                    .clickable { hourPref = hr }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(lb, fontSize = 10.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        onUpdateSchedule(plant, isEnabled, hourPref, 0, intervalDays)
                                        isEditingSchedule = false
                                        Toast.makeText(context, "${plant.commonName} schedule updated!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_save_edit_schedule_${plant.id}")
                                ) {
                                    Text("Save Changes", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatedNotificationDialog(
    plant: PlantEntity,
    onWaterNow: (Long) -> Unit,
    onSnoozeNow: (PlantEntity) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CanvasWaterDropIcon(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "BOTANICAL PUSH REMINDER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.1.sp
                        )
                        Text(
                            text = "Reminder Matrix Triggered Now • ⏰",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close simulation", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Your ${plant.commonName} is Thirsty!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = "A scheduler reminder is configured to repeat every ${plant.customWateringIntervalDays.takeIf { it > 0 } ?: plant.wateringIntervalDays} days.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SPECIES WATERING GUIDELINE:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = plant.wateringInstructions,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSnoozeNow(plant) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Notifications, "snooze", modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Snooze-24h", fontSize = 11.sp)
                    }
                    Button(
                        onClick = { onWaterNow(plant.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        CanvasWaterDropIcon(modifier = Modifier.size(12.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Water Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LogWateringDialog(
    plant: PlantEntity,
    onDismiss: () -> Unit,
    onConfirmWatering: (Long, Long) -> Unit // plantId, customTimestamp
) {
    var selectedOffsetDays by remember { mutableStateOf(0) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CanvasWaterDropIcon(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Log Watering",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "When did you last water ${plant.commonName}?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Relative Days Selections List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickChoices = listOf(
                        0 to "💧 Just Now (Today)",
                        1 to "📅 Yesterday",
                        2 to "📅 2 Days Ago",
                        3 to "📅 3 Days Ago",
                        5 to "📅 5 Days Ago",
                        7 to "📅 1 Week Ago"
                    )
                    
                    quickChoices.forEach { (days, label) ->
                        val isSelected = selectedOffsetDays == days
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedOffsetDays = days
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                .testTag("water_log_choice_${days}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .border(
                                            width = if (isSelected) 5.dp else 2.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Advanced custom offset days slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Offset Days:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = if (selectedOffsetDays == 0) "Today" else "$selectedOffsetDays days ago",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Slider(
                    value = selectedOffsetDays.toFloat(),
                    onValueChange = { selectedOffsetDays = (it + 0.5f).toInt() },
                    valueRange = 0f..30f,
                    steps = 30,
                    modifier = Modifier.testTag("water_log_slider")
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Dialog Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }
                    
                    Button(
                        onClick = {
                            val targetTime = System.currentTimeMillis() - (selectedOffsetDays.toLong() * 24 * 60 * 60 * 1000L)
                            onConfirmWatering(plant.id, targetTime)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("water_log_confirm"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LogFertilizingDialog(
    plant: PlantEntity,
    onDismiss: () -> Unit,
    onConfirmFertilizing: (Long, Long, Int) -> Unit // plantId, customTimestamp, customIntervalDays
) {
    var selectedOffsetDays by remember { mutableStateOf(0) }
    var fertIntervalDays by remember { 
        mutableStateOf(
            if (plant.customFertilizingIntervalDays > 0) plant.customFertilizingIntervalDays else plant.fertilizingIntervalDays
        ) 
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .border(BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.2f)), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CanvasFertilizerIcon(modifier = Modifier.size(24.dp), color = Color(0xFF4CAF50))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Log Fertilization",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "When did you last fertilize ${plant.commonName}?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Relative Days Selections List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickChoices = listOf(
                        0 to "🌱 Just Now (Today)",
                        1 to "📅 Yesterday",
                        3 to "📅 3 Days Ago",
                        7 to "📅 1 Week Ago",
                        14 to "📅 2 Weeks Ago",
                        30 to "📅 1 Month Ago"
                    )
                    
                    quickChoices.forEach { (days, label) ->
                        val isSelected = selectedOffsetDays == days
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedOffsetDays = days
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                .testTag("fertilize_log_choice_${days}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                                )
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .border(
                                            width = if (isSelected) 5.dp else 2.dp,
                                            color = if (isSelected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Advanced custom offset days slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Offset Days:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = if (selectedOffsetDays == 0) "Today" else "$selectedOffsetDays days ago",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF388E3C)
                    )
                }
                
                Slider(
                    value = selectedOffsetDays.toFloat(),
                    onValueChange = { selectedOffsetDays = (it + 0.5f).toInt() },
                    valueRange = 0f..90f,
                    steps = 90,
                    modifier = Modifier.testTag("fertilize_log_slider")
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Interval Adjustment Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Customize Feeding Interval:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Every $fertIntervalDays days",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF388E3C)
                    )
                }
                
                Slider(
                    value = fertIntervalDays.toFloat(),
                    onValueChange = { fertIntervalDays = (it + 0.5f).toInt() },
                    valueRange = 5f..120f,
                    steps = 115,
                    modifier = Modifier.testTag("fertilize_interval_slider")
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Dialog Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }
                    
                    Button(
                        onClick = {
                            val targetTime = System.currentTimeMillis() - (selectedOffsetDays.toLong() * 24 * 60 * 60 * 1000L)
                            onConfirmFertilizing(plant.id, targetTime, fertIntervalDays)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("fertilize_log_confirm"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Save Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ==================== SUBTAB: GEMS WISHLIST SCREEN ====================

@Composable
fun MyWishlistScreen(
    wishlistPlants: List<com.example.data.PlantEntity>,
    onCardClick: (com.example.data.PlantEntity) -> Unit,
    onPromote: (com.example.data.PlantEntity) -> Unit,
    onDelete: (com.example.data.PlantEntity) -> Unit
) {
    val context = LocalContext.current

    if (wishlistPlants.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("wishlist_empty_state"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("✨", fontSize = 36.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Wishlist is Empty",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Discover rare houseplants in the Botanical Encyclopedia tab and hit \"Add to Wishlist\" to curate your dream garden!",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("wishlist_plants_list"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💫 Dream Collection Wishlist",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Track target flora. Click 'Move to Garden' to transition acquired species into active care management.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            items(wishlistPlants) { plant ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCardClick(plant) }
                        .testTag("wishlist_item_${plant.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plant.commonName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = plant.scientificName,
                                    fontSize = 11.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = {
                                    onDelete(plant)
                                    Toast.makeText(context, "🗑️ Removed ${plant.commonName} from Wishlist.", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp).testTag("wishlist_delete_${plant.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete from wishlist",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (plant.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = plant.description,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                lineHeight = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🔆 Sunlight", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(plant.sunlightRequirements, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("💧 Cycle", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Every ${plant.wateringIntervalDays} days", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                onPromote(plant)
                                Toast.makeText(context, "🎉 ${plant.commonName} is now in your active living garden!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("wishlist_promote_${plant.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("🏡 Move to Garden", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ==================== SUBTAB: LOCAL NURSERY FINDER SCREEN ====================

data class NurseryModel(
    val name: String,
    val city: String,
    val rating: Float,
    val address: String,
    val phone: String,
    val distanceMiles: Float,
    val signaturePlants: String,
    val coordinateX: Float,
    val coordinateY: Float
)

@Composable
fun GardenNurseryFinderScreen(
    modifier: Modifier = Modifier,
    nurseryQuery: String,
    onQueryChange: (String) -> Unit
) {
    val context = LocalContext.current
    var activeCitySelection by remember { mutableStateOf("All") }

    val fullNurseriesList = remember {
        listOf(
            NurseryModel("Chelsea Flora & Botanical Station", "London", 4.9f, "84 King's Rd, Chelsea, London SW3 4TZ", "+44 20 7730 0411", 0.4f, "Ferns, Swiss Cheese Plants, Soil Composites", 150f, 130f),
            NurseryModel("Clifton Gardens Nursery Centre", "London", 4.8f, "5A Clifton Villas, Little Venice, London W9 2PH", "+44 20 7289 6851", 1.8f, "Variegated Monstera, Boston Fern, Orchid Flora", 300f, 220f),
            NurseryModel("Kew Gardens Canopy Shop", "London", 4.7f, "Richmond, Kew, West London TW9 3AB", "+44 20 8332 5000", 3.2f, "Bonsai Trees, Pitcher Plants, Garden Shrubs", 80f, 280f),
            
            NurseryModel("Chelsea Garden Center (NY)", "New York", 4.8f, "444 W 20th St, New York, NY 10011", "+1 (212) 929-2477", 0.6f, "Snake Plants, Fiddle-Fig Trees, Clay Terracotta", 210f, 160f),
            NurseryModel("The Sill Greenhouse Emporium", "New York", 4.6f, "190 Elizabeth St, New York, NY 10012", "+1 (646) 899-4042", 1.2f, "Parlor Palms, Pothos Vines, Indoor Fertilizer", 120f, 240f),
            
            NurseryModel("Flora Grubb Botanical Gardens", "San Francisco", 4.9f, "1634 Jerrold Ave, San Francisco, CA 94124", "+1 (415) 626-7256", 0.8f, "Succulents, Tillandsias, Desert Cacti, Soil", 250f, 100f),
            NurseryModel("Clement Houseplant Nursery", "San Francisco", 4.5f, "2050 Clement St, San Francisco, CA 94121", "+1 (415) 750-3945", 2.3f, "Calathea Geometra, Peperomia, Organic Mulch", 90f, 180f),
            
            NurseryModel("Tokyo Bonsai & Botanical Center", "Tokyo", 4.9f, "1 Chome-3-1 Tokiwadai, Itabashi City, Tokyo", "+81 3-3960-5511", 1.4f, "Traditional Bonsai, Japanese Ferns, Sand Clay", 180f, 290f),
            NurseryModel("Sydney Greenhouse Oasis", "Sydney", 4.7f, "Elizabeth St, Sydney NSW 2000, Australia", "+61 2 9231 8111", 2.1f, "Wildflowers, Hanging Pothos, Hanging Rope", 270f, 310f)
        )
    }

    val filteredNurseries = remember(nurseryQuery, activeCitySelection) {
        fullNurseriesList.filter { nursery ->
            val matchesCity = activeCitySelection == "All" || nursery.city.equals(activeCitySelection, ignoreCase = true)
            val matchesQuery = nurseryQuery.isEmpty() ||
                    nursery.name.contains(nurseryQuery, ignoreCase = true) ||
                    nursery.address.contains(nurseryQuery, ignoreCase = true) ||
                    nursery.signaturePlants.contains(nurseryQuery, ignoreCase = true)
            matchesCity && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("nursery_finder_screen_layout"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcoming card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🗺️ Local Nursery Scout",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Search certified botanical centers, specialty garden stores, and nurseries to pick up soil, fertilizer, or expand your collection.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
        }

        // Location Search Input
        OutlinedTextField(
            value = nurseryQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Search location, store name or flora items...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Default.Place, contentDescription = "Place finder", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (nurseryQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("nursery_search_input")
        )

        // City Filters horizontal selection list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "London", "New York", "San Francisco", "Tokyo", "Sydney").forEach { cityName ->
                val isSelected = activeCitySelection == cityName
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .clickable { activeCitySelection = cityName }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("nursery_city_chip_${cityName.lowercase().replace(" ", "_")}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cityName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Vector Local Map Drawing (Acts as a visual Radar)
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color(0xFFE8F5E9)) // Light eco radar background green
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw grid lines
                        val stepX = size.width / 10f
                        val stepY = size.height / 6f
                        for (i in 1..10) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.7f),
                                start = androidx.compose.ui.geometry.Offset(i * stepX, 0f),
                                end = androidx.compose.ui.geometry.Offset(i * stepX, size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (i in 1..6) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.7f),
                                start = androidx.compose.ui.geometry.Offset(0f, i * stepY),
                                end = androidx.compose.ui.geometry.Offset(size.width, i * stepY),
                                strokeWidth = 1f
                            )
                        }

                        // Draw a mock lake segment
                        drawCircle(
                            color = Color(0xFFBBDEFB),
                            radius = 42f,
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.75f, size.height * 0.4f)
                        )

                        // Draw current location marker (centered)
                        drawCircle(
                            color = Color(0xFF2196F3),
                            radius = 6f,
                            center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                        )

                        // Draw pulsing light radar ring
                        drawCircle(
                            color = Color(0xFF2196F3).copy(alpha = 0.15f),
                            radius = 28f,
                            center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                        )

                        // Draw pin marks of active/filtered nurseries
                        filteredNurseries.forEach { nursery ->
                            // Scale coordinates slightly to fit the canvas dimensions nicely
                            val plotX = (nursery.coordinateX * 2.2f) % size.width
                            val plotY = (nursery.coordinateY * 1.5f) % size.height

                            // Draw pin dot
                            drawCircle(
                                color = Color(0xFFE57373),
                                radius = 5f,
                                center = androidx.compose.ui.geometry.Offset(plotX, plotY)
                            )
                            drawCircle(
                                color = Color(0xFFE57373).copy(alpha = 0.3f),
                                radius = 10f,
                                center = androidx.compose.ui.geometry.Offset(plotX, plotY),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                            )
                        }
                    }

                    // Radar HUD text Overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "📡 Botanical Finder Active Location: GPS Live",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${filteredNurseries.size} Found",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Scrollable local list results
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("nursery_locations_results_list"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredNurseries.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🔍", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No local garden centers match", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Try clearing search or testing other cities on quick filter list.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(filteredNurseries) { nursery ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("nursery_card_${nursery.name.replace(" ", "_")}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Title & distance row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = nursery.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "★ ${nursery.rating}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFF57C00)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = nursery.city,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${nursery.distanceMiles} mi",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "📍 Address: ${nursery.address}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "🌿 Specialty Supplies: ${nursery.signaturePlants}",
                                fontSize = 10.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "📞 Initiating call to: ${nursery.phone}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("📞 Contact Store", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(context, "🗺️ Directions unlocked for: ${nursery.name} (${nursery.address})", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("🗺️ Map Route", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
