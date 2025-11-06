package com.example.trailfinder

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET viewedTrails = :trails WHERE id = :id")
    suspend fun updateViewedTrails(id: Int, trails: String)
}