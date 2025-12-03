package com.example.trailfinder.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "user_badges",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userOwnerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Badge::class,
            parentColumns = ["badgeId"],
            childColumns = ["badgeOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserBadge(
    @PrimaryKey(autoGenerate = true) val userBadgeId: Int = 0,
    val userOwnerId: Int,
    val badgeOwnerId: Int
)