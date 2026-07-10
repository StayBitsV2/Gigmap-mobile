package com.example.gigmap_frontend_sprint1.model

data class CreateReactionRequest(
    val threadId: Int? = null,
    val commentId: Int? = null,
    val userId: Int,
    val emoji: String
)
