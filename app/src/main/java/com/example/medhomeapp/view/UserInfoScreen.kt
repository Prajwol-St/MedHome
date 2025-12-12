package com.example.medhomeapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun UserInfoScreen(uid: String) {

    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("users").document(uid).get().await()

        if (snapshot.exists()) {
            userData = snapshot.data
        }
        loading = false
    }

    if (loading) {
        CircularProgressIndicator()
        return
    }

    val data = userData

    if (data == null) {
        Text("User not found.")
        return
    }

    val name = data["name"] ?: "Unknown"
    val email = data["email"] ?: "Unknown"
    val phone = data["phone"] ?: "Unknown"
    val role = data["role"] ?: "user"        // Example: admin, staff, user

    Column(modifier = Modifier.padding(20.dp)) {

        Text("User Information", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Name: $name")
        Text("Email: $email")
        Text("Phone: $phone")
        Text("Role: $role")

        Spacer(modifier = Modifier.height(20.dp))

        // 🔐 Sensitive Fields — Only Admin / Staff Can See
        if (role == "admin" || role == "staff") {
            Divider()
            Spacer(modifier = Modifier.height(10.dp))

            Text("🔐 Sensitive Data", style = MaterialTheme.typography.titleMedium)

            Text("Address: ${data["address"] ?: "N/A"}")
            Text("Citizenship No: ${data["citizenship_no"] ?: "N/A"}")
            Text("Medical ID: ${data["medical_id"] ?: "N/A"}")
        } else {
            Text(
                text = "Sensitive information hidden — unauthorized role.",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}