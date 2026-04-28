package com.example.gigmap_frontend_sprint1.model

import com.google.gson.annotations.SerializedName

data class UserEditRequest(


    val email: String? = null,
    val username: String? = null,
    val name: String? = null,
    val role: String? = null,
    val imagenUrl: String? = null,
    val descripcion: String? = null
)
