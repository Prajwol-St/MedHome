package com.example.medhomeapp.repository

import com.example.medhomeapp.model.ChatMessage
import com.example.medhomeapp.remote.AiApiClient
import com.example.medhomeapp.remote.AiApiService
import com.example.medhomeapp.remote.AiRequest
import com.google.firebase.firestore.FirebaseFirestore

class AiChatRepositoryImpl(
    private val api: AiApiService = AiApiClient.api,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AiChatRepository {

    override fun observeMessages(
        userId: String,
        onUpdate: (List<ChatMessage>) -> Unit
    ) {
        db.collection("ai_chats")
            .document(userId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    onUpdate(it.toObjects(ChatMessage::class.java))
                }
            }
    }

    override suspend fun sendUserMessage(
        userId: String,
        message: ChatMessage
    ) {
        db.collection("ai_chats")
            .document(userId)
            .collection("messages")
            .add(message)
    }

    override suspend fun sendAiMessage(
        userId: String,
        userText: String
    ) {
        val response = api.sendMessage(AiRequest(userText))

        val aiMessage = ChatMessage(
            message = response.reply,
            isUser = false
        )

        sendUserMessage(userId, aiMessage)
    }
}
