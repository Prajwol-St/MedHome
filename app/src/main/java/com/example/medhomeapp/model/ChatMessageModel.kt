package com.example.medhomeapp.model

data class ChatMessage(
    val message: String = "",
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

