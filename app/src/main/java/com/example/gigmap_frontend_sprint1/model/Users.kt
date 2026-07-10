package com.example.gigmap_frontend_sprint1.model

import com.google.gson.annotations.SerializedName

data class Users (
    val id: Int,
    val email: String,
    val name: String,
    val username: String,
    val role: String,
    val descripcion: String,
    val image: String,
    @SerializedName("bannerUrl") val bannerUrl: String? = null,
    @SerializedName("generoMusical") val generoMusical: String? = null,
    @SerializedName("sitioWeb") val sitioWeb: String? = null,
    @SerializedName("spotifyUrl") val spotifyUrl: String? = null,
    @SerializedName("instagramUrl") val instagramUrl: String? = null,
    @SerializedName("youtubeUrl") val youtubeUrl: String? = null
)