package com.example.gigmap_frontend_sprint1.model

data class Notification(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String,
    val createdAt: String,
    val readAt: String?
)
