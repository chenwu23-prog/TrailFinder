package com.example.trailfinder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val avatarUri: String? = null, // URI or asset reference
    val viewedTrails: String? = null // comma-separated trail IDs or JSON string
)