package com.example.gigmap_frontend_sprint1.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

object GoogleMapsService {
    private const val API_KEY = "AIzaSyBFzoruFMnUrsFG8JeLHMzMlIS6P4Kr8_4"

    suspend fun getLatLngFromAddress(address: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val encoded = URLEncoder.encode(address, "UTF-8")
                val url =
                    "https://maps.googleapis.com/maps/api/geocode/json?address=$encoded&key=$API_KEY"

                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext null
                val json = JSONObject(body)

                if (json.getString("status") == "OK") {
                    val location =
                        json.getJSONArray("results").getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                    val lat = location.getDouble("lat")
                    val lng = location.getDouble("lng")
                    Pair(lat, lng)
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}