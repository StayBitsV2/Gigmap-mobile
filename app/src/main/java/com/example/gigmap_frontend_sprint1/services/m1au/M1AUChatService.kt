package com.example.gigmap_frontend_sprint1.services.m1au

import com.example.gigmap_frontend_sprint1.model.m1au.M1AUBotReply
import com.example.gigmap_frontend_sprint1.model.m1au.M1AUConcertCard
import com.example.gigmap_frontend_sprint1.services.m1au.dto.M1AUChatRequestDto
import com.example.gigmap_frontend_sprint1.services.m1au.dto.M1AUChatResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class M1AUChatService(
    private val api: M1AUService = M1AUApiClient.retrofit.create(M1AUService::class.java)
) {
    suspend fun sendMessage(message: String, userId: Int? = null): Result<M1AUBotReply> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.sendMessage(M1AUChatRequestDto(message = message, userId = userId))
            response.toDomain()
        }
    }
}

private fun M1AUChatResponseDto.toDomain(): M1AUBotReply {
    val cards = concerts.map {
        M1AUConcertCard(
            id = it.id,
            title = it.title,
            description = it.description,
            imageUrl = it.imageUrl,
            formattedDate = it.formattedDate,
            artistId = it.artist?.id,
            artistName = it.artist?.name,
            artistUsername = it.artist?.username,
            venueName = it.venue?.name,
            venueAddress = it.venue?.address,
            platformName = it.platform?.name
        )
    }

    return M1AUBotReply(
        message = message,
        concerts = cards,
        total = meta.total,
        hasResults = meta.hasResults
    )
}
