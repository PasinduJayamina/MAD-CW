package com.example.novelonline.models // Adjust package as needed

data class Chapter(
    val id: String,
    val bookId: String, // Link to the book it belongs to
    val title: String,
    val savedDate: String, // Or use a Date/Timestamp type
    val wordCount: Int
)