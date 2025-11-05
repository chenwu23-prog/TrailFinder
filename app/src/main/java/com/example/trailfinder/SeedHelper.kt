package com.example.trailfinder

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SeedHelper {
    suspend fun seedDatabaseIfEmpty(context: Context) {
        val db = AppDatabase.getDatabase(context)
        withContext(Dispatchers.IO) {
            if (db.trailDao().getAllTrails().isEmpty()) {
                val trails = listOf(
                    TrailEntity(
                        name = "Huckleberry Trail",
                        lat = 37.2108,
                        lng = -80.4089,
                        distance = 7.0,
                        difficulty = "Easy",
                        notes = "Popular paved trail connecting Blacksburg and Christiansburg."
                    ),
                    TrailEntity(
                        name = "Pandapas Pond Loop",
                        lat = 37.2803,
                        lng = -80.4751,
                        distance = 3.6,
                        difficulty = "Moderate",
                        notes = "Wooded loop trail around a scenic pond."
                    ),
                    TrailEntity(
                        name = "Brush Mountain Trail",
                        lat = 37.2619,
                        lng = -80.4500,
                        distance = 5.5,
                        difficulty = "Hard",
                        notes = "Steep hike with panoramic views."
                    )
                )
                db.trailDao().insertAll(trails)
            }
        }
    }
}
