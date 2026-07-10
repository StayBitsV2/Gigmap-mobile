package com.example.gigmap_frontend_sprint1.model

data class Comment(
    val id: Int,
    val threadId: Int,
    val userId: Int,
    val userName: String = "Usuario",
    val content: String,
    val createdAt: String? = null
)
