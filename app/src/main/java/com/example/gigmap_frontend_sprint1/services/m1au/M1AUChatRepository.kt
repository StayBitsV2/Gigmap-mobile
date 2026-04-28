package com.example.gigmap_frontend_sprint1.services.m1au

import com.example.gigmap_frontend_sprint1.model.m1au.M1AUBotReply

class M1AUChatRepository(
    private val service: M1AUChatService = M1AUChatService()
) {
    suspend fun askM1AU(message: String, userId: Int? = null): Result<M1AUBotReply> {
        return service.sendMessage(message, userId)
    }
}
