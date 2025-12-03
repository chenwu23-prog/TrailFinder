package com.example.trailfinder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.trailfinder.data.UserBadge

@Dao
interface UserBadgeDao {
    @Insert suspend fun insertUserBadge(badge: UserBadge)

    @Query("SELECT * FROM user_badges WHERE userOwnerId = :userId")
    suspend fun getUserBadges(userId: Int): List<Int>
}