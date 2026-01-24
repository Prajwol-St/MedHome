package com.example.medhomeapp.view

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medhomeapp.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.Locale

// ---------------- DATA ----------------

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqChatCompletionRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val max_tokens: Int = 512
)

data class GroqChatCompletionResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: GroqMessage
)

// ---------------- API ----------------

interface GroqApiService {

    @Headers("Content-Type: application/json")
    @POST("openai/v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: GroqChatCompletionRequest
    ): Response<GroqChatCompletionResponse>
}

// ---------------- ACTIVITY ----------------

class ChatbotActivity : ComponentActivity() {

    private val groqApiService: GroqApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApiService::class.java)
    }

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tts = TextToSpeech(this) {
            tts.language = Locale.US
        }

        setContent {
            MaterialTheme {
                ChatScreen(
                    api = groqApiService,
                    tts = tts,
                    onBack = { finish() }
                )
            }
        }
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}

// ---------------- UI ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    api: GroqApiService,
    tts: TextToSpeech,
    onBack: () -> Unit
) {
    val systemPrompt =
        "You are MedGuide, an AI medical assistant. Always remind users you are not a doctor."

    val messages = remember { mutableStateListOf<GroqMessage>() }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        messages.add(
            GroqMessage(
                "assistant",
                "👋 Hi! I’m MedGuide.\nI’m not a doctor, but I can help with medical questions."
            )
        )
    }

    fun send() {
        if (input.isBlank() || loading) return

        val userText = input.trim()
        messages.add(GroqMessage("user", userText))
        input = ""
        loading = true

        scope.launch(Dispatchers.IO) {
            try {
                val request = GroqChatCompletionRequest(
                    model = "llama-3.1-8b-instant",
                    messages = listOf(
                        GroqMessage("system", systemPrompt)
                    ) + messages
                )

                val response = api.getChatCompletion(
                    "Bearer ${BuildConfig.GROQ_API_KEY}",
                    request
                )

                if (response.isSuccessful) {
                    val reply = response.body()
                        ?.choices
                        ?.firstOrNull()
                        ?.message
                        ?.content
                        ?.trim()

                    if (!reply.isNullOrEmpty()) {
                        messages.add(GroqMessage("assistant", reply))
                        tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                } else {
                    messages.add(
                        GroqMessage("assistant", "⚠️ ${response.code()} — Invalid request")
                    )
                }
            } catch (e: Exception) {
                messages.add(GroqMessage("assistant", "⚠️ Network error"))
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.lastIndex)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MedGuide", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },

        // ✅ BOTTOM SCAFFOLDING (NOT NAV BAR)
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask a medical question…") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )

                    Spacer(Modifier.width(10.dp))

                    IconButton(
                        onClick = { send() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        enabled = !loading
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF4F7FB)),
            state = listState,
            contentPadding = PaddingValues(14.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(msg)
            }

            item {
                AnimatedVisibility(loading) {
                    Text(
                        "MedGuide is typing…",
                        color = Color.Gray,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

// ---------------- CHAT BUBBLE ----------------

@Composable
fun ChatBubble(message: GroqMessage) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) Color(0xFF1976D2) else Color.White,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp
            ),
            tonalElevation = 2.dp
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(14.dp).widthIn(max = 300.dp),
                color = if (isUser) Color.White else Color.Black,
                fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}
