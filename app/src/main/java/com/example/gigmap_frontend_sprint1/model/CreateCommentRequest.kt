package com.example.gigmap_frontend_sprint1.model

data class CreateCommentRequest(
    val threadId: Int,
    val userId: Int,
    val content: String
)
