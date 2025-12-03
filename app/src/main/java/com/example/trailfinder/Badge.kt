package com.example.trailfinder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey(autoGenerate = true) val badgeId: Int = 0,
    val badgeName: String,
    val badgeDescription: String
)