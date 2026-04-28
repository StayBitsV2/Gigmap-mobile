package com.example.gigmap_frontend_sprint1.model.client

import com.example.gigmap_frontend_sprint1.model.response.WebService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val authInterceptor = Interceptor { chain ->
        val token = TokenManager.token
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl("https://gigmap-api.onrender.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }
}