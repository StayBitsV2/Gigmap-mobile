package com.example.gigmap_frontend_sprint1.model

data class RelatedEventCreateRequest(
    val concertId: Int,
    val titulo: String,
    val datehour: String,
    val descripcion: String,
    val tipo: String,
    val venue: Venue,
    val status: String,
    val organizadorId: Int
)
