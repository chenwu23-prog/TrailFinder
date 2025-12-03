package com.example.trailfinder.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val reviewId: Int = 0,
    val userId: Int,
    val trailId: Int,
    val rating: Int,
    val comment: String,
    val createdAt: Long
)