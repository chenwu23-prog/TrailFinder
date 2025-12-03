package com.example.trailfinder.data.repository

import androidx.room.Query
import com.example.trailfinder.data.dao.ReviewDao

class ReviewRepository(
    private val reviewDao: ReviewDao
) {
    @Query("SELECT COUNT(*) FROM reviews WHERE userId = :userId")
    suspend fun getReviewCountByUser(userId: Int): Int {
        return reviewDao.getReviewCountByUser(userId)
    }

}