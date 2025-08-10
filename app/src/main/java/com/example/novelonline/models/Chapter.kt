package com.example.novelonline.models // Adjust package as needed

data class Chapter(
    val id: String,
    val novelId: String = "",
    val bookId: String,
    val title: String,
    val savedDate: String,
    val createdOn: String = "",
    val wordCount: Int
)