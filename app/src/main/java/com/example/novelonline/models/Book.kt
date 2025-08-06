package com.example.novelonline.models

data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val coverImageUrl: String = "",
    val chapterCount: Int = 0,
    val lastUpdated: String = "",
    val createdOn: String = ""
)