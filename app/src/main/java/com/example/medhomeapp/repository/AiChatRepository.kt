package com.example.medhomeapp.repository

import com.example.medhomeapp.model.ChatMessage

interface AiChatRepository {

    fun observeMessages(
        userId: String,
        onUpdate: (List<ChatMessage>) -> Unit
    )

    suspend fun sendUserMessage(
        userId: String,
        message: ChatMessage
    )

    suspend fun sendAiMessage(
        userId: String,
        userText: String
    )
}
