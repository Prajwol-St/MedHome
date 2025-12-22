package com.example.medhomeapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.medhomeapp.model.UserModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@Composable
fun UserInfoScreen(uid: String, viewerRole: String) {


    var user by remember { mutableStateOf<UserModel?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(uid)

        ref.get().addOnSuccessListener {
            user = it.getValue(UserModel::class.java)
            loading = false
        }.addOnFailureListener {
            loading = false
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("❌ User not found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val u = user!!

    val canViewSensitiveInfo = viewerRole == "admin" || viewerRole == "staff"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = u.name.ifEmpty { "Unnamed User" },
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (canViewSensitiveInfo) {
                    AssistChip(
                        onClick = {},
                        label = { Text(u.role.uppercase()) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        /* ---------- BASIC INFO ---------- */
        InfoCard(title = "Basic Information") {
            InfoRow("Email", u.email)
            InfoRow("Contact", u.contact)
            InfoRow("Gender", u.gender)
            InfoRow("Age", u.dateOfBirth.toString())
        }

        /* ---------- ADMIN / STAFF ONLY ---------- */
        if (u.role == "admin" || u.role == "staff") {

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(
                title = "Sensitive Information",
                containerColor = MaterialTheme.colorScheme.errorContainer
            ) {
                InfoRow("Address", u.address)
                InfoRow("Blood Group", u.bloodGroup)
                InfoRow("Emergency Contact", u.emergencyContact)
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "🔒 Sensitive information hidden",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}


@Composable
fun InfoCard(
    title: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value.ifEmpty { "N/A" },
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}