package com.example.novelonline.models // Adjust package as needed

data class Chapter(
    val id: String,
    val novelId: String = "",
    val bookId: String, // Link to the book it belongs to
    val title: String,
    val savedDate: String,
    val createdOn: String = "",// Or use a Date/Timestamp type
    val wordCount: Int
)