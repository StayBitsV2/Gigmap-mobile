package com.example.gigmap_frontend_sprint1.model

data class Venue(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int
)