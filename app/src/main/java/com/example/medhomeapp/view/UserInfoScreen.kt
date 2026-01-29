package com.example.medhomeapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.medhomeapp.R
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
            Text(stringResource(R.string.error_user_not_found), style = MaterialTheme.typography.bodyLarge)
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
                    text = u.name.ifEmpty { stringResource(R.string.label_unnamed_user) },
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

        InfoCard(title = stringResource(R.string.section_basic_information)) {
            InfoRow(stringResource(R.string.label_email), u.email)
            InfoRow(stringResource(R.string.label_contact), u.contact)
            InfoRow(stringResource(R.string.label_gender), u.gender)
            InfoRow(stringResource(R.string.label_age), u.dateOfBirth.toString())
        }

        if (u.role == "admin" || u.role == "staff") {

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(
                title = stringResource(R.string.section_sensitive_information),
                containerColor = MaterialTheme.colorScheme.errorContainer
            ) {
                InfoRow(stringResource(R.string.label_address), u.address)
                InfoRow(stringResource(R.string.label_blood_group), u.bloodGroup)
                InfoRow(stringResource(R.string.label_emergency_contact), u.emergencyContact)
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.message_sensitive_info_hidden),
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
            text = value.ifEmpty { stringResource(R.string.label_not_available) },
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}