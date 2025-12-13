package com.example.medhomeapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medhomeapp.model.UserModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun UserInfoScreen(uid: String) {

    var user by remember { mutableStateOf<UserModel?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(uid)

        ref.get().addOnSuccessListener {
            user = it.getValue(UserModel::class.java)
            loading = false
        }
    }

    if (loading) {
        CircularProgressIndicator()
        return
    }

    if (user == null) {
        Text("User not found")
        return
    }

    Column(Modifier.padding(16.dp)) {
        Text("Name: ${user!!.name}")
        Text("Email: ${user!!.email}")
        Text("Role: ${user!!.role}")

        if (user!!.role == "admin" || user!!.role == "staff") {
            Divider()
            Text("Address: ${user!!.address}")
            Text("Blood Group: ${user!!.bloodGroup}")
        }
    }
}