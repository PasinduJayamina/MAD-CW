package com.example.novelonline.models

data class User(
    val id: Int,
    val email: String,
    val password: String,
    val role: String,
    val name: String = "",
    val username: String = ""
)
