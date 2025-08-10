package com.example.novelonline.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.novelonline.models.Book

@Entity(tableName = "offline_books")
data class OfflineBook(
    @PrimaryKey
    val bookId: String,
    val title: String,
    val author: String,
    val coverImageUrl: String,
    val localFilePath: String // The path to the downloaded PDF file
)

// A function to easily convert an online book to an offline book
fun Book.toOfflineBook(localFilePath: String): OfflineBook {
    return OfflineBook(
        bookId = this.id,
        title = this.title, // 'title' is already non-nullable
        author = this.author, // 'author' is already non-nullable
        coverImageUrl = this.coverImageUrl ?: "", // Handle nullable 'coverImageUrl'
        localFilePath = localFilePath
    )
}