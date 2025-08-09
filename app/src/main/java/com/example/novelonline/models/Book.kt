package com.example.novelonline.models

import com.google.firebase.firestore.DocumentId
import java.util.Date

// UPDATED: Changed Date fields back to String for simplicity and to match fragment code.
data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val authorId: String = "",
    val coverImageUrl: String? = null,
    val chapterCount: Int = 0,
    val lastUpdated: Date = Date(),
    val createdOn: Date = Date(),
    val language: String? = null,
    val bookType: String? = null,
    val genres: List<String> = emptyList(),
    val tags: List<String>? = null,
    val length: String? = null,
    val pdfUrl: String? = null,
    val synopsis: String? = null,
    val warningNotice: String? = null,
    val sourceCollection: String = ""
)