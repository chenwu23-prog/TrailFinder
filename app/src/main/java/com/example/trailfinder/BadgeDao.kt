package com.example.trailfinder.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BadgeDao {


    @Query("SELECT * FROM badges WHERE badgeId IN (:ids)")
    suspend fun getBadgeById(ids: Int): Badge
}