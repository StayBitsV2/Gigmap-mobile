package com.example.gigmap_frontend_sprint1.model

import com.google.gson.annotations.SerializedName

data class UserEditRequest(
    val email: String? = null,
    val username: String? = null,
    val name: String? = null,
    val role: String? = null,
    val imagenUrl: String? = null,
    val descripcion: String? = null,
    @SerializedName("bannerUrl") val bannerUrl: String? = null,
    @SerializedName("generoMusical") val generoMusical: String? = null,
    @SerializedName("sitioWeb") val sitioWeb: String? = null,
    @SerializedName("spotifyUrl") val spotifyUrl: String? = null,
    @SerializedName("instagramUrl") val instagramUrl: String? = null,
    @SerializedName("youtubeUrl") val youtubeUrl: String? = null
)
