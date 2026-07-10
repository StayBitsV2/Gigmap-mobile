package com.example.gigmap_frontend_sprint1.model

data class CreateThreadRequest(
    val title: String,
    val content: String,
    val imageUrl: String = "",
    val communityId: Int,
    val userId: Int
)
