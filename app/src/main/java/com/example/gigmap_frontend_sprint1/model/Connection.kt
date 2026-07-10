package com.example.gigmap_frontend_sprint1.model

data class ConnectionRequestResource(
    val id: Long,
    val requesterId: Long,
    val targetId: Long,
    val status: String,
    val createdAt: String? = null
)

data class ConnectionResource(
    val id: Long,
    val connectedUserId: Long,
    val connectedUsername: String?,
    val connectedUserImage: String?,
    val createdAt: String? = null
)

data class CreateConnectionRequest(
    val targetId: Long
)
