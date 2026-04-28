package com.example.gigmap_frontend_sprint1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigmap_frontend_sprint1.model.m1au.M1AUBotReply
import com.example.gigmap_frontend_sprint1.model.m1au.M1AUConcertCard
import com.example.gigmap_frontend_sprint1.services.m1au.M1AUChatRepository
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean
)

data class M1AUChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val concerts: List<M1AUConcertCard> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class M1AUChatViewModel(
    private val repository: M1AUChatRepository = M1AUChatRepository()
) : ViewModel() {

    var uiState by mutableStateOf(M1AUChatUiState())
        private set

    fun sendMessage(message: String, userId: Int? = null) {
        if (message.isBlank() || uiState.isLoading) return

        val updatedMessages = uiState.messages + ChatMessage(text = message.trim(), isUser = true)
        uiState = uiState.copy(messages = updatedMessages, isLoading = true, error = null)

        viewModelScope.launch {
            val result = repository.askM1AU(message.trim(), userId)
            uiState = result.fold(
                onSuccess = { reply -> uiStateSuccess(reply, updatedMessages) },
                onFailure = { throwable ->
                    uiState.copy(
                        isLoading = false,
                        error = throwable.message ?: "Ups, algo salió mal. Intenta más tarde."
                    )
                }
            )
        }
    }

    private fun uiStateSuccess(reply: M1AUBotReply, currentMessages: List<ChatMessage>): M1AUChatUiState {
        val botMessage = ChatMessage(text = reply.message, isUser = false)
        return uiState.copy(
            messages = currentMessages + botMessage,
            concerts = reply.concerts,
            isLoading = false,
            error = null
        )
    }

    fun dismissError() {
        uiState = uiState.copy(error = null)
    }
}
