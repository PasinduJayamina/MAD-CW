package com.example.novelonline.models

data class BookFair(
    val name: String,
    val address: String,
    val contact: String,
    val startDate: String,
    val endDate: String,
    val latitude: Double,
    val longitude: Double,
    var straightLineDistanceKm: Float? = null, // Renamed for clarity
    var drivingDistance: String? = null // To store the formatted driving distance text
)