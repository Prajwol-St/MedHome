package com.example.medhomeapp.view

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BuildConfig
import com.example.medhomeapp.view.ui.theme.MintGreen
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
    val systemPrompt = "You are MedGuide, an AI medical assistant. Always remind users you are not a doctor."
    val messages = remember { mutableStateListOf<GroqMessage>() }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(GroqMessage("assistant", "👋 Hi! I’m MedGuide.\nI’m not a doctor, but I can help with medical questions."))
        }
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
                    messages = listOf(GroqMessage("system", systemPrompt)) + messages
                )
                val response = api.getChatCompletion("Bearer ${BuildConfig.GROQ_API_KEY}", request)

                if (response.isSuccessful) {
                    val reply = response.body()?.choices?.firstOrNull()?.message?.content?.trim()
                    if (!reply.isNullOrEmpty()) {
                        messages.add(GroqMessage("assistant", reply))
                        tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                } else {
                    messages.add(GroqMessage("assistant", "⚠️ Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                messages.add(GroqMessage("assistant", "⚠️ Network error"))
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        "MedGuide AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask a medical question...", color = Color.Gray) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color(0xFFF9F9F9),
                            unfocusedContainerColor = Color(0xFFF9F9F9)
                        ),
                        maxLines = 4
                    )

                    Spacer(Modifier.width(12.dp))

                    IconButton(
                        onClick = { send() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (input.isNotBlank()) MintGreen else Color.LightGray,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5)) // Light grey background like Booking screen
        ) {
            if (loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MintGreen,
                    trackColor = MintGreen.copy(alpha = 0.1f)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: GroqMessage) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MintGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Face, contentDescription = null, tint = MintGreen, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(8.dp))
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MintGreen else Color.White
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = if (isUser) 2.dp else 16.dp,
                bottomStart = if (isUser) 16.dp else 2.dp
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = if (!isUser) BorderStroke(1.dp, Color(0xFFEEEEEE)) else null
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp).widthIn(max = 260.dp),
                color = if (isUser) Color.White else Color(0xFF2C3E50),
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }

        if (isUser) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}
