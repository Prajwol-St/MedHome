package com.example.medhomeapp.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AiApiService {
    @POST("chat")
    suspend fun sendMessage(
        @Body request: AiRequest
    ): AiResponse
}

data class AiRequest(val message: String)
data class AiResponse(val reply: String)
