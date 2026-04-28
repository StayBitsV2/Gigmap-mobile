package com.example.gigmap_frontend_sprint1.model.m1au

data class M1AUBotReply(
    val message: String,
    val concerts: List<M1AUConcertCard>,
    val total: Int,
    val hasResults: Boolean
)

data class M1AUConcertCard(
    val id: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val formattedDate: String?,
    val artistId: String?,
    val artistName: String?,
    val artistUsername: String?,
    val venueName: String?,
    val venueAddress: String?,
    val platformName: String?
)
