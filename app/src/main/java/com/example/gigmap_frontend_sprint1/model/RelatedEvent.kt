package com.example.gigmap_frontend_sprint1.model

data class RelatedEvent(
    val id: Int?,
    val concertId: Int,
    val titulo: String,
    val datehour: String,
    val descripcion: String,
    val tipo: String,
    val venue: Venue,
    val status: String,
    val organizadorId: Int,
    val participantes: List<Int> = emptyList()
)
