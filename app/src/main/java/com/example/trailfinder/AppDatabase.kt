package com.example.trailfinder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trailfinder.data.Badge
import com.example.trailfinder.data.User
import com.example.trailfinder.data.UserBadge
import com.example.trailfinder.data.entities.Review

@Database(
    entities = [
        TrailEntity::class,
        UserEntity::class,
        RatingEntity::class,
        User::class,
        Badge::class,
        UserBadge::class,
        Review::class,
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)   // ← ADD THIS LINE
abstract class AppDatabase : RoomDatabase() {

    abstract fun trailDao(): TrailDao
    abstract fun userDao(): UserDao
    abstract fun ratingDao(): RatingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trailfinder.db"
                ).build().also { INSTANCE = it }
            }
        }
    }


}