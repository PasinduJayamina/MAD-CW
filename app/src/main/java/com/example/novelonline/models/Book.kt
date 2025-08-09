package com.example.novelonline.models

// UPDATED: Changed Date fields back to String for simplicity and to match fragment code.
data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val authorId: String = "",
    val coverImageUrl: String? = null,
    val chapterCount: Int = 0,
    val lastUpdated: String = "", // Changed from Date to String
    val createdOn: String = "",   // Changed from Date to String
    val language: String? = null,
    val bookType: String? = null,
    val genres: List<String> = emptyList(),
    val tags: List<String>? = null,
    val length: String? = null,
    val pdfUrl: String? = null,
    val synopsis: String? = null,
    val warningNotice: String? = null
)