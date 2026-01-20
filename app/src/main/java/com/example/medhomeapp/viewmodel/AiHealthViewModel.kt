package com.example.medhomeapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medhomeapp.model.ChatMessage
import com.example.medhomeapp.repository.AiChatRepository
import com.example.medhomeapp.repository.AiChatRepositoryImpl
import kotlinx.coroutines.launch

class AiHealthViewModel(
    private val repository: AiChatRepository =
        AiChatRepositoryImpl()
) : ViewModel() {

    val messages = mutableStateListOf<ChatMessage>()

    fun startObserving(userId: String) {
        repository.observeMessages(userId) {
            messages.clear()
            messages.addAll(it)
        }
    }

    fun sendMessage(userId: String, text: String) {
        val userMessage = ChatMessage(
            message = text,
            isUser = true
        )

        viewModelScope.launch {
            repository.sendUserMessage(userId, userMessage)

            try {
                repository.sendAiMessage(userId, text)
            } catch (e: Exception) {
                repository.sendUserMessage(
                    userId,
                    ChatMessage(
                        "AI service is currently unavailable.",
                        false
                    )
                )
            }
        }
    }
}
