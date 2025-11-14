package com.example.trailfinder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ratings")
data class RatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trailId: Int,               // FK to TrailEntity.id
    val stars: Int,                 // 1–5
    val tags: String? = null,       // e.g. "Scenic,Steep"
    val comment: String? = null,    // free-text comment
    val timestamp: Long = System.currentTimeMillis()
)