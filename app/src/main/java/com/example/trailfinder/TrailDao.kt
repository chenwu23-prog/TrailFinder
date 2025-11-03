package com.example.trailfinder

@Dao
interface TrailDao {

    @Query("SELECT * FROM trails")
    suspend fun getAllTrails(): List<TrailEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trails: List<TrailEntity>)
}