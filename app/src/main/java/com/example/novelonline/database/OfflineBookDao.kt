package com.example.novelonline.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OfflineBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: OfflineBook)

    @Query("SELECT * FROM offline_books WHERE bookId = :id")
    suspend fun getOfflineBook(id: String): OfflineBook?

    @Query("DELETE FROM offline_books WHERE bookId = :id")
    suspend fun delete(id: String)
}