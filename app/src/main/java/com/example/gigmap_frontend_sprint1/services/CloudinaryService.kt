package com.example.gigmap_frontend_sprint1.services


import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

object CloudinaryService {
    // ✅ CREDENCIALES CONFIGURADAS
    private const val CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/dngk5bc3p/image/upload"
    private const val UPLOAD_PRESET = "cloudinary_staymap"

    fun uploadImage(imageFile: File): String? {
        val client = OkHttpClient()
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .addFormDataPart("file", imageFile.name, imageFile.asRequestBody())
            .build()

        val request = Request.Builder()
            .url(CLOUDINARY_URL)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            val json = JSONObject(response.body!!.string())
            json.getString("secure_url") // devuelve la URL lista para guardar
        } else null
    }
}