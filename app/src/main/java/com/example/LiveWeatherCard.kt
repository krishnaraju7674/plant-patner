package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.PlantViewModel
import com.example.viewmodel.WeatherState

@Composable
fun LiveWeatherCard(
    viewModel: PlantViewModel,
    modifier: Modifier = Modifier
) {
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    var searchCityInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_weather_container"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CanvasSunIcon(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.primary)
                    }
                    Column {
                        Text(
                            text = "Live Gardening climate",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Local environment triggers guidelines",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Inline quick geocode city search
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = searchCityInput,
                        onValueChange = { searchCityInput = it },
                        placeholder = { Text("Search city...", fontSize = 9.sp) },
                        modifier = Modifier
                            .width(110.dp)
                            .height(38.dp)
                            .testTag("weather_city_search_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 10.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = {
                                if (searchCityInput.trim().isNotEmpty()) {
                                    viewModel.fetchWeather(searchCityInput.trim())
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    )
                    IconButton(
                        onClick = {
                            if (searchCityInput.trim().isNotEmpty()) {
                                viewModel.fetchWeather(searchCityInput.trim())
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                            .testTag("btn_weather_search")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Find local weather",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            when (val state = weatherState) {
                is WeatherState.Idle -> {
                    Text("Search a city to contextually verify environmental safety tags.", fontSize = 10.sp)
                }
                is WeatherState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Connecting to Open-Meteo API forecasts...", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                is WeatherState.Error -> {
                    Text(
                        text = "Forecast unreachable: ${state.message}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
                is WeatherState.Success -> {
                    val temp = state.temperature
                    val hum = state.humidity
                    val city = state.cityName

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "📍 $city",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Real-time satellite coordinates",
                                fontSize = 8.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // TEMP METRIC
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌡️", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Column {
                                    Text("TEMP", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$temp°C", fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            // HUMIDITY METRIC
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💧", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Column {
                                    Text("HUMIDITY", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$hum%", fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    // Strategic botanical recommendation Advice Strip
                    val recommendation = when {
                        hum < 40.0 -> "⚠️ Dry Ambient Humidity ($hum%): Mist tropical foliage or use pebble water trays beneath your species."
                        temp < 15.0 -> "❄️ Cold Atmosphere alert ($temp°C): Bring delicate warmth-loving seedlings inside to protect root health."
                        temp > 30.0 -> "🔥 Accelerated Evaporation ($temp°C): Rapid soil dry-out detected. Verify water saturation depth daily."
                        else -> "🌱 Optimal Conditions: Local climate coordinates perfectly sync with balanced botanical requirements."
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = recommendation,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
