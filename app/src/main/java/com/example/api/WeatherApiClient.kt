package com.example.api

import android.util.Log
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null
)

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val current: CurrentWeatherData?
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherData(
    val time: String,
    val temperature_2m: Double,
    val relative_humidity_2m: Double
)

interface WeatherApiService {
    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCity(
        @Query("name") cityName: String,
        @Query("count") count: Int = 1,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse

    @GET("https://api.open-meteo.com/v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") currentFields: String = "temperature_2m,relative_humidity_2m"
    ): WeatherResponse
}

object WeatherApiClient {
    private const val TAG = "WeatherApiClient"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val apiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }

    suspend fun fetchWeatherForCity(cityName: String): Pair<GeocodingResult, WeatherResponse>? = withContext(Dispatchers.IO) {
        try {
            val geocodingRes = apiService.searchCity(cityName)
            val firstResult = geocodingRes.results?.firstOrNull()
            if (firstResult != null) {
                val weatherRes = apiService.getWeather(firstResult.latitude, firstResult.longitude)
                return@withContext Pair(firstResult, weatherRes)
            } else {
                Log.e(TAG, "No geocoding result found for city: $cityName")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather for city: $cityName", e)
            return@withContext null
        }
    }
}
