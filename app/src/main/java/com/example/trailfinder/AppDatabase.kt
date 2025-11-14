package com.example.trailfinder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TrailEntity::class,
        UserEntity::class,
        RatingEntity::class
    ],
    version = 3,            // keep version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trailDao(): TrailDao
    abstract fun userDao(): UserDao
    abstract fun ratingDao(): RatingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trailfinder.db"
                )
                    // IMPORTANT: remove this so the DB stops resetting
                    // .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}