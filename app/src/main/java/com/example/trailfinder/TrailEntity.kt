package com.example.trailfinder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trails")
data class TrailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val lat: Double,
    val lng: Double,
    val distance: Double,
    val difficulty: String,
    val notes: String? = null,
    val photoUri: String? = null // optional photo path or URI
)