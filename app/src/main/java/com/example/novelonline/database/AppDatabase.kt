package com.example.novelonline.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OfflineBook::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun offlineBookDao(): OfflineBookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "novel_online_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}