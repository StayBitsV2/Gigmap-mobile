package com.example.gigmap_frontend_sprint1.model

data class ConcertCreateRequest(
    val title: String,
    val description: String,
    val imageUrl: String,
    val date: String,
    val genre: String,
    val status: String = "PUBLICADO",
    val platform: PlatformRequest,
    val venue: VenueRequest,
    val userId: Int,
    val attendees: List<Int> = emptyList()
)
