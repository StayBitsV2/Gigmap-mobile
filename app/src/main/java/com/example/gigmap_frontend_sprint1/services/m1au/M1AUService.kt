package com.example.gigmap_frontend_sprint1.services.m1au

import com.example.gigmap_frontend_sprint1.services.m1au.dto.M1AUChatRequestDto
import com.example.gigmap_frontend_sprint1.services.m1au.dto.M1AUChatResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface M1AUService {
    @POST("chat")
    suspend fun sendMessage(@Body request: M1AUChatRequestDto): M1AUChatResponseDto
}
