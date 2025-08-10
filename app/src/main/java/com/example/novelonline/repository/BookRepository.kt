package com.example.novelonline.repository

import android.util.Log
import com.example.novelonline.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object BookRepository {

    private val booksCollection = FirebaseFirestore.getInstance().collection("books")

    // Your existing function to get all books
    suspend fun getBooks(): List<Book> {
        return try {
            val result = booksCollection
                .orderBy("lastUpdated", Query.Direction.DESCENDING)
                .get()
                .await()

            result.documents.map { document ->
                val bookData = document.toObject(Book::class.java) ?: Book()
                val authorName = UserRepository.getUserName(bookData.authorId)
                bookData.copy(id = document.id, author = authorName)
            }
        } catch (exception: Exception) {
            Log.e("BookRepository", "Error getting books", exception)
            emptyList()
        }
    }

    // This function fetches a single book by its document ID
    suspend fun getBookById(novelId: String): Book? {
        return try {
            val document = booksCollection.document(novelId).get().await()
            val bookData = document.toObject(Book::class.java)
            // Also fetch the author name for the single book view
            bookData?.let {
                val authorName = UserRepository.getUserName(it.authorId)
                it.copy(id = document.id, author = authorName)
            }
        } catch (e: Exception) {
            Log.e("BookRepository", "Error getting book by ID: $novelId", e)
            null
        }
    }
}