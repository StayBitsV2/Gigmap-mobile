package com.example.gigmap_frontend_sprint1.model

data class Report(
    val id: Int,
    val reason: String,
    val threadId: Int?,
    val commentId: Int?,
    val reporterId: Int,
    val status: String,
    val createdAt: String
)
