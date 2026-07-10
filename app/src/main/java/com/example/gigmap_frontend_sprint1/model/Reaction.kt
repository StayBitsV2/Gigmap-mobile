package com.example.gigmap_frontend_sprint1.model

data class Reaction(
    val id: Int,
    val emoji: String,
    val userId: Int,
    val threadId: Int?,
    val commentId: Int?
)
