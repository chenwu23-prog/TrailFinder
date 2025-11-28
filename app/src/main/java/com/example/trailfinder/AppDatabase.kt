package com.example.trailfinder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        TrailEntity::class,
        UserEntity::class,
        RatingEntity::class
    ],
    version = 4,
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

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trailfinder.db"
                )

                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}