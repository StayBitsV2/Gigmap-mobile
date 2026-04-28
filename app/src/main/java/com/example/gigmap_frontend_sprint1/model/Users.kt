package com.example.gigmap_frontend_sprint1.model

data class Users (
    val id: Int,
    val email: String,
    val name: String,
    val username: String,
    val role: String,
    val descripcion: String,
    val image: String
)