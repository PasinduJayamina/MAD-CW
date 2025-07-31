package com.example.novelonline.models // Adjust package as needed

data class Book(
    val id: String,
    val coverUrl: String?, // URL to the book cover image
    val title: String,
    val chapterCount: Int,
    val lastUpdated: String, // Or use a Date/Timestamp type
    val createdOn: String // Or use a Date/Timestamp type
)