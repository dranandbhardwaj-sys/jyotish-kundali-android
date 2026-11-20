package com.example.data.remote

import com.example.model.BirthPlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.math.roundToInt

/**
 * Live worldwide birthplace geocoding client using Open-Meteo Geocoding API
 * (no API key required) to resolve city name, latitude, longitude, and timezone offset.
 */
object GeocodingService {
    private const val BASE_URL = "https://geocoding-api.open-meteo.com/v1/search"

    sealed class SearchResult {
        data class Success(val places: List<BirthPlace>) : SearchResult()
        data class Error(val message: String) : SearchResult()
    }

    suspend fun searchCityOnline(query: String): SearchResult = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return@withContext SearchResult.Success(emptyList())

        var connection: HttpURLConnection? = null
        try {
            val encoded = URLEncoder.encode(trimmed, "UTF-8")
            val url = URL("$BASE_URL?name=$encoded&count=12&language=en&format=json")
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("Accept", "application/json")
            }

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext SearchResult.Error("HTTP ${connection.responseCode}")
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)
            val results = json.optJSONArray("results") ?: return@withContext SearchResult.Success(emptyList())

            val places = buildList {
                for (i in 0 until results.length()) {
                    val item = results.getJSONObject(i)
                    val id = item.optLong("id", i.toLong()).toString()
                    val name = item.optString("name", "")
                    val admin1 = item.optString("admin1", "")
                    val country = item.optString("country", "")
                    val lat = item.optDouble("latitude", Double.NaN)
                    val lon = item.optDouble("longitude", Double.NaN)
                    if (name.isBlank() || lat.isNaN() || lon.isNaN()) continue

                    val region = listOf(admin1, country).filter { it.isNotBlank() }.joinToString(", ")
                    // Estimate standard timezone offset from longitude if tz offset isn't returned directly
                    // (Open-Meteo geocoding returns IANA timezone string; Android TimeZone resolves exact standard offset!)
                    val tzId = item.optString("timezone", "")
                    val tzOffsetHours = resolveTimezoneOffsetHours(tzId, lon)

                    add(
                        BirthPlace(
                            id = "geo_$id",
                            nameHi = name,
                            nameEn = name,
                            stateHi = region,
                            stateEn = region,
                            latitude = lat,
                            longitude = lon,
                            timezoneOffsetHours = tzOffsetHours,
                            isCustom = false
                        )
                    )
                }
            }
            SearchResult.Success(places)
        } catch (e: Exception) {
            SearchResult.Error(e.message ?: "Network error")
        } finally {
            connection?.disconnect()
        }
    }

    private fun resolveTimezoneOffsetHours(tzId: String, lon: Double): Double {
        if (tzId.isNotBlank()) {
            val tz = java.util.TimeZone.getTimeZone(tzId)
            if (tz.id == tzId) {
                return tz.rawOffset / 3_600_000.0
            }
        }
        // Fallback nearest 0.5h meridian estimate
        return ((lon / 15.0) * 2.0).roundToInt() / 2.0
    }
}
