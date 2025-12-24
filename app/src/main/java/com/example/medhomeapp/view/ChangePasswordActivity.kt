package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChangePasswordScreen()
        }
    }
}

@Composable
fun ChangePasswordScreen() {
    val context = LocalContext.current

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { (context as ComponentActivity).finish() }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back",
                    tint = Color(0xFF648DDB)
                )
            }
            Text(
                text = "Change Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF648DDB)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            enabled = !isLoading,
            trailingIcon = {
                IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                    Icon(
                        painter = if (currentPasswordVisible)
                            painterResource(R.drawable.baseline_visibility_off_24)
                        else
                            painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (currentPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF648DDB),
                unfocusedIndicatorColor = Color(0xFF648DDB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            enabled = !isLoading,
            trailingIcon = {
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                    Icon(
                        painter = if (newPasswordVisible)
                            painterResource(R.drawable.baseline_visibility_off_24)
                        else
                            painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (newPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF648DDB),
                unfocusedIndicatorColor = Color(0xFF648DDB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm New Password") },
            enabled = !isLoading,
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        painter = if (confirmPasswordVisible)
                            painterResource(R.drawable.baseline_visibility_off_24)
                        else
                            painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF648DDB),
                unfocusedIndicatorColor = Color(0xFF648DDB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                when {
                    currentPassword.isBlank() -> {
                        Toast.makeText(context, "Enter current password", Toast.LENGTH_SHORT).show()
                    }
                    newPassword.isBlank() -> {
                        Toast.makeText(context, "Enter new password", Toast.LENGTH_SHORT).show()
                    }
                    newPassword.length < 6 -> {
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    }
                    newPassword != confirmPassword -> {
                        Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        isLoading = true
                        val user = FirebaseAuth.getInstance().currentUser
                        val email = user?.email

                        if (user != null && email != null) {
                            // Re-authenticate user
                            val credential = EmailAuthProvider.getCredential(email, currentPassword)
                            user.reauthenticate(credential)
                                .addOnCompleteListener { reauth ->
                                    if (reauth.isSuccessful) {
                                        // Update password
                                        user.updatePassword(newPassword)
                                            .addOnCompleteListener { update ->
                                                isLoading = false
                                                if (update.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "Password changed successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    (context as ComponentActivity).finish()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to update password",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Current password is incorrect",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            isLoading = false
                            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF648DDB))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Change Password",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}