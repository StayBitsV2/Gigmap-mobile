package com.example.gigmap_frontend_sprint1.services.m1au.dto

data class M1AUChatResponseDto(
    val message: String,
    val concerts: List<M1AUConcertDto> = emptyList(),
    val filters: Map<String, String> = emptyMap(),
    val meta: M1AUMetaDto = M1AUMetaDto()
)

data class M1AUConcertDto(
    val id: String,
    val title: String,
    val description: String?,
    val genre: String?,
    val status: String?,
    val datehour: String?,
    val imageUrl: String?,
    val formattedDate: String?,
    val artist: M1AUArtistDto?,
    val venue: M1AUVenueDto?,
    val platform: M1AUPlatformDto?
)

data class M1AUArtistDto(
    val id: String?,
    val name: String?,
    val username: String?
)

data class M1AUVenueDto(
    val id: String?,
    val name: String?,
    val address: String?
)

data class M1AUPlatformDto(
    val id: String?,
    val name: String?
)

data class M1AUMetaDto(
    val total: Int = 0,
    val hasResults: Boolean = false
)
