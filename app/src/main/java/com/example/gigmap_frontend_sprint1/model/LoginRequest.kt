package com.example.gigmap_frontend_sprint1.model

data class LoginRequest(
    val emailOrUsername: String,
    val password: String
)