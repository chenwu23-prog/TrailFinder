
package edu.vt.trailreview.util

import android.content.Context
import org.json.JSONArray

data class TrailSeed(
    val name: String,
    val distanceKm: Double,
    val difficulty: String,
    val lat: Double,
    val lng: Double
)

object JsonLoader {
    fun loadTrails(context: Context): List<TrailSeed> {
        val input = context.assets.open("trails.json")
        val text = input.bufferedReader().use { it.readText() }
        val arr = JSONArray(text)
        val out = mutableListOf<TrailSeed>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out += TrailSeed(
                name = o.getString("name"),
                distanceKm = o.getDouble("distanceKm"),
                difficulty = o.getString("difficulty"),
                lat = o.getDouble("lat"),
                lng = o.getDouble("lng")
            )
        }
        return out
    }
}
