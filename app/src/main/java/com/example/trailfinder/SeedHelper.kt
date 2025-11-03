package com.example.trailfinder

package com.example.trailfinder.data

import android.content.Context
import com.example.trailfinder.util.JsonLoader
import com.example.trailfinder.data.model.TrailEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SeedHelper {
    suspend fun seedDatabaseIfEmpty(context: Context) = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)
        val dao = db.trailDao()
        if (dao.getAllTrails().isEmpty()) {
            val trails = JsonLoader.loadTrails(context).map {
                TrailEntity(it.id, it.name, it.lat, it.lng, it.notes)
            }
            dao.insertAll(trails)
        }
    }
}
