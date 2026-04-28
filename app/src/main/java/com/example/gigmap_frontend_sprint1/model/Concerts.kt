package com.example.gigmap_frontend_sprint1.model


data class Concerts(
    val id: Int? = null,
    val name: String,
    val date: String,
    val status: String,
    val description: String,
    val image: String,
    val genre: String,
    val platform: Platform,
    val venue: Venue,
    val attendees: List<Int> = emptyList()
)