package com.example

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.api.SymptomCause
import com.example.api.TroubleshootingResponse
import com.example.viewmodel.PlantViewModel
import com.example.viewmodel.TroubleshootingState

@Composable
fun AITroubleshooterScreen(
    viewModel: PlantViewModel,
    modifier: Modifier = Modifier
) {
    val myGardenPlants by viewModel.allPlants.collectAsStateWithLifecycle()
    val troubleshootingState by viewModel.troubleshootingState.collectAsStateWithLifecycle()

    var selectedPlantName by remember { mutableStateOf("General Houseplant") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var symptomDescription by remember { mutableStateOf("") }

    // Predefined common symptom chips
    val symptomPresets = listOf(
        "🍂 Yellowing Leaves",
        "🥀 Drooping & Wilting",
        "🟤 Brown Leaf Tips",
        "⚪ White Spots / Mold",
        "⛔ Stunted Growth / No Leaves",
        "🕷️ Tiny Webbings / Pests"
    )

    // Remember checked status for immediate action checkpoints
    val checkedActions = remember { mutableStateMapOf<Int, Boolean>() }

    // Reset checkboxes when success response changes
    LaunchedEffect(troubleshootingState) {
        if (troubleshootingState is TroubleshootingState.Success) {
            checkedActions.clear()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("ai_troubleshooter_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming card description
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Diagnostic icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "AI Symptom Troubleshooter",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Select a plant and specify visual symptoms to analyze the issue & receive expert horticultural rescue steps.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }
        }

        // Section 1: Plant Picker
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "1. Select Affected Plant",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { dropdownExpanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("troubleshoot_plant_dropdown_trigger"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedPlantName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    DropdownMenuItem(
                        text = { Text("🌿 General Houseplant", fontSize = 13.sp) },
                        onClick = {
                            selectedPlantName = "General Houseplant"
                            dropdownExpanded = false
                        }
                    )
                    myGardenPlants.forEach { plant ->
                        DropdownMenuItem(
                            text = { Text("🪴 ${plant.commonName}", fontSize = 13.sp) },
                            onClick = {
                                selectedPlantName = plant.commonName
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Section 2: Preset Symptom Chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "2. Quick Symptom Presets",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                symptomPresets.forEach { preset ->
                    val isChecked = symptomDescription.contains(preset.substring(2))
                    FilterChip(
                        selected = isChecked,
                        onClick = {
                            val cleanName = preset.substring(2)
                            if (isChecked) {
                                symptomDescription = symptomDescription.replace(cleanName, "").replace(", ,", ",").trim(' ', ',', ';')
                            } else {
                                if (symptomDescription.isNotEmpty()) {
                                    symptomDescription += ", $cleanName"
                                } else {
                                    symptomDescription = cleanName
                                }
                            }
                        },
                        label = { Text(preset, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Section 3: Detailed Description Box
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "3. Describe Symptoms in Detail",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = symptomDescription,
                onValueChange = { symptomDescription = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("troubleshoot_symptom_input"),
                placeholder = {
                    Text(
                        text = "Describe how the plant looks (e.g. drooping leaves, dry brown edges, powdery white spots under leaves, sticky residue, slow growth)...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        // Section 4: Action Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (symptomDescription.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        symptomDescription = ""
                        viewModel.resetTroubleshooting()
                    },
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear", fontSize = 12.sp)
                }
            }

            Button(
                onClick = {
                    viewModel.diagnoseSymptoms(symptomDescription, selectedPlantName)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_trigger_troubleshoot"),
                enabled = symptomDescription.isNotBlank() && troubleshootingState !is TroubleshootingState.Diagnosing,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search diagnoses",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (troubleshootingState is TroubleshootingState.Diagnosing) "Analyzing symptoms..." else "Diagnose Issues",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Diagnostic Loading or Render Container
        when (val state = troubleshootingState) {
            is TroubleshootingState.Idle -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Pending prompt",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Awaiting Symptom Input",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Fill in symptoms above and tap 'Diagnose Issues' to activate AI diagnosis.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is TroubleshootingState.Diagnosing -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Consulting AI Horticulture Expert...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Analyzing hydration schedules, leaf symptoms, and environment profiles. This may take up to 20 seconds...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is TroubleshootingState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error notification",
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Diagnostic Call Failed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.message,
                            fontSize = 11.sp,
                            color = Color(0xFFB71C1C),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.diagnoseSymptoms(symptomDescription, selectedPlantName) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Retry Diagnostic", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            is TroubleshootingState.Success -> {
                val report = state.response
                AIReportView(
                    report = report,
                    checkedActions = checkedActions
                )
            }
        }
    }
}

@Composable
fun AIReportView(
    report: TroubleshootingResponse,
    checkedActions: MutableMap<Int, Boolean>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .testTag("ai_troubleshoot_report_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title & Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DIAGNOSTIC REPORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = report.plantCommonName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val confidenceColor = when (report.confidenceRating.lowercase()) {
                    "high", "90%", "85%", "95%" -> Color(0xFF2E7D32)
                    "low" -> Color(0xFFC62828)
                    else -> Color(0xFFEF6C00)
                }
                Box(
                    modifier = Modifier
                        .background(confidenceColor.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                        .border(1.dp, confidenceColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Confidence: ${report.confidenceRating}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = confidenceColor
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))

            // Clinical Summary Box
            Column {
                Text(
                    text = "AI Medical Assessment",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = report.diagnosisSummary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Possible Causes Stack
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Potential Underlying Causes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                report.possibleCauses.forEach { cause ->
                    val severityColor = when (cause.severity.lowercase()) {
                        "high" -> Color(0xFFD32F2F)
                        "medium" -> Color(0xFFF57C00)
                        else -> Color(0xFF388E3C)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, severityColor.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cause.cause,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Box(
                                    modifier = Modifier
                                        .background(severityColor.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${cause.severity} Risk",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = severityColor
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cause.explanation,
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                            )
                        }
                    }
                }
            }

            // Grid: Hydration & Light adjustment advice
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Hydration
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CanvasWaterDropIcon(modifier = Modifier.size(12.dp), color = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Water Action",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.wateringAdjustment,
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            color = Color(0xFF0D47A1)
                        )
                    }
                }

                // Sunlight
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CanvasSunIcon(modifier = Modifier.size(12.dp), color = Color(0xFFFFB300))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Solar Exposure",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF57F17)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.lightAdjustment,
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            // Interactive Treatment Checklist
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Immediate First-Aid Steps (Checkoff)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                report.immediateActions.forEachIndexed { index, action ->
                    val isChecked = checkedActions[index] ?: false
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                                else Color.Transparent
                            )
                            .clickable { checkedActions[index] = !isChecked }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checkedActions[index] = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = action,
                            fontSize = 11.sp,
                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                            color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Long Term Prevention
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Long-Term Preventative Care",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                report.preventativeCareList.forEach { care ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = care,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
