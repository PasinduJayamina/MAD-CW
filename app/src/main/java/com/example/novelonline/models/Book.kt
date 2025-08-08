package com.example.novelonline.models

import java.util.Date

data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "", // Assuming you'll add the author later
    val authorId: String = "",
    val coverImageUrl: String? = null,
    val chapterCount: Int = 0,
    val lastUpdated: Date = Date(),
    val createdOn: Date = Date(),
    // New fields
    val language: String? = null,
    val bookType: String? = null,
    val genres: List<String> = emptyList()
)