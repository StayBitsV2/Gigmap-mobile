package com.example.gigmap_frontend_sprint1.model

data class Post(
    val id: Int,
    val communityId: Int,
    val userId: Int,
    val content: String,
    val image: String?,
    val likes: List<Int> = emptyList()
)
