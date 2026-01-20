package com.example.medhomeapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.model.ChatMessage
import com.example.medhomeapp.viewmodel.AiHealthViewModel

class AiHealthAssistantActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setContent {
            MedHomeAppTheme {
                Scaffold { padding ->
                    AiHealthAssistantScreen(
                        userId = userId,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}



@Composable
fun AiHealthAssistantScreen(
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: AiHealthViewModel = viewModel()
) {
    var input by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        viewModel.startObserving(userId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1FBF9)) // same background as home
    ) {

        // 🌿 Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF7AD58B))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "AI Health Assistant",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "General health guidance",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
        }

        // ⚠️ Disclaimer
        Text(
            text = "⚠️ This AI provides general health information only.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(12.dp)
        )

        // 💬 Chat messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.messages) { msg ->
                ChatBubble(msg)
            }
        }

        // ✍️ Input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color.White, RoundedCornerShape(30.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Describe your symptoms…") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )


            )

            IconButton(
                onClick = {
                    if (input.isNotBlank()) {
                        viewModel.sendMessage(userId, input)
                        input = ""
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF7AD58B), CircleShape)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}



@Composable
fun ChatBubble(msg: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment =
            if (msg.isUser) Alignment.CenterEnd
            else Alignment.CenterStart
    ) {
        Surface(
            color =
                if (msg.isUser) Color(0xFF7AD58B)
                else Color.White,
            shape = RoundedCornerShape(14.dp),
            tonalElevation = 2.dp
        ) {
            Text(
                text = msg.message,
                modifier = Modifier.padding(12.dp),
                color =
                    if (msg.isUser) Color.White
                    else Color(0xFF2C3E50),
                fontSize = 14.sp
            )
        }
    }
}


