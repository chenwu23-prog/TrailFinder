package com.example.trailfinder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RatingDao {

    @Insert
    suspend fun insertRating(rating: RatingEntity)

    @Query("SELECT * FROM ratings WHERE trailId = :trailId ORDER BY timestamp DESC")
    suspend fun getRatingsForTrail(trailId: Int): List<RatingEntity>

    @Query("SELECT AVG(stars) FROM ratings WHERE trailId = :trailId")
    suspend fun getAverageRating(trailId: Int): Double?

    @Query("SELECT COUNT(*) FROM ratings WHERE trailId = :trailId")
    suspend fun getRatingCount(trailId: Int): Int
}