package com.example.trailfinder.data.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ReviewDao {

    @Query("SELECT COUNT(*) FROM reviews WHERE userId = :userId")
    suspend fun getReviewCountByUser(userId: Int): Int
}