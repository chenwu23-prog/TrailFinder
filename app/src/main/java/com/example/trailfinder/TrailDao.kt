package com.example.trailfinder

import androidx.room.*

@Dao
interface TrailDao {
    @Query("SELECT * FROM trails")
    fun getAllTrails(): List<TrailEntity>

    @Query("SELECT * FROM trails WHERE id = :id")
    fun getTrailById(id: Int): TrailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trails: List<TrailEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrail(trail: TrailEntity)

    @Delete
    suspend fun deleteTrail(trail: TrailEntity)

    @Query("DELETE FROM trails")
    suspend fun clearAll()


}
